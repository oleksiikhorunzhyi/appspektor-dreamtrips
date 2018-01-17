package com.worldventures.dreamtrips.social.ui.tripsimages.service.command

import android.media.ExifInterface
import com.worldventures.dreamtrips.modules.trips.model.Location
import io.techery.janet.Command
import io.techery.janet.command.annotations.CommandAction
import rx.Observable

@CommandAction
class FetchLocationFromExifCommand(private val filePath: String) : Command<Location>() {

   @Throws(Throwable::class)
   override fun run(callback: Command.CommandCallback<Location>) {
      Observable.just(filePath)
            .flatMap { path ->
               val exifInterface = ExifInterface(path)
               val latLng = FloatArray(2)
               if (exifInterface.getLatLong(latLng)) {
                  Observable.just(Location(latLng[0].toDouble(), latLng[1].toDouble()))
               } else {
                  Observable.just(Location())
               }
            }
            .subscribe(callback::onSuccess, callback::onFail)
   }
}
