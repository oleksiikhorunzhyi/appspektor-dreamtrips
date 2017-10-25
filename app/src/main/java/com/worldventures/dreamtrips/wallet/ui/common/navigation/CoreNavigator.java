package com.worldventures.dreamtrips.wallet.ui.common.navigation;


import android.net.Uri;

import com.worldventures.core.modules.infopages.model.FeedbackImageAttachment;

import java.util.List;

public interface CoreNavigator {

   void goFeedBackImageAttachments(int position, List<FeedbackImageAttachment> attachments);

   void goVideoPlayer(Uri uri, String videoName, Class launchComponent, String videoLanguage);

   void openLoginActivity();
}
