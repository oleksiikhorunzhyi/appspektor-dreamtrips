package com.worldventures.dreamtrips.social.friends.spec.interactor

import com.worldventures.core.janet.SessionActionPipeCreator
import com.worldventures.core.janet.cache.CacheResultWrapper
import com.worldventures.core.model.User
import com.worldventures.core.test.AssertUtil
import com.worldventures.dreamtrips.BaseSpec
import com.worldventures.dreamtrips.social.friends.util.MockUtil
import com.worldventures.dreamtrips.social.ui.friends.service.FriendsInteractor
import com.worldventures.dreamtrips.social.ui.friends.service.command.AddFriendCommand
import com.worldventures.dreamtrips.social.ui.friends.storage.RequestsStorage
import io.techery.janet.ActionService
import io.techery.janet.ActionState
import io.techery.janet.CommandActionService
import io.techery.janet.Janet
import io.techery.janet.command.test.MockCommandActionService
import io.techery.janet.http.test.MockHttpActionService
import org.jetbrains.spek.api.dsl.context
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import rx.observers.TestSubscriber

class AddFriendCommandSpec : BaseSpec({
   describe("Receive added user successfully") {
      beforeEachTest { setup(mockHttpServiceForSuccessResult()) }

      context("Should send mocked query for add user and return it") {
         it("Should return added user entity") {
            AssertUtil.assertActionSuccess(senCommand()) {
               (it.result?.id ?: return@assertActionSuccess false) == user.id
            }
         }
      }
   }

   describe("Error during adding  user") {
      beforeEachTest { setup(mockHttpServiceForError()) }

      context("Here must throw Exception") {
         it("Exception should be thrown") {
            AssertUtil.assertActionFail(senCommand(), { it != null })
         }
      }
   }
}) {
   companion object {
      private lateinit var friendsInteractor: FriendsInteractor
      private val user: User = MockUtil.mockUser(100500)
      private const val FRIEND_CIRCLE_ID = "61bef157-12d8-41fd-a1cd-70643c099974"

      private fun setup(httpService: ActionService) {
         val daggerCommandActionService = CommandActionService().wrapDagger()
         val storage = RequestsStorage()
         val commandCacheWrapper = CacheResultWrapper(daggerCommandActionService)
         commandCacheWrapper.bindStorage(storage.actionClass, storage)

         val janet = Janet.Builder()
               .addService(commandCacheWrapper)
               .addService(httpService)
               .build()

         daggerCommandActionService.registerProvider(Janet::class.java) { janet }

         friendsInteractor = FriendsInteractor(SessionActionPipeCreator(janet))
      }

      private fun mockHttpServiceForSuccessResult(): MockHttpActionService {
         return MockHttpActionService.Builder()
               .bind(MockHttpActionService.Response(200), {
                  it.url.contains("/api/social/friends/requests")
               })
               .build()
      }

      private fun mockHttpServiceForError(): MockHttpActionService {
         return MockHttpActionService.Builder()
               .bind(MockHttpActionService.Response(422), {
                  it.url.contains("/api/social/friends/requests")
               })
               .build()
      }

      private fun senCommand(): TestSubscriber<ActionState<AddFriendCommand>> {
         return TestSubscriber<ActionState<AddFriendCommand>>().apply {
            friendsInteractor.addFriendPipe()
                  .createObservable(AddFriendCommand(user, FRIEND_CIRCLE_ID))
                  .subscribe(this)
         }
      }
   }
}
