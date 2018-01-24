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
import com.worldventures.dreamtrips.BaseSpec.Companion.anyString
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
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it

class BucketPhotoFullscreenPresenterTest : PresenterBaseSpec ({

   describe("BucketPhotoFullscreenPresenter") {

      it("should update bucket photo in view on view taken") {
         setup()

         presenter.onViewTaken()

         verify(view).setBucketPhoto(any())
      }

      it("should hide update cover photo button") {
         setup()
         val user = User()
         user.id = FOREIGN_ACCOUNT_ID
         whenever(bucketItem.owner).thenReturn(user)

         presenter.updatePhoto()

         verify(view).hideDeleteBtn()
         verify(view).hideCoverCheckBox()
      }

      it("should update cover checkbox and present it as cover photo") {
         setup()
         val user = User()
         user.id = ACCOUNT_ID
         whenever(bucketItem.owner).thenReturn(user)
         whenever(bucketItem.coverPhoto).thenReturn(bucketPhoto)

         presenter.updatePhoto()

         verify(view).updateCoverCheckbox(true)
         verify(view).showDeleteBtn()
      }

      it("should update cover checkbox and present it as usual photo") {
         setup()
         val user = User()
         user.id = ACCOUNT_ID
         whenever(bucketItem.owner).thenReturn(user)
         val otherBucketPhoto: BucketPhoto = mock()
         whenever(otherBucketPhoto.uid).thenReturn("random uid")
         whenever(bucketItem.coverPhoto).thenReturn(otherBucketPhoto)

         presenter.updatePhoto()

         verify(view).updateCoverCheckbox(false)
         verify(view).showDeleteBtn()
      }

      it("should update cover check box when bucket item is updated") {
         setup(Contract.of(UpdateBucketItemCommand::class.java).result(bucketItem))
         presenter.subscribeToBucketUpdates()

         bucketInteractor.updatePipe().send(UpdateBucketItemCommand(mock()))

         verify(view).updateCoverCheckbox(any())
      }

      it("should delete photo and inform use") {
         setup(Contract.of(DeleteItemPhotoCommand::class.java).result(bucketItem))

         presenter.onDeletePhoto()

         verify(view).informUser(anyOrNull<String>())
      }

      it("should save cover and update view") {
         setup(Contract.of(UpdateBucketItemCommand::class.java).result(bucketItem))
         whenever(bucketItem.uid).thenReturn("11")
         whenever(bucketItem.status).thenReturn("done")
         whenever(bucketItem.type).thenReturn("location")
         whenever(bucketPhoto.uid).thenReturn("22")

         presenter.onChangeCoverChosen()

         verify(view).hideCoverProgress()
      }
   }
}) {
   companion object {
      lateinit var presenter: BucketPhotoFullscreenPresenter
      lateinit var view: BucketPhotoFullscreenPresenter.View
      lateinit var bucketInteractor: BucketInteractor
      val bucketPhoto: BucketPhoto = mock()
      val bucketItem: BucketItem = mock()
      val ACCOUNT_ID = 5
      val FOREIGN_ACCOUNT_ID = 6

      fun setup(contract: Contract? = null) {
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

         presenter.takeView(view)
      }

      fun makeSessionHolder(): SessionHolder {
         val sessionHolder = mock<SessionHolder>()
         val user = User()
         user.id = ACCOUNT_ID
         val userSession = mock<UserSession>()
         whenever(userSession.user()).thenReturn(user)
         whenever(sessionHolder.get()).thenReturn(Optional.of(userSession))
         return sessionHolder
      }
   }
}
