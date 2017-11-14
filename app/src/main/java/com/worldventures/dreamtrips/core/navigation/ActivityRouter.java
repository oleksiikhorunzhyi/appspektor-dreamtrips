package com.worldventures.dreamtrips.core.navigation;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.worldventures.core.ui.view.routing.ActivityBoundRouter;
import com.worldventures.dreamtrips.modules.common.view.activity.LaunchActivity;
import com.worldventures.dreamtrips.social.ui.activity.Player360Activity;
import com.worldventures.dreamtrips.social.ui.activity.PlayerActivity;
import com.worldventures.dreamtrips.social.ui.activity.SocialMainActivity;
import com.worldventures.dreamtrips.social.ui.podcast_player.PodcastPlayerActivity;

public class ActivityRouter extends ActivityBoundRouter {

   public ActivityRouter(Activity activity) {
      super(activity);
   }

   public void openMain() {
      startActivity(SocialMainActivity.class);
   }

   public void openMainWithComponent(String key) {
      Bundle bundle = new Bundle();
      bundle.putString(SocialMainActivity.COMPONENT_KEY, key);
      startActivity(SocialMainActivity.class, bundle);
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

   public void openPodcastPlayer(String url, String name) {
      Intent intent = new Intent(getContext(), PodcastPlayerActivity.class);
      intent.setData(Uri.parse(url));
      intent.putExtra(PodcastPlayerActivity.PODCAST_NAME_KEY, name);
      startActivityIntent(intent);
   }
}
