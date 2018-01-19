package com.worldventures.dreamtrips.social.service.users.request.command

import com.worldventures.dreamtrips.social.service.users.base.command.BaseUserPaginationCommand
import com.worldventures.dreamtrips.social.service.users.base.command.GetUsersCommand
import io.techery.janet.command.annotations.CommandAction
import rx.Observable

@CommandAction
class GetRequestsPaginationCommand(
      refresh: Boolean,
      getUserOperation: (page: Int, perPage: Int) -> Observable<out GetUsersCommand>
) : BaseUserPaginationCommand(refresh, getUserOperation)
