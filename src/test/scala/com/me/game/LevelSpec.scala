package com.me.game

import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers
import java.security.InvalidParameterException

class LevelSpec extends FlatSpec with ShouldMatchers {

  "A Level" should "be created" in {
    new Level(Level.Info(0,5,5,20),Level.Bounds(200,200,0,0))
  }
}
