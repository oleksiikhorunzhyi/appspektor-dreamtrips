package com.worldventures.dreamtrips.social.ui.tripsimages.service.command

import com.worldventures.core.janet.CommandWithError
import com.worldventures.dreamtrips.R
import com.worldventures.dreamtrips.modules.common.model.UploadTask
import com.worldventures.dreamtrips.social.ui.feed.view.custom.tagview.viewgroup.newio.model.PhotoTag
import com.worldventures.dreamtrips.social.ui.tripsimages.model.Photo
import com.worldventures.dreamtrips.social.ui.tripsimages.service.TripImagesInteractor
import com.worldventures.janet.injection.InjectableAction
import io.techery.janet.Command
import io.techery.janet.command.annotations.CommandAction
import rx.Observable
import javax.inject.Inject

@CommandAction
class EditPhotoWithTagsCommand(private val uid: String, private val task: UploadTask, private val addedTags: MutableList<PhotoTag>,
                               private val removedTags: List<PhotoTag>) : CommandWithError<Photo>(), InjectableAction {

   @Inject internal lateinit var tripImagesInteractor: TripImagesInteractor

   @Throws(Throwable::class)
   override fun run(callback: Command.CommandCallback<Photo>) {
      tripImagesInteractor.editPhotoActionPipe.createObservableResult(EditPhotoCommand(uid, task))
            .map {
               val photo = it.result
               addedTags.removeAll(photo.photoTags)
               photo
            }
            .flatMap(this::getAddPhotoTagsObservable)
            .flatMap(this::getRemovePhotoTagsObservable)
            .doOnNext { photo ->
               photo.photoTags.addAll(addedTags)
               photo.photoTags.removeAll(removedTags)
               photo.photoTagsCount = photo.photoTags.size
            }
            .subscribe(callback::onSuccess, callback::onFail)
   }

   private fun getAddPhotoTagsObservable(photo: Photo) = if (addedTags.isEmpty()) {
      Observable.just(photo)
   } else tripImagesInteractor.addPhotoTagsActionPipe
         .createObservableResult(AddPhotoTagsCommand(uid, addedTags))
         .map { photo }

   private fun getRemovePhotoTagsObservable(photo: Photo) = if (removedTags.isEmpty()) {
      Observable.just(photo)
   } else tripImagesInteractor.deletePhotoTagsPipe
         .createObservableResult(DeletePhotoTagsCommand(uid, removedTags.plus(photo.photoTags).map { it.user.id }))
         .map { photo }

   override fun getFallbackErrorMessage() = R.string.photo_edit_error
}
