package shared

object Protocol {

  sealed trait ProtocolBase
  case class Hello(message: String) extends ProtocolBase
  case class Error(message: String) extends ProtocolBase

}
