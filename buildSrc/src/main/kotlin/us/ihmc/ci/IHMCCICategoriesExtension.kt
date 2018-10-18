package us.ihmc.ci

import org.gradle.api.Project

open class IHMCCICategoriesExtension(private val project: Project)
{
   val categories = hashMapOf<String, IHMCCICategory>()

   fun create(name: String, configuration: IHMCCICategory.() -> Unit)
   {
      val category = IHMCCICategory(name, project)
      configuration.invoke(category)
      categories.put(name, category)
   }
}