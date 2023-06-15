package it.unibo.pcd.akka.basics.e01hello

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, ActorSystem, Behavior, Terminated}

object Killable:
  def apply(): Behavior[String] =
    Behaviors.receiveMessage[String] {
      case "kill" => Behaviors.stopped
      case _ => Behaviors.same
    }

object Root:
  def apply(): Behavior[String] =
    Behaviors.setup[String] { context =>
      val child = context.spawn(Killable(), "Killable")
      context.watch(child)
      child ! "kill"
      Behaviors.receiveSignal { case (_, Terminated(_)) =>
        context.log.info("Child terminated")
        Behaviors.stopped
      }
    }
@main def killExample() =
  ActorSystem(Root(), "Root")
