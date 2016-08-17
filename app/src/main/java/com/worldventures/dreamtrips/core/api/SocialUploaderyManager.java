package com.worldventures.dreamtrips.core.api;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.core.api.uploadery.SimpleUploaderyCommand;
import com.worldventures.dreamtrips.core.api.uploadery.UploaderyImageCommand;
import com.worldventures.dreamtrips.core.api.uploadery.UploaderyManager;

import javax.inject.Inject;

import io.techery.janet.ActionState;
import rx.Observable;

public class SocialUploaderyManager {

   @Inject UploaderyManager uploaderyManager;

   public SocialUploaderyManager(Injector injector) {
      injector.inject(this);
   }

   public void upload(String filePath) {
      upload(filePath, filePath.hashCode());
   }

   public void upload(String filePath, int commandId) {
      uploaderyManager.getUploadImagePipe().send(new SimpleUploaderyCommand(filePath, commandId));
   }

   public Observable<ActionState<UploaderyImageCommand>> getTaskChangingObservable() {
      return uploaderyManager.getUploadImagePipe().observe();
   }

}
