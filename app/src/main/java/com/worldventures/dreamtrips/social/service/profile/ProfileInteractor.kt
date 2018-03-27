package com.worldventures.dreamtrips.social.service.profile

import com.worldventures.core.janet.SessionActionPipeCreator
import com.worldventures.core.model.User
import com.worldventures.core.model.session.ImmutableUserSession
import com.worldventures.core.model.session.SessionHolder
import com.worldventures.dreamtrips.social.service.profile.command.AddFriendToCircleCommand
import com.worldventures.dreamtrips.social.service.profile.command.GetPrivateProfileCommand
import com.worldventures.dreamtrips.social.service.profile.command.GetPublicProfileCommand
import com.worldventures.dreamtrips.social.service.profile.command.RemoveFriendFromCircleCommand
import com.worldventures.dreamtrips.social.service.profile.command.UploadAvatarCommand
import com.worldventures.dreamtrips.social.service.profile.command.UploadBackgroundCommand
import io.techery.janet.Command
import rx.Observable
import rx.schedulers.Schedulers

class ProfileInteractor(sessionActionPipeCreator: SessionActionPipeCreator, private val sessionHolder: SessionHolder) {

   val privateProfilePipe = sessionActionPipeCreator.createPipe(GetPrivateProfileCommand::class.java, Schedulers.io())
   val uploadAvatarPipe = sessionActionPipeCreator.createPipe(UploadAvatarCommand::class.java, Schedulers.io())
   val uploadBackgroundPipe = sessionActionPipeCreator.createPipe(UploadBackgroundCommand::class.java, Schedulers.io())
   val publicProfilePipe = sessionActionPipeCreator.createPipe(GetPublicProfileCommand::class.java, Schedulers.io())
   val addFriendToCirclePipe = sessionActionPipeCreator.createPipe(AddFriendToCircleCommand::class.java, Schedulers.io())
   val removeFriendFromCirclePipe = sessionActionPipeCreator.createPipe(RemoveFriendFromCircleCommand::class.java, Schedulers.io())

   init {
      listenToPrivateProfileUpdates()
   }

   private fun listenToPrivateProfileUpdates() {
      Observable.merge<Command<User>>(privateProfilePipe.observeSuccess(),
            uploadAvatarPipe.observeSuccess(),
            uploadBackgroundPipe.observeSuccess())
            .subscribe {
               sessionHolder.put(ImmutableUserSession.builder()
                     .from(sessionHolder.get().get())
                     .user(it.result)
                     .build())
            }
   }
}
