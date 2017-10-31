package com.worldventures.dreamtrips.social.media_picker

import com.innahema.collections.query.queriables.Queryable
import com.nhaarman.mockito_kotlin.mock
import com.worldventures.core.janet.SessionActionPipeCreator
import com.worldventures.core.modules.picker.command.GetMediaFromGalleryCommand
import com.worldventures.core.modules.picker.command.GetPhotosFromGalleryCommand
import com.worldventures.core.modules.picker.model.MediaPickerModel
import com.worldventures.core.modules.picker.model.PhotoPickerModel
import com.worldventures.core.modules.picker.model.VideoPickerModel
import com.worldventures.core.modules.picker.service.MediaPickerInteractor
import com.worldventures.core.modules.picker.service.delegate.PhotosProvider
import com.worldventures.core.modules.picker.service.delegate.VideosProvider
import com.worldventures.core.test.AssertUtil.assertActionSuccess
import com.worldventures.dreamtrips.BaseSpec
import io.techery.janet.ActionState
import io.techery.janet.CommandActionService
import io.techery.janet.Janet
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.xdescribe
import org.mockito.Mockito.`when`
import rx.observers.TestSubscriber
import java.util.ArrayList
import java.util.Date

class MediaInteractorSpec : BaseSpec({

   // todo fix NPE in isAbsolutePathUrl method
   xdescribe("Getting photos from gallery") {

      it ("should return same set of photos") {
         val photos = makeMockPhotos()
         setup(photos)
         assertActionSuccess(loadPhotos()) { it.result.size == photos.size }
      }

      it ("should exclude photos with gif paths") {
         setup(makeMockPhotosWithGifs())
         assertActionSuccess(loadPhotos()) {
            Queryable.from(it.result).filter { element, _ ->  element.absolutePath.endsWith(".gif")}.count() == 0}
      }
   }

   xdescribe("Getting media from gallery") {

      it ("should return sorted photos and videos") {
         val photos = makeMockPhotos()
         val videos = makeMockVideos()
         setup(photos, videos)
         assertActionSuccess(loadMedia()) {
            assert(it.result.size == photos.size + videos.size)
            checkModelsSorted(it.result)
            true
         }
      }
   }

}) {

   companion object {

      private lateinit var mediaInteractor : MediaPickerInteractor

      fun setup(photos: List<PhotoPickerModel>, videos: List<VideoPickerModel> = emptyList()) {
         val service = CommandActionService().wrapDagger()
         val janet = Janet.Builder()
               .addService(service)
               .build()
         service.registerProvider(PhotosProvider::class.java) { PhotosProvider { _: Date, _: Int -> photos }}
         service.registerProvider(VideosProvider::class.java) { VideosProvider { videos }}
         mediaInteractor = MediaPickerInteractor(SessionActionPipeCreator(janet))
         service.registerProvider(MediaPickerInteractor::class.java) { mediaInteractor }
      }

      fun makeMockPhotos(): List<PhotoPickerModel> {
         return listOf(getPhotoPickerModel("/storage/1.jpg", 11), getPhotoPickerModel("/storage/2.jpg", 33))
      }

      fun makeMockPhotosWithGifs(): List<PhotoPickerModel> {
         val photos: MutableList<PhotoPickerModel> = ArrayList(makeMockPhotos())
         photos.add(getPhotoPickerModel("/storage/3.gif", 55))
         return photos
      }

      fun makeMockVideos(): List<VideoPickerModel> {
         return listOf(getVideoPickerModel("/storage/1.mp4", 22), getVideoPickerModel("/storage/2.mp4", 44))
      }

      private fun getPhotoPickerModel(path: String, dateTaken: Long): PhotoPickerModel {
         val photoPickerModel = mock<PhotoPickerModel>()
         `when`(photoPickerModel.absolutePath).thenReturn(path)
         `when`(photoPickerModel.isAbsolutePathUrl).thenReturn(false)
         `when`(photoPickerModel.dateTaken).thenReturn(dateTaken)
         return photoPickerModel
      }

      // todo why do we create mocked entity ? it's just model
      private fun getVideoPickerModel(path: String, dateTaken: Long): VideoPickerModel {
         val videoPickerModel = mock<VideoPickerModel>()
         `when`(videoPickerModel.absolutePath).thenReturn(path)
         `when`(videoPickerModel.isAbsolutePathUrl).thenReturn(false)
         `when`(videoPickerModel.dateTaken).thenReturn(dateTaken)
         return videoPickerModel
      }

      fun loadPhotos(): TestSubscriber<ActionState<GetPhotosFromGalleryCommand>> {
         val testSubcriber = TestSubscriber<ActionState<GetPhotosFromGalleryCommand>>()
         mediaInteractor.photosFromGalleryPipe.createObservable(GetPhotosFromGalleryCommand()).subscribe(testSubcriber)
         return testSubcriber
      }

      fun loadMedia(): TestSubscriber<ActionState<GetMediaFromGalleryCommand>> {
         val testSubcriber = TestSubscriber<ActionState<GetMediaFromGalleryCommand>>()
         mediaInteractor.mediaFromGalleryPipe.createObservable(GetMediaFromGalleryCommand(true)).subscribe(testSubcriber)
         return testSubcriber
      }

      fun checkModelsSorted(mediaItems: List<MediaPickerModel>) {
         var previousModel: MediaPickerModel? = null
         for (model in mediaItems) {
            if (previousModel != null) {
               assert(previousModel.dateTaken > model.dateTaken)
            }
            previousModel = model
         }
      }
   }
}