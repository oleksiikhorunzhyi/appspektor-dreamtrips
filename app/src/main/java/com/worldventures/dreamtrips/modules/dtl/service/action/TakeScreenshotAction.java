package com.worldventures.dreamtrips.modules.dtl.service.action;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;

import com.worldventures.janet.injection.InjectableAction;

import java.io.File;

import javax.inject.Inject;

import github.nisrulz.screenshott.ScreenShott;
import io.techery.janet.Command;
import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class TakeScreenshotAction extends Command<String> implements InjectableAction {

   @Inject Context context;

   private View view;

   public TakeScreenshotAction(View view) {
      this.view = view;
   }

   @Override
   protected void run(CommandCallback callback) throws Throwable {
      callback.onProgress(0);

      try {
         Bitmap bitmap = ScreenShott.getInstance().takeScreenShotOfRootView(view);
         File file = ScreenShott.getInstance()
               .saveScreenshotToPicturesFolder(context, bitmap, "my_screenshot_filename.jpg");
         callback.onSuccess(file.getAbsolutePath());
      } catch (Exception e) {
         callback.onFail(e);
      } finally {
         view = null;
      }
   }
}
