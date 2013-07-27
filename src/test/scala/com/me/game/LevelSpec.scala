package com.me.game

import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers
import java.security.InvalidParameterException

class LevelSpec extends FlatSpec with ShouldMatchers {

  "A Level" should "be created" in {
    //new Level(Level.Info(0,5,5))
  }

  it should "throw InvalidParameterException if an argument is 0" in {
    //evaluating { new Level(0,0) } should produce [InvalidParameterException]
  }
}
