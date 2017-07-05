package us.ihmc.continuousIntegration.codeQuality;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import us.ihmc.commons.MathTools;
import us.ihmc.commons.PrintTools;
import us.ihmc.continuousIntegration.ContinuousIntegrationAnnotations.ContinuousIntegrationPlan;
import us.ihmc.continuousIntegration.ContinuousIntegrationAnnotations.ContinuousIntegrationTest;
import us.ihmc.continuousIntegration.model.AgileTestingClassPath;
import us.ihmc.continuousIntegration.model.AgileTestingProject;
import us.ihmc.continuousIntegration.model.AgileTestingTestClass;
import us.ihmc.continuousIntegration.model.AgileTestingTestMethod;
import us.ihmc.continuousIntegration.tools.SourceTools;
import us.ihmc.continuousIntegration.AgileTestingProjectLoader;
import us.ihmc.continuousIntegration.AgileTestingTools;
import us.ihmc.continuousIntegration.IntegrationCategory;

@ContinuousIntegrationPlan(categories = IntegrationCategory.HEALTH)
public class CodeStatisticsCodeQualityTest
{
   private static final double PERCENTAGE_OF_UNFINISHED_TESTS_THRESHOLD = 10.0;
   
	@ContinuousIntegrationTest(estimatedDuration = 16.8)
   @Test(timeout = 84000)
   public void testPercentOfTestClassesThatAreUnfinishedIsLessThanThreshold()
   {
      int numberOfTestClasses = 0;
      int numberOfTests = 0;
      Map<IntegrationCategory, Integer> numberOfTestsInTargets = new HashMap<>();
      for (IntegrationCategory category : IntegrationCategory.values)
      {
         numberOfTestsInTargets.put(category, 0);
      }

      final Map<String, AgileTestingClassPath> nameToPathMap = AgileTestingTools.mapAllClassNamesToClassPaths(SourceTools.getWorkspacePath());
      Map<String, AgileTestingProject> bambooEnabledProjects = AgileTestingTools.loadATProjects(new AgileTestingProjectLoader()
      {
         @Override
         public boolean meetsCriteria(AgileTestingProject atProject)
         {
            return atProject.isBambooEnabled();
         }
         
         @Override
         public void setupProject(AgileTestingProject atProject)
         {
            atProject.loadTestCloud(nameToPathMap);
         }
      }, SourceTools.getWorkspacePath());
      for (AgileTestingProject bambooEnabledProject : bambooEnabledProjects.values())
      {
         for (AgileTestingTestClass bambooTestClass : bambooEnabledProject.getTestCloud().getTestClasses())
         {
            numberOfTestClasses++;
            
            for (AgileTestingTestMethod testMethod : bambooTestClass.getTestMethods())
            {
               numberOfTests++;
               
               for (IntegrationCategory integrationCategory : testMethod.getCategories())
               {
                  numberOfTestsInTargets.put(integrationCategory, numberOfTestsInTargets.get(integrationCategory) + 1);
               }
            }
         }
      }
      
      PrintTools.info(this, "Number of test classes: " + numberOfTestClasses);
      PrintTools.info(this, "Number of tests: " + numberOfTests);
      
      for (IntegrationCategory category : IntegrationCategory.values)
      {
         PrintTools.info(this, "Number of tests in " + category.getName() + ": " + numberOfTestsInTargets.get(category));
      }
      
      int numberOfUnfinishedTests = numberOfTestsInTargets.get(IntegrationCategory.EXCLUDE) + numberOfTestsInTargets.get(IntegrationCategory.IN_DEVELOPMENT) + numberOfTestsInTargets.get(IntegrationCategory.FLAKY);
      PrintTools.info(this, "Number of tests in Exclude, InDevelopment, Flaky (Unfinished): " + numberOfUnfinishedTests);
      double perecentageOfTestClassesThatAreUnfinished = (double) numberOfUnfinishedTests / (double) numberOfTests * 100.0;
      
      PrintTools.info(this, "Percentage of unfinished test classes: " + MathTools.roundToSignificantFigures(perecentageOfTestClassesThatAreUnfinished, 2) + " %");
      
      Assert.assertFalse("Percentage of unfinished test classes is greater than " + PERCENTAGE_OF_UNFINISHED_TESTS_THRESHOLD + " %.", perecentageOfTestClassesThatAreUnfinished > PERCENTAGE_OF_UNFINISHED_TESTS_THRESHOLD);
   }
}
