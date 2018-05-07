package com.worldventures.dreamtrips.social.ui.bucketlist.presenter

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.anyOrNull
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import com.worldventures.core.janet.SessionActionPipeCreator
import com.worldventures.core.model.User
import com.worldventures.core.model.session.SessionHolder
import com.worldventures.core.model.session.UserSession
import com.worldventures.core.storage.complex_objects.Optional
import com.worldventures.dreamtrips.social.common.presenter.PresenterBaseSpec
import com.worldventures.dreamtrips.social.ui.bucketlist.model.BucketItem
import com.worldventures.dreamtrips.social.ui.bucketlist.model.BucketPhoto
import com.worldventures.dreamtrips.social.ui.bucketlist.service.BucketInteractor
import com.worldventures.dreamtrips.social.ui.bucketlist.service.action.UpdateBucketItemCommand
import com.worldventures.dreamtrips.social.ui.bucketlist.service.command.DeleteItemPhotoCommand
import io.techery.janet.CommandActionService
import io.techery.janet.Janet
import io.techery.janet.command.test.Contract
import io.techery.janet.command.test.MockCommandActionService
import org.jetbrains.spek.api.dsl.SpecBody
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it

class BucketPhotoFullscreenPresenterTest : PresenterBaseSpec(BucketPhotoFullscreenTestSuite()) {

   class BucketPhotoFullscreenTestSuite : TestSuite<BucketPhotoFullscreenComponents>(BucketPhotoFullscreenComponents()) {

      override fun specs(): SpecBody.() -> Unit = {

         with(components) {
            describe("Bucket Photo Fullscreen Presenter") {

               it("should update bucket photo in view on view taken") {
                  init()
                  linkPresenterAndView()

                  presenter.onViewTaken()

                  verify(view).setBucketPhoto(any())
               }

               it("should hide update cover photo button") {
                  init(userId = FOREIGN_ACCOUNT_ID)
                  linkPresenterAndView()

                  presenter.updatePhoto()

                  verify(view).hideDeleteBtn()
                  verify(view).hideCoverCheckBox()
               }

               it("should update cover checkbox and present it as cover photo") {
                  init()
                  linkPresenterAndView()

                  presenter.updatePhoto()

                  verify(view).updateCoverCheckbox(true)
                  verify(view).showDeleteBtn()
               }

               it("should update cover checkbox and present it as usual photo") {
                  init()
                  linkPresenterAndView()
                  val otherBucketPhoto = mockBucketPhoto("random uid")
                  whenever(bucketItem.coverPhoto).thenReturn(otherBucketPhoto)

                  presenter.updatePhoto()

                  verify(view).updateCoverCheckbox(false)
                  verify(view).showDeleteBtn()
               }

               it("should update cover check box when bucket item is updated") {
                  val contract = Contract.of(UpdateBucketItemCommand::class.java).result(bucketItem)
                  init(contract)
                  linkPresenterAndView()

                  presenter.subscribeToBucketUpdates()
                  bucketInteractor.updatePipe().send(UpdateBucketItemCommand(mock()))

                  verify(view).updateCoverCheckbox(any())
               }

               it("should delete photo and inform use") {
                  val contract = Contract.of(DeleteItemPhotoCommand::class.java).result(bucketItem)
                  init(contract)
                  linkPresenterAndView()

                  presenter.onDeletePhoto()

                  verify(view).informUser(anyOrNull<String>())
               }

               it("should save cover and update view") {
                  val contract = Contract.of(UpdateBucketItemCommand::class.java).result(bucketItem)
                  init(contract)
                  linkPresenterAndView()

                  presenter.onChangeCoverChosen()

                  verify(view).hideCoverProgress()
               }
            }
         }
      }
   }

   class BucketPhotoFullscreenComponents : TestComponents<BucketPhotoFullscreenPresenter, BucketPhotoFullscreenPresenter.View>() {

      val ACCOUNT_ID = 5
      val FOREIGN_ACCOUNT_ID = 6

      lateinit var bucketItem: BucketItem
      lateinit var bucketInteractor: BucketInteractor

      fun init(contract: Contract? = null, userId: Int = ACCOUNT_ID) {
         val bucketPhoto = mockBucketPhoto("22")
         bucketItem = mockBucketItem(userId, bucketPhoto)
         presenter = BucketPhotoFullscreenPresenter(bucketPhoto, bucketItem)
         view = mock()

         val service = MockCommandActionService.Builder().apply {
            actionService(CommandActionService())
            if (contract != null) {
               addContract(contract)
            }
         }.build()
         val janet = Janet.Builder().addService(service).build()
         val sessionPipeCreator = SessionActionPipeCreator(janet)

         bucketInteractor = BucketInteractor(sessionPipeCreator)

         prepareInjector(makeSessionHolder()).apply {
            registerProvider(BucketInteractor::class.java, { bucketInteractor })
            inject(presenter)
         }
      }

      fun mockBucketPhoto(bucketPhotoUid: String): BucketPhoto {
         val bucketPhoto = mock<BucketPhoto>()
         whenever(bucketPhoto.uid).thenReturn(bucketPhotoUid)
         return bucketPhoto
      }

      private fun makeSessionHolder(): SessionHolder {
         val sessionHolder = mock<SessionHolder>()
         val user = User()
         user.id = ACCOUNT_ID
         val userSession = mock<UserSession>()
         whenever(userSession.user()).thenReturn(user)
         whenever(sessionHolder.get()).thenReturn(Optional.of(userSession))
         return sessionHolder
      }

      private fun mockBucketItem(userId: Int, bucketPhoto: BucketPhoto): BucketItem {
         val user = User()
         user.id = userId

         val bucketItem = mock<BucketItem>().apply {
            whenever(uid).thenReturn("11")
            whenever(status).thenReturn("done")
            whenever(type).thenReturn("location")
            whenever(owner).thenReturn(user)
            whenever(coverPhoto).thenReturn(bucketPhoto)
         }

         return bucketItem
      }
   }
}
