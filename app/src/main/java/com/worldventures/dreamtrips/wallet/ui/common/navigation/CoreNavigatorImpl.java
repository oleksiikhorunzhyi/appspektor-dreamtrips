package com.worldventures.dreamtrips.wallet.ui.common.navigation;


import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.navigation.ToolbarConfig;
import com.worldventures.dreamtrips.core.navigation.router.NavigationConfig;
import com.worldventures.dreamtrips.core.navigation.router.NavigationConfigBuilder;
import com.worldventures.dreamtrips.core.navigation.router.Router;
import com.worldventures.dreamtrips.modules.infopages.bundle.FeedbackImageAttachmentsBundle;
import com.worldventures.dreamtrips.modules.infopages.model.FeedbackImageAttachment;

import java.util.List;

public class CoreNavigatorImpl implements CoreNavigator {

   private final Router router;

   public CoreNavigatorImpl(Router router) {
      this.router = router;
   }

   @Override
   public void goFeedBackImageAttachments(int position, List<FeedbackImageAttachment> attachments) {
      NavigationConfig config = NavigationConfigBuilder.forActivity()
            .data(new FeedbackImageAttachmentsBundle(position,
                  attachments))
            .toolbarConfig(ToolbarConfig.Builder.create().visible(false).build())
            .build();
      router.moveTo(Route.FEEDBACK_IMAGE_ATTACHMENTS, config);
   }
}
