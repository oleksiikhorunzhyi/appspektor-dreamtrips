package com.worldventures.dreamtrips.social.service.reptools.command

import com.worldventures.dreamtrips.modules.common.list_storage.operation.RefreshStorageOperation
import com.worldventures.dreamtrips.social.ui.reptools.model.SuccessStory
import io.techery.janet.command.annotations.CommandAction

@CommandAction
class RefreshSuccessStoriesCommand(list: List<SuccessStory>) : SuccessStoriesCommand(RefreshStorageOperation(list))
