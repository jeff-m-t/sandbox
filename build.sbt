import AssemblyKeys._

organization  := "org.jmt"

name := "sandbox"

scalaVersion  := "2.11.2"

scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8")

libraryDependencies ++= {
  val akkaV = "2.3.4"
  val sprayV = "1.3.1"
  Seq(
    "io.spray"            %%  "spray-can"         % sprayV,
    "io.spray"            %%  "spray-routing"     % sprayV,
    "io.spray"            %%  "spray-client"      % sprayV,
    "io.spray"            %%  "spray-testkit"     % sprayV  % "test",
    "com.typesafe.akka"   %%  "akka-actor"        % akkaV,
    "com.typesafe.akka"   %%  "akka-slf4j"        % akkaV,
    "com.typesafe.akka"   %% "akka-persistence-experimental" % akkaV,
    "com.typesafe.akka"   %%  "akka-testkit"      % akkaV   % "test",
    "org.scala-lang.modules" %% "scala-async"     % "0.9.2",
    "org.json4s"          %%  "json4s-jackson"    % "3.2.10",
    "org.specs2"          %%  "specs2-core"       % "2.3.13" % "test",
    "ch.qos.logback"      %   "logback-classic"   % "1.1.2"
  )
}

Revolver.settings

releaseSettings

ReleaseKeys.crossBuild := false

ReleaseKeys.versionBump := sbtrelease.Version.Bump.Minor

assemblySettings

addArtifact(Artifact("aggregator", "assembly"), sbtassembly.Plugin.AssemblyKeys.assembly)

mainClass in assembly := Some("org.jmt.sandbox.Boot")
