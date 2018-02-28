package com.worldventures.dreamtrips.social.service.bucketlist.command

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import com.worldventures.core.model.EntityStateHolder
import com.worldventures.core.modules.infopages.StaticPageProvider
import com.worldventures.core.service.UploadingFileManager
import com.worldventures.core.service.UriPathProvider
import com.worldventures.core.test.AssertUtil.assertActionStateFail
import com.worldventures.core.test.AssertUtil.assertActionSuccess
import com.worldventures.core.test.janet.MockDaggerActionService
import com.worldventures.dreamtrips.api.bucketlist.model.ImmutableBucketPhoto
import com.worldventures.dreamtrips.api.uploadery.model.UploaderyImage
import com.worldventures.dreamtrips.api.uploadery.model.UploaderyImageResponse
import com.worldventures.dreamtrips.social.common.base.BaseBodySpec
import com.worldventures.dreamtrips.social.ui.bucketlist.service.command.AddBucketItemPhotoCommand
import io.techery.janet.ActionState
import io.techery.janet.http.test.MockHttpActionService
import org.jetbrains.spek.api.dsl.Spec
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.junit.rules.TemporaryFolder
import org.mockito.ArgumentMatchers.anyString
import rx.observers.TestSubscriber

typealias ApiBucketPhoto = com.worldventures.dreamtrips.api.bucketlist.model.BucketPhoto

class AddBucketItemPhotoCommandSpec : BaseBodySpec(object : BaseBucketListCommandTestBody() {

   private val bucketPhoto = mockApiBucketPhoto("1")
   private val uploaderyImage: UploaderyImage = mock()
   private val imageUploadResponse: UploaderyImageResponse = mock()
   private val TEST_IMAGE_PATH = TemporaryFolder().createFileAndGetPath("TestPhoto.jpeg")
   private val TEST_BACKEND_PATH = "http://test-server"

   override fun create(): Spec.() -> Unit = {
      describe("Item add photo") {
         it("Should create item's photo") {
            setup(mockHttpServiceForSuccess())

            val testSubscriber = TestSubscriber<ActionState<AddBucketItemPhotoCommand>>()
            bucketInteractor.addBucketItemPhotoPipe()
                  .createObservable(AddBucketItemPhotoCommand(bucketItem, TEST_IMAGE_PATH))
                  .subscribe(testSubscriber)

            assertActionSuccess(testSubscriber) {
               it.result.second?.uid == bucketPhoto.uid() &&
                     it.result.first?.photos?.contains(it.result.second) ?: false &&
                     it.photoEntityStateHolder().state() == EntityStateHolder.State.DONE
            }
         }
      }

      describe("Error while added photo") {
         it("Should throw error end set failed status to entity holder") {
            setup(mockHttpServiceForError())
            val testSubscriber = TestSubscriber<ActionState<AddBucketItemPhotoCommand>>()
            bucketInteractor.addBucketItemPhotoPipe()
                  .createObservable(AddBucketItemPhotoCommand(bucketItem, TEST_IMAGE_PATH))
                  .subscribe(testSubscriber)
            assertActionStateFail(testSubscriber) { it.action.photoEntityStateHolder().state() == EntityStateHolder.State.FAIL }
         }
      }
   }

   override fun setup(httpService: MockHttpActionService) {
      super.setup(httpService)
      whenever(imageUploadResponse.uploaderyPhoto()).thenReturn(uploaderyImage)
      whenever(uploaderyImage.location()).thenReturn(TEST_BACKEND_PATH)
   }

   override fun registerProviders(daggerActionService: MockDaggerActionService) {
      daggerActionService.registerProvider(UriPathProvider::class.java, { UriPathProvider { TEST_IMAGE_PATH } })

      val uploadingFileManager: UploadingFileManager = mock()
      uploadingFileManager.apply {
         daggerActionService.registerProvider(UploadingFileManager::class.java) { this }
         whenever(this.copyFileIfNeed(anyString())).thenReturn(TEST_IMAGE_PATH)
      }

      val staticProvider: StaticPageProvider = mock()
      staticProvider.apply {
         daggerActionService.registerProvider(StaticPageProvider::class.java) { this }
         whenever(this.uploaderyUrl).thenReturn("http://test-uploadery")
      }
   }

   override fun mockHttpServiceForSuccess() = MockHttpActionService.Builder()
         .bind(MockHttpActionService.Response(200).body(bucketPhoto)) {
            it.url.contains("/api/bucket_list_items/") && it.url.contains("/photos")
         }
         .bind(MockHttpActionService.Response(200).body(imageUploadResponse)) {
            it.url.contains("/upload")
         }
         .build()

   override fun mockHttpServiceForError() = MockHttpActionService.Builder()
         .bind(MockHttpActionService.Response(400).body(imageUploadResponse)) {
            it.url.contains("/upload")
         }
         .build()

   fun mockApiBucketPhoto(uid: String): ApiBucketPhoto {
      return ImmutableBucketPhoto.builder()
            .id(Integer.parseInt(uid))
            .uid(uid)
            .url("http://$uid.jpg")
            .originUrl("http://$uid.jpg")
            .build()
   }

})

fun TemporaryFolder.createFileAndGetPath(fileName: String): String {
   this.create()
   return this.newFile(fileName).path
}
