package com.worldventures.wallet.ui.common.navigation;


import android.net.Uri;

import com.worldventures.core.modules.infopages.model.FeedbackImageAttachment;

import java.util.List;

public interface CoreNavigator {

   void goFeedBackImageAttachments(int position, List<FeedbackImageAttachment> attachments);

   void goVideoPlayer(Uri uri, String uid, String videoName, Class launchComponent, String videoLanguage);

   void openLoginActivity();
}
