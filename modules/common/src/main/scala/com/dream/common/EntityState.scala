package com.dream.common

import java.util.UUID

trait EntityState[Error ,T] {

  protected def foreachState(f: (T) => Unit): Unit

  protected def equalsId(id: UUID)(state: Option[T], f: (T) => Boolean): Boolean =
    state match {
      case None =>
        throw new IllegalStateException(s"Invalid state: requestId = ${id.toString}")
      case Some(state) =>
        f(state)
    }

  protected def mapState(
    f: (T) => Either[Error, T]
  ): Either[Error, T]

}
