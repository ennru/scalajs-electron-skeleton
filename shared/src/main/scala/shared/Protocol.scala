package shared

object Protocol {

  sealed trait Message
  case class Hello(message: String)

}
