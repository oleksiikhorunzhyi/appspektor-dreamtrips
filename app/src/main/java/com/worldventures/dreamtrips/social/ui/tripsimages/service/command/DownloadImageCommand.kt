package com.worldventures.dreamtrips.social.ui.tripsimages.service.command

import android.content.ContentResolver
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.provider.MediaStore
import com.worldventures.core.di.qualifier.ForApplication
import com.worldventures.core.modules.video.utils.CachedModelHelper
import com.worldventures.janet.injection.InjectableAction
import io.techery.janet.Command
import io.techery.janet.command.annotations.CommandAction
import timber.log.Timber
import java.net.URL
import javax.inject.Inject

@CommandAction
class DownloadImageCommand(private val url: String) : Command<String>(), InjectableAction {

   @Inject @ForApplication internal lateinit var context: Context
   @Inject internal lateinit var cachedModelHelper: CachedModelHelper

   @Throws(Throwable::class)
   override fun run(callback: Command.CommandCallback<String>) = callback.onSuccess(cacheBitmap())

   @Throws(Exception::class)
   private fun cacheBitmap(): String? {
      val bitmap = BitmapFactory.decodeStream(URL(url).openStream())
      return insertImage(bitmap, cachedModelHelper.getFileName(url))
   }

   /**
    * Use for save image to gallery instead of [android.provider.MediaStore.Images.Media],
    * because we need to change [MediaStore.Images.Media.DATE_TAKEN]
    *
    * @param source Bitmap decoded from stream
    * @param title The name of image
    * @return Uri for newly created image
    */
   private fun insertImage(source: Bitmap?, title: String): String? {
      val values = ContentValues()
      values.put(MediaStore.Images.Media.TITLE, title)
      values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis())
      values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
      //
      var url: Uri? = null
      var stringUrl: String? = null
      val cr = context.contentResolver
      //
      try {
         url = cr.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)

         if (source != null) {
            val imageOut = cr.openOutputStream(url)
            imageOut.use {
               source.compress(Bitmap.CompressFormat.JPEG, COMPRESS_QUALITY, it)
            }

            val id = ContentUris.parseId(url)
            val miniThumb = MediaStore.Images.Thumbnails.getThumbnail(cr, id, MediaStore.Images.Thumbnails.MINI_KIND, null)
            storeThumbnail(cr, miniThumb, id, THUMB_SIZE, THUMB_SIZE, MediaStore.Images.Thumbnails.MICRO_KIND)
         } else {
            Timber.e("Failed to create thumbnail, removing original")
            if (url != null) {
               cr.delete(url, null, null)
            }
            url = null
         }
      } catch (e: Exception) {
         Timber.e(e, "Failed to insert image")
         if (url != null) {
            cr.delete(url, null, null)
            url = null
         }
      }

      //
      if (url != null) {
         stringUrl = url.toString()
      }

      return stringUrl
   }

   private fun storeThumbnail(cr: ContentResolver, source: Bitmap, id: Long, width: Float, height: Float, kind: Int): Bitmap? {
      val matrix = Matrix()
      //
      val scaleX = width / source.width
      val scaleY = height / source.height
      matrix.setScale(scaleX, scaleY)
      //
      val thumb = Bitmap.createBitmap(source, 0, 0, source.width, source.height, matrix, true)
      //
      val values = ContentValues(CONTENT_VALUES_SIZE)
      values.put(MediaStore.Images.Thumbnails.KIND, kind)
      values.put(MediaStore.Images.Thumbnails.IMAGE_ID, id.toInt())
      values.put(MediaStore.Images.Thumbnails.HEIGHT, thumb.height)
      values.put(MediaStore.Images.Thumbnails.WIDTH, thumb.width)
      //
      val url = cr.insert(MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI, values)
      //
      return try {
         val thumbOut = cr.openOutputStream(url)
         thumb.compress(Bitmap.CompressFormat.JPEG, COMPRESS_QUALITY_THUMB_OUT, thumbOut)
         thumbOut.close()
         thumb
      } catch (ex: Exception) {
         null
      }
   }

   companion object {
      const val CONTENT_VALUES_SIZE = 4
      const val THUMB_SIZE = 50f
      const val COMPRESS_QUALITY = 50
      const val COMPRESS_QUALITY_THUMB_OUT = 100
   }
}
