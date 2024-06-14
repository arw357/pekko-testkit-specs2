package org.arw357.pekko.testkit.specs2.mutable

import org.apache.pekko.testkit.TestKitBase
import org.arw357.pekko.testkit.specs2.PekkoMatchers
import org.specs2.mutable.SpecificationLike
import org.specs2.specification.AfterAll

trait PekkoSpecificationLike extends TestKitBase with SpecificationLike with PekkoMatchers with AfterAll {
  def afterAll(): Unit = shutdown()
}
