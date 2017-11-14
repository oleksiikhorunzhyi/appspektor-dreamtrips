package com.worldventures.social.dummy

import com.worldventures.social.BaseSpec
import org.jetbrains.spek.api.dsl.context
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import kotlin.test.assertTrue

class DummySpec : BaseSpec({
   describe("Dummy Tests") {
      context("Dummy Context") {
         it("should always be true") {
            assertTrue { true }
         }
      }
   }
})