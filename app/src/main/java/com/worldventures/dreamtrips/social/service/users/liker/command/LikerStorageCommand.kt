package com.worldventures.dreamtrips.social.service.users.liker.command

import com.worldventures.dreamtrips.social.service.users.base.command.BaseUserStorageCommand
import com.worldventures.dreamtrips.social.service.users.base.operation.BaseUserStorageOperation
import io.techery.janet.command.annotations.CommandAction

@CommandAction
class LikerStorageCommand(operation: BaseUserStorageOperation) : BaseUserStorageCommand(operation)
