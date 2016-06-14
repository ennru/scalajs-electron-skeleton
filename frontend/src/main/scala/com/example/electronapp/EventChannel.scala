package com.example.electronapp

trait Receiver[T] {
  def onMessage(msg: T): Unit
}

class EventChannel[T] extends Receiver[T] {

  private var subscribers: Set[Receiver[T]] = Set.empty

  def subscribe(r: Receiver[T]): Unit = {
    subscribers = subscribers + r
  }

  def unsubscribe(r: Receiver[T]): Unit = {
    subscribers = subscribers - r
  }

  def onMessage(msg: T): Unit = subscribers.foreach(_.onMessage(msg))

}

case class BidirChannel[T](in: EventChannel[T], out: EventChannel[T])