name := "preowned-kittens"

version := "1.0.0"

// This is a definition in sbt. A Definition defines a variable or method for reuse within sbt settings.
val gitHeadCommitSha = taskKey[String](
  "Determine the current git commit SHA")
// The way to think of this process is that the definition (=) is constructing a new slot where computed build
// values can go. The setting (:=) is constructing a function that will compute the value for the slot when needed.
//
// Unlike a lot of modern build tools, sbt separates defining the computation of a value from the slot that
// stores the value. This is to aid in parallel execution of builds.
gitHeadCommitSha := Process("git rev-parse HEAD").lines.head

libraryDependencies ++= Seq(
  "org.specs2" % "specs2_2.10" % "1.14" % "test"//,
//  "junit" % "junit" % "4.11" % "test"
)




