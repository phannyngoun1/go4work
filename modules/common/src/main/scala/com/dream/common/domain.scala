package com.dream.common

object domain {

  trait Type {

    def value: String

    def name: String

    def description: Option[String]

  }

  trait ActivityTracking {

  }

}
