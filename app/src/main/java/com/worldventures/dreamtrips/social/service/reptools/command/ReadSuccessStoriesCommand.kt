package com.worldventures.dreamtrips.social.service.reptools.command

import com.worldventures.dreamtrips.modules.common.list_storage.operation.ListStorageOperation
import com.worldventures.dreamtrips.social.ui.reptools.model.SuccessStory
import io.techery.janet.command.annotations.CommandAction

@CommandAction
class ReadSuccessStoriesCommand(predicate: (SuccessStory) -> Boolean = { true }) : SuccessStoriesCommand(ListStorageOperation { it }, predicate)
