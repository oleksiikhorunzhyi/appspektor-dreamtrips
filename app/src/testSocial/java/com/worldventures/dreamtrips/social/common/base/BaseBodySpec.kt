package com.worldventures.dreamtrips.social.common.base

import org.jetbrains.spek.api.Spek
import org.junit.platform.runner.JUnitPlatform
import org.junit.runner.RunWith

@RunWith(JUnitPlatform::class)
abstract class BaseBodySpec(testBody: BaseTestBody) : Spek(testBody.create())
