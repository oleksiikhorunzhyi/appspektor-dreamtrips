package com.worldventures.dreamtrips.social.media_picker

import com.innahema.collections.query.queriables.Queryable
import com.nhaarman.mockito_kotlin.mock
import com.worldventures.dreamtrips.AssertUtil.assertActionSuccess
import com.worldventures.dreamtrips.BaseSpec
import com.worldventures.dreamtrips.modules.media_picker.service.command.GetMediaFromGalleryCommand
import com.worldventures.dreamtrips.modules.media_picker.service.command.GetPhotosFromGalleryCommand
import com.worldventures.dreamtrips.modules.media_picker.model.MediaPickerModel
import com.worldventures.dreamtrips.modules.media_picker.model.PhotoPickerModel
import com.worldventures.dreamtrips.modules.media_picker.model.VideoPickerModel
import com.worldventures.dreamtrips.modules.common.service.MediaInteractor
import com.worldventures.dreamtrips.modules.media_picker.service.delegate.PhotosProvider
import com.worldventures.dreamtrips.modules.media_picker.service.delegate.VideosProvider
import io.techery.janet.ActionState
import io.techery.janet.CommandActionService
import io.techery.janet.Janet
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.mockito.Mockito.`when`
import rx.observers.TestSubscriber
import java.util.*

class MediaInteractorSpec : BaseSpec({

   describe("Getting photos from gallery") {

      it ("should return same set of photos") {
         val photos = makeMockPhotos();
         setup(photos)
         assertActionSuccess(loadPhotos()) { it.result.size == photos.size }
      }

      it ("should exclude photos with gif paths") {
         setup(makeMockPhotosWithGifs())
         assertActionSuccess(loadPhotos()) {
            Queryable.from(it.result).filter {element, index ->  element.absolutePath.endsWith(".gif")}.count() == 0}
      }
   }

   describe("Getting media from gallery") {

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

      lateinit var mediaInteractor : MediaInteractor

      fun setup(photos: List<PhotoPickerModel>, videos: List<VideoPickerModel> = emptyList()) {
         val service = CommandActionService().wrapDagger()
         val janet = Janet.Builder()
               .addService(service)
               .build()
         service.registerProvider(PhotosProvider::class.java) { PhotosProvider { photos }}
         service.registerProvider(VideosProvider::class.java) { VideosProvider { videos }}
         mediaInteractor = MediaInteractor(janet)
         service.registerProvider(MediaInteractor::class.java) { mediaInteractor }
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

      fun getPhotoPickerModel(path: String, dateTaken: Long): PhotoPickerModel {
         var photoPickerModel = mock<PhotoPickerModel>()
         `when`(photoPickerModel.absolutePath).thenReturn(path)
         `when`(photoPickerModel.isAbsolutePathUrl).thenReturn(false)
         `when`(photoPickerModel.dateTaken).thenReturn(dateTaken)
         return photoPickerModel
      }

      fun getVideoPickerModel(path: String, dateTaken: Long): VideoPickerModel {
         var videoPickerModel = mock<VideoPickerModel>()
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