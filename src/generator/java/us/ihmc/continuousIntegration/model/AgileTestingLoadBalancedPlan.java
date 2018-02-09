package us.ihmc.continuousIntegration.model;

import us.ihmc.commons.Conversions;
import us.ihmc.commons.MathTools;
import us.ihmc.commons.PrintTools;
import us.ihmc.continuousIntegration.IntegrationCategory;

import java.nio.file.Path;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AgileTestingLoadBalancedPlan
{
   private IntegrationCategory bambooPlanType;
   private AgileTestingProject bambooEnabledProject;

   private Map<String, List<Path>> loadBalancedTests = new HashMap<>();
   private Map<String, Double> loadBalancedDurations = new HashMap<>();

   private ArrayList<AgileTestingTestSuiteFile> testSuiteFiles = new ArrayList<AgileTestingTestSuiteFile>();

   private String currentLetter = "A";

   private int numberOfTests = 0;

   public AgileTestingLoadBalancedPlan(IntegrationCategory bambooPlanType, AgileTestingProject bambooEnabledProject)
   {
      this.bambooPlanType = bambooPlanType;
      this.bambooEnabledProject = bambooEnabledProject;
   }

   public void add(AgileTestingTestClass bambooTestClass)
   {
      if (bambooPlanType == IntegrationCategory.FAST && bambooTestClass.getTotalDurationForTarget(bambooPlanType) > Conversions
            .minutesToSeconds(bambooEnabledProject.getConfiguration().getMaximumSuiteDuration()))
      {
         PrintTools.warn(this, "classTotalDuration > MAXIMUM_SUITE_DURATION in Fast: " + bambooTestClass.getTestClassSimpleName() + " (" + MathTools
               .roundToSignificantFigures(Conversions.secondsToMinutes(bambooTestClass.getTotalDurationForTarget(bambooPlanType)), 2) + " m)");
      }
      if (bambooPlanType == IntegrationCategory.SLOW && bambooTestClass.getTotalDurationForTarget(bambooPlanType) < Conversions
            .minutesToSeconds(bambooEnabledProject.getConfiguration().getRecommendedTestClassDuration()))
      {
         PrintTools.warn(this,
                         "classTotalDuration < RECOMMENDED_TEST_CLASS_DURATION in Slow. Consider moving to Fast: " + bambooTestClass.getTestClassSimpleName()
                               + " (" + MathTools
                               .roundToSignificantFigures(Conversions.secondsToMinutes(bambooTestClass.getTotalDurationForTarget(bambooPlanType)), 2) + " m)");
      }

      if (loadBalancedDurations.containsKey(currentLetter)
            && (loadBalancedDurations.get(currentLetter) + bambooTestClass.getTotalDurationForTarget(bambooPlanType)) > Conversions
            .minutesToSeconds(bambooEnabledProject.getConfiguration().getMaximumSuiteDuration()))
      {
         currentLetter = incrementCurrentLetter(currentLetter);
      }

      addTestClassToCurrentLetterJob(bambooTestClass);
   }

   private void addTestClassToCurrentLetterJob(AgileTestingTestClass bambooTestClass)
   {
      ensureMapsAreReadyForNewJobInLetter();

      loadBalancedTests.get(currentLetter).add(bambooTestClass.getPath());
      loadBalancedDurations.put(currentLetter, loadBalancedDurations.get(currentLetter) + bambooTestClass.getTotalDurationForTarget(bambooPlanType));

      numberOfTests++;
   }

   private void ensureMapsAreReadyForNewJobInLetter()
   {
      if (!loadBalancedTests.containsKey(currentLetter))
      {
         loadBalancedTests.put(currentLetter, new ArrayList<Path>());
         loadBalancedDurations.put(currentLetter, 0.0);
      }
   }

   public void loadTestSuites()
   {
      processLoadBalancedTests(false);
   }

   public void generateTestSuites()
   {
      processLoadBalancedTests(true);
   }

   private void processLoadBalancedTests(boolean generateTestSuites)
   {
      for (String letter : loadBalancedTests.keySet())
      {
         String testSuiteSimpleName = bambooEnabledProject.getModifiedProjectName() + letter + bambooPlanType.getName() + "TestSuite";
         Path suitePath = bambooEnabledProject.getGeneratedTestSuitesDirectory().resolve(testSuiteSimpleName + ".java");
         String packageName = bambooEnabledProject.getPackageName();
         List<Path> testPathsToPutInTestSuite = loadBalancedTests.get(letter);
         double durationInMinutes = Conversions.secondsToMinutes(loadBalancedDurations.get(letter));
         String formattedDuration = new DecimalFormat("0.0").format(durationInMinutes);

         String planShortName = testSuiteSimpleName.replaceAll("TestSuite", "");

         AgileTestingTestSuiteFile bambooTestSuiteFile = new AgileTestingTestSuiteFile(suitePath, bambooPlanType, planShortName,
                                                                                       loadBalancedDurations.get(letter));

         testSuiteFiles.add(bambooTestSuiteFile);

         if (generateTestSuites)
         {
            PrintTools.info(this, "Generating: " + "(" + formattedDuration + " min) " + planShortName);

            bambooTestSuiteFile.generateTestSuite(testSuiteSimpleName, packageName, testPathsToPutInTestSuite);
         }
         else
         {
            PrintTools.info(this, "Loading: " + "(" + formattedDuration + " min) " + planShortName);
         }
      }
   }

   public int getNumberOfTests()
   {
      return numberOfTests;
   }

   public IntegrationCategory getBambooPlanType()
   {
      return bambooPlanType;
   }

   public Map<String, List<Path>> getLoadBalancedTests()
   {
      return loadBalancedTests;
   }

   public Map<String, Double> getLoadBalancedDurations()
   {
      return loadBalancedDurations;
   }

   public ArrayList<AgileTestingTestSuiteFile> getTestSuiteFiles()
   {
      return testSuiteFiles;
   }

   private static String incrementCurrentLetter(String currentLetter)
   {
      int indexLastLetter = currentLetter.length() - 1;
      if (currentLetter.charAt(indexLastLetter) < 'Z')
      {
         currentLetter = currentLetter.substring(0, indexLastLetter) + String.valueOf((char) (currentLetter.charAt(indexLastLetter) + 1));
      }
      else if (currentLetter.charAt(indexLastLetter) >= 'Z')
      {
         if (currentLetter.length() == 1)
         {
            currentLetter = "AA";
         }
         else
         {
            currentLetter = String.valueOf((char) (currentLetter.charAt(0) + 1)) + "A";
         }
      }
      return currentLetter;
   }

   public static void main(String[] args)
   {
      System.out.println(incrementCurrentLetter("A"));
      System.out.println(incrementCurrentLetter("C"));
      System.out.println(incrementCurrentLetter("Z"));
      System.out.println(incrementCurrentLetter("AA"));
      System.out.println(incrementCurrentLetter("AB"));
      System.out.println(incrementCurrentLetter("ZA"));
      System.out.println(incrementCurrentLetter("ZZ"));
   }
}