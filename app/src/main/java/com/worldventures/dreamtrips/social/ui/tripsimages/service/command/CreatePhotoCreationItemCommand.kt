package com.worldventures.dreamtrips.social.ui.tripsimages.service.command

import android.graphics.BitmapFactory
import android.net.Uri
import com.worldventures.core.modules.picker.command.CopyFileCommand
import com.worldventures.core.modules.picker.model.MediaPickerAttachment
import com.worldventures.core.modules.picker.model.PhotoPickerModel
import com.worldventures.core.modules.picker.service.MediaPickerInteractor
import com.worldventures.core.modules.picker.util.CapturedRowMediaHelper
import com.worldventures.core.utils.Size
import com.worldventures.core.utils.ValidationUtils
import com.worldventures.dreamtrips.social.ui.feed.model.PhotoCreationItem
import com.worldventures.janet.injection.InjectableAction
import io.techery.janet.Command
import io.techery.janet.command.annotations.CommandAction
import java.io.File
import javax.inject.Inject

@CommandAction
class CreatePhotoCreationItemCommand(private val photoPickerModel: PhotoPickerModel, private val source: MediaPickerAttachment.Source) : Command<PhotoCreationItem>(), InjectableAction {

   @Inject internal lateinit var mediaInteractor: MediaPickerInteractor
   @Inject internal lateinit var capturedRowMediaHelper: CapturedRowMediaHelper

   @Throws(Throwable::class)
   override fun run(callback: Command.CommandCallback<PhotoCreationItem>) {
      val fileUri = photoPickerModel.uri.toString()
      if (ValidationUtils.isUrl(fileUri)) {
         mediaInteractor.copyFilePipe()
               .createObservableResult(CopyFileCommand(fileUri))
               .subscribe({
                  val stringUri = it.result
                  val uri = Uri.parse(stringUri)
                  callback.onSuccess(createPhotoItem(stringUri, uri.path, getImageSize(uri.path)))
               }, callback::onFail)
      } else {
         val photoCreation = createPhotoItem(photoPickerModel.uri
               .toString(), photoPickerModel.absolutePath,
               getImageSize(photoPickerModel.uri.path))
         photoCreation.rotation = capturedRowMediaHelper.obtainPhotoOrientation(photoPickerModel.uri.path)
         callback.onSuccess(photoCreation)
      }
   }

   private fun createPhotoItem(uri: String, path: String, size: Size): PhotoCreationItem {
      val item = PhotoCreationItem()
      item.id = photoPickerModel.uri.hashCode().toLong()
      item.source = source
      item.isCanDelete = true
      item.isCanEdit = true
      item.filePath = path
      item.fileUri = uri
      item.width = size.width
      item.height = size.height
      return item
   }

   private fun getImageSize(path: String): Size {
      val imageSize = photoPickerModel.size
      return if (imageSize == null) {
         val options = BitmapFactory.Options()
         options.inJustDecodeBounds = true
         BitmapFactory.decodeFile(File(path).absolutePath, options)
         Size(options.outWidth, options.outHeight)
      } else imageSize
   }
}
