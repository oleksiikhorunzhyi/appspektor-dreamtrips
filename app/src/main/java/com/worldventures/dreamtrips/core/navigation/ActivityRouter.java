package com.worldventures.dreamtrips.core.navigation;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.webkit.MimeTypeMap;

import com.techery.spares.ui.routing.ActivityBoundRouter;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.presenter.ComponentPresenter;
import com.worldventures.dreamtrips.modules.common.view.activity.ComponentActivity;
import com.worldventures.dreamtrips.modules.common.view.activity.LaunchActivity;
import com.worldventures.dreamtrips.modules.common.view.activity.MainActivity;
import com.worldventures.dreamtrips.modules.common.view.activity.Player360Activity;
import com.worldventures.dreamtrips.modules.player.PodcastPlayerActivity;

import java.io.File;

public class ActivityRouter extends ActivityBoundRouter {

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

   public void open360Activity(String url, String title) {
      Bundle bundle = new Bundle();
      bundle.putString(Player360Activity.EXTRA_URL, url);
      bundle.putString(Player360Activity.EXTRA_TITLE, title);
      startActivity(Player360Activity.class, bundle);
   }

   public void openDefaultShareIntent(Intent intent) {
      startActivityIntent(Intent.createChooser(intent, getActivity().getString(R.string.action_share)));
   }

   public void openComponentActivity(@NonNull Route route, @NonNull Bundle args) {
      args.putSerializable(ComponentPresenter.ROUTE, route);
      startActivityWithArgs(ComponentActivity.class, args);
   }

   public void openComponentActivity(@NonNull Route route, @NonNull Bundle args, int flags) {
      args.putSerializable(ComponentPresenter.ROUTE, route);
      startActivityWithArgs(ComponentActivity.class, args, flags);
   }

   public void startService(Class clazz) {
      super.startService(clazz);
   }

   public void openPodcastPlayer(String url) {
      Intent intent = new Intent(getContext(), PodcastPlayerActivity.class);
      intent.setData(Uri.parse(url));
      startActivityIntent(intent);
   }

   /**
    * {@link ActivityNotFoundException} would be thrown
    * if there was no Activity found to run the given Intent
    *
    * @param url audio file url
    */
   public void openDeviceAudioPlayerForUrl(String url) {
      Intent intent = new Intent(Intent.ACTION_VIEW);
      String extension = MimeTypeMap.getFileExtensionFromUrl(url);
      String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
      intent.setDataAndType(Uri.parse(url), mimeType);
      startActivity(Intent.createChooser(intent, getContext().getString(R.string.complete_action_with)));
   }

   /**
    * {@link ActivityNotFoundException} would be thrown
    * if there was no Activity found to run the given Intent
    *
    * @param path audio file url
    */
   public void openDeviceAudioPlayerForFile(String path) {
      Intent intent = new Intent(Intent.ACTION_VIEW);
      File file = new File(path);
      intent.setDataAndType(Uri.fromFile(file), "audio/*");
      startActivity(Intent.createChooser(intent, getContext().getString(R.string.complete_action_with)));
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
