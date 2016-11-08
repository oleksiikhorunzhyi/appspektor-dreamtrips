package com.worldventures.dreamtrips.modules.video.presenter;

import com.worldventures.dreamtrips.modules.reptools.presenter.TrainingVideosPresenter;
import com.worldventures.dreamtrips.modules.video.service.command.GetMemberVideosCommand;

public class HelpVideosPresenter extends TrainingVideosPresenter {

   @Override
   protected GetMemberVideosCommand getMemberVideosRequest() {
      return GetMemberVideosCommand.forHelpVideos(videoLanguage);
   }

   @Override
   protected boolean isNeedToSendAnalytics() {
      return false;
   }

   @Override
   public void sendAnalytic(String action, String name) {
      // Add analytics when click to video
   }
}
