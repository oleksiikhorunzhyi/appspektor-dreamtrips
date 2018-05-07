package com.worldventures.dreamtrips.social.service.users.liker.command

import com.worldventures.dreamtrips.social.service.users.base.command.BaseUserPaginationCommand
import com.worldventures.dreamtrips.social.service.users.base.command.GetUsersCommand
import io.techery.janet.command.annotations.CommandAction
import rx.Observable

@CommandAction
class GetLikersPaginationCommand(refresh: Boolean, getUserOperation: (page: Int, perPage: Int) -> Observable<out GetUsersCommand>)
   : BaseUserPaginationCommand(refresh, getUserOperation)
