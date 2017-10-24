package com.worldventures.dreamtrips.social.service.invites

import com.worldventures.dreamtrips.social.service.invites.operation.DeselectContactsOperation
import io.techery.janet.command.annotations.CommandAction

@CommandAction
class DeselectAllContactsCommand : MembersCommand(DeselectContactsOperation())