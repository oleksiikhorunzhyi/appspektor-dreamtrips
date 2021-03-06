package com.worldventures.dreamtrips.wallet;


import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

import com.worldventures.core.modules.infopages.bundle.FeedbackImageAttachmentsBundle;
import com.worldventures.core.modules.infopages.model.FeedbackImageAttachment;
import com.worldventures.dreamtrips.core.navigation.ActivityRouter;
import com.worldventures.dreamtrips.core.navigation.ToolbarConfig;
import com.worldventures.dreamtrips.core.navigation.router.NavigationConfig;
import com.worldventures.dreamtrips.core.navigation.router.NavigationConfigBuilder;
import com.worldventures.dreamtrips.core.navigation.router.Router;
import com.worldventures.dreamtrips.social.ui.activity.PlayerActivity;
import com.worldventures.dreamtrips.social.ui.infopages.view.fragment.FeedbackImageAttachmentsFragment;
import com.worldventures.wallet.ui.common.navigation.CoreNavigator;

import java.util.List;

class CoreNavigatorImpl implements CoreNavigator {

   private final Activity activity;
   private final Router router;

   CoreNavigatorImpl(Activity activity, Router router) {
      this.activity = activity;
      this.router = router;
   }

   @Override
   public void goFeedBackImageAttachments(int position, List<FeedbackImageAttachment> attachments) {
      NavigationConfig config = NavigationConfigBuilder.forActivity()
            .data(new FeedbackImageAttachmentsBundle(position,
                  attachments))
            .toolbarConfig(ToolbarConfig.Builder.create().visible(false).build())
            .build();
      router.moveTo(FeedbackImageAttachmentsFragment.class, config);
   }

   @Override
   public void goVideoPlayer(Uri uri, String uid, String videoName, Class launchComponent, String videoLanguage) {
      Intent intent = new Intent(activity, PlayerActivity.class).setData(uri)
            .putExtra(PlayerActivity.EXTRA_VIDEO_NAME, videoName)
            .putExtra(PlayerActivity.EXTRA_LAUNCH_COMPONENT, launchComponent)
            .putExtra(PlayerActivity.EXTRA_VIDEO_UID, uid)
            .putExtra(PlayerActivity.EXTRA_LANGUAGE, videoLanguage);
      activity.startActivity(intent);
   }

   @Override
   public void openLoginActivity() {
      new ActivityRouter(activity).openLaunch();
   }
}
