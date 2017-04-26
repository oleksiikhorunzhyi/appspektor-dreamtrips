package com.worldventures.dreamtrips.core.navigation;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.webkit.MimeTypeMap;

import com.techery.spares.ui.routing.ActivityBoundRouter;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.utils.FileUtils;
import com.worldventures.dreamtrips.modules.common.presenter.ComponentPresenter;
import com.worldventures.dreamtrips.modules.common.view.activity.ComponentActivity;
import com.worldventures.dreamtrips.modules.common.view.activity.LaunchActivity;
import com.worldventures.dreamtrips.modules.common.view.activity.MainActivity;
import com.worldventures.dreamtrips.modules.common.view.activity.Player360Activity;
import com.worldventures.dreamtrips.modules.common.view.activity.PlayerActivity;
import com.worldventures.dreamtrips.modules.player.PodcastPlayerActivity;

import java.io.File;

public class ActivityRouter extends ActivityBoundRouter {

   public static final int CAPTURE_PICTURE_REQUEST_TYPE = 294;

   public ActivityRouter(Activity activity) {
      super(activity);
   }

   public void openMain() {
      startActivity(MainActivity.class);
   }

   public void openMainWithComponent(String key, Class<? extends Activity> activitySender) {
      Bundle bundle = new Bundle();
      bundle.putString(MainActivity.COMPONENT_KEY, key);
      bundle.putSerializable(MainActivity.FROM_ACTIVITY_KEY, activitySender);
      startActivity(MainActivity.class, bundle);
   }

   public void openLaunch() {
      startActivity(LaunchActivity.class, null, Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
   }

   public void openPlayerActivity(Uri url, String videoName, String language, Class launchComponent) {
      Intent intent = new Intent(getContext(), PlayerActivity.class).setData(url)
            .putExtra(PlayerActivity.EXTRA_VIDEO_NAME, videoName)
            .putExtra(PlayerActivity.EXTRA_LAUNCH_COMPONENT, launchComponent)
            .putExtra(PlayerActivity.EXTRA_LANGUAGE, language);
      getContext().startActivity(intent);
   }

   public void open360Activity(String url, String title) {
      Bundle bundle = new Bundle();
      bundle.putString(Player360Activity.EXTRA_URL, url);
      bundle.putString(Player360Activity.EXTRA_TITLE, title);
      startActivity(Player360Activity.class, bundle);
   }

   public void openComponentActivity(@NonNull Route route, @NonNull Bundle args) {
      args.putSerializable(ComponentPresenter.ROUTE, route);
      startActivityWithArgs(ComponentActivity.class, args);
   }

   public void openComponentActivity(@NonNull Route route, @NonNull Bundle args, int flags) {
      args.putSerializable(ComponentPresenter.ROUTE, route);
      startActivityWithArgs(ComponentActivity.class, args, flags);
   }

   public String openCamera(String folderName) {
      Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
      String filePathOriginal = FileUtils.buildFilePathOriginal(folderName, "jpg");
      intent.putExtra("output", Uri.fromFile(new File(filePathOriginal)));
      startActivityResult(intent, CAPTURE_PICTURE_REQUEST_TYPE);
      return filePathOriginal;
   }

   public void startService(Class clazz) {
      super.startService(clazz);
   }

   public void openPodcastPlayer(String url) {
      Intent intent = new Intent(getContext(), PodcastPlayerActivity.class);
      intent.setData(Uri.parse(url));
      startActivityIntent(intent);
   }

   public void openMarket() {
      String appPackageName = "com.worldventures.dreamtrips";
      try {
         startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
      } catch (android.content.ActivityNotFoundException exception) {
         startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
      }
   }

   public void openSettings() {
      Intent intent = new Intent(Settings.ACTION_INTERNAL_STORAGE_SETTINGS);
      intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
      startActivity(intent);
   }

}
