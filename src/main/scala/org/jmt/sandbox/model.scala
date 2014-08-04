package org.jmt.sandbox

import java.util.UUID

object Model {
  case class Project(name: String, status: String, notes: List[String])
}

object Commands {
  sealed trait ProjectCommand
  case class CreateProject(name: String) extends ProjectCommand
  case class SetProjectStatus(status: String) extends ProjectCommand
  case class AddNote(note: String) extends ProjectCommand

  case class Envelope[T <: ProjectCommand](clientId: String, projectId: UUID, command: T)
  case class ProjectCommandResult(clientId: String, projectId: UUID, error: Option[String])
}

object Queries {
  sealed trait ProjectQuery
  case object Get extends ProjectQuery
  
  case class Envelope[T <: ProjectQuery](clientId: String, projectId: UUID, query: T)
}



