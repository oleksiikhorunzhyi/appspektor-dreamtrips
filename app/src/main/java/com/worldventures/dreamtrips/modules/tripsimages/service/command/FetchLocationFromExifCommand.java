package com.worldventures.dreamtrips.modules.tripsimages.service.command;


import android.media.ExifInterface;

import com.worldventures.dreamtrips.modules.trips.model.Location;

import java.io.IOException;

import io.techery.janet.Command;
import io.techery.janet.command.annotations.CommandAction;
import rx.Observable;

@CommandAction
public class FetchLocationFromExifCommand extends Command<Location> {

   private String filePath;

   public FetchLocationFromExifCommand(String filePath) {
      this.filePath = filePath;
   }

   @Override
   protected void run(CommandCallback<Location> callback) throws Throwable {
      Observable.just(filePath)
            .flatMap(path -> {
               ExifInterface exifInterface = null;
               try {
                  exifInterface = new ExifInterface(path);
               } catch (IOException e) {
                  return Observable.error(e);
               }
               float latLng[] = new float[2];
               if (exifInterface.getLatLong(latLng))
                  return Observable.just(new Location(latLng[0], latLng[1]));
               return Observable.just(new Location());
            })
            .subscribe(callback::onSuccess, callback::onFail);
   }
}
