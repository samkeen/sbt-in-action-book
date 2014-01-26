// Working through SBT In Action, i.e. Lots of comments

name := "preowned-kittens"

version := "1.0.0"

// #############################################################
// Define Tasks

// below is a Task used to generate a version properties file
//                                              task desc in sbt console
val makeVersionProperties = taskKey[Seq[File]]("Makes a version properties file")

// This is a definition in sbt. A Definition defines a variable or method for reuse within sbt settings.
val gitHeadCommitSha = taskKey[String]("Determine the current git commit SHA")

// Note on := vs =
//   The way to think of this process is that the definition (=) is constructing a new slot where computed build
// values can go. The setting (:=) is constructing a function that will compute the value for the slot when needed.
//   Unlike a lot of modern build tools, sbt separates defining the computation of a value from the slot that
// stores the value. This is to aid in parallel execution of builds.

// 'in ThisBuild' defines the Task on the build itself (rather than a Project).  This way
// all Projects have access to it
// If sbt does not find a task/setting for a key in a project, it will
// use the one defined in the build (is found).
gitHeadCommitSha in ThisBuild := Process("git rev-parse HEAD").lines.head

// ###########################################################
// Project Definitions

// helper function: returns a new sbt.Project object where the location and name are the same
def PreownedKittenProject(name: String): Project = (
  Project(name, file(name))
  settings(
    version := "1.0",
    organization := "com.preownedkittens",
    libraryDependencies ++= Seq(
      "org.specs2" % "specs2_2.10" % "1.14" % "test"
    )
  )
)

// Project definition order matters! Just as any other Scala
// object, any values defined cannot be referenced before they are declared.
// Always make these lazy vals

// > projects
//  [info] In file:/Users/sam/Projects/kittens/
//  [info] 	   analytics
//  [info] 	   common
//  [info] 	 * kittens
//  [info] 	   website

// SBT automatically generates a root project. This is a default project defined against
// the root directory of the build, in this case the “kittens” directory
// For multi-module projects it is bad practice to place code in the root project.  rather
// place it all in cohesive sub-projects.

lazy val common = (
  PreownedKittenProject("common")
    settings( // define additional setting for this project beyond what is in PreownedKittenProject
      makeVersionProperties := {
        val propFile = new File((resourceManaged in Compile).value, version.value)
        val content = "version=%s" format (gitHeadCommitSha.value)
        IO.write(propFile, content)
        Seq(propFile)
      },
      // Finally, we need to tell sbt to include this properties file in the runtime classpath for our website.
      // Add our resource into the Production (Compile) config (as opposed to the Test config)
      //
      // Configurations are one namespacing strategy, SubProjects are another.
      //
      // other (Popular) Configurations:
      //   * Test (compile & run testing code)
      //   * Runtime (used to run project w/ SBT)
      //   * IntegrationTest (run tests against Prod artifacts)
      //
      // Appends out task to the list of Tasks to generate resources
      // resourceGenerators is a SettingsKey containing a list of Tasks
      resourceGenerators in Compile <+= makeVersionProperties
    )
  )

//
lazy val analytics = (
  PreownedKittenProject("analytics")
    dependsOn("common")
    settings() // define additional setting for this project beyond what is in PreownedKittenProject
  )

//
lazy val website = (
  PreownedKittenProject("website")
    dependsOn("common")
    settings() // define additional setting for this project beyond what is in PreownedKittenProject
)




