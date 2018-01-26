package com.worldventures.dreamtrips.social.common.base

import org.jetbrains.spek.api.dsl.Spec

interface BaseTestBody {

   fun create(): Spec.() -> Unit

}
