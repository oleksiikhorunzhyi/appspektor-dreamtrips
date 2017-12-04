package com.worldventures.wallet.service.profile

import com.worldventures.wallet.domain.entity.SmartCardUser
import io.techery.janet.Command

abstract class BaseUserUpdateCommand<R>(val smartCardId: String,
                                        val newUser: SmartCardUser) : Command<R>()
