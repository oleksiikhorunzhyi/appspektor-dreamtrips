package com.worldventures.dreamtrips.social.common.command

import com.worldventures.dreamtrips.social.common.base.BaseTestBody
import io.techery.janet.HttpActionService

interface BaseCommandTestBody : BaseTestBody {

   fun setup(actionService: HttpActionService)

}
