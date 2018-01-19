package com.worldventures.dreamtrips.social.service.users.search.command

import com.worldventures.dreamtrips.social.service.users.base.command.BaseUserStorageCommand
import com.worldventures.dreamtrips.social.service.users.base.operation.BaseUserStorageOperation
import io.techery.janet.command.annotations.CommandAction

@CommandAction
class SearchedUsersStorageCommand(operation: BaseUserStorageOperation) : BaseUserStorageCommand(operation)
