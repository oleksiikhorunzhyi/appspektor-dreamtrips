package com.worldventures.dreamtrips.modules.video.presenter;

import com.worldventures.dreamtrips.core.utils.LocaleHelper;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsInteractor;
import com.worldventures.dreamtrips.modules.infopages.service.analytics.ViewVideosTabAnalyticAction;
import com.worldventures.dreamtrips.modules.reptools.presenter.TrainingVideosPresenter;
import com.worldventures.dreamtrips.modules.video.service.command.GetMemberVideosCommand;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import rx.Observable;

public class HelpVideosPresenter extends TrainingVideosPresenter<HelpVideosPresenter.View> {

   private static final long DEBOUNCE_VISIBILITY_CHANGE = 600;

   @Inject AnalyticsInteractor analyticsInteractor;

   private boolean analyticSent = true;

   @Override
   protected GetMemberVideosCommand getMemberVideosRequest() {
      return GetMemberVideosCommand.forHelpVideos(videoLanguage);
   }

   @Override
   public void takeView(View view) {
      super.takeView(view);
      listenViewVisibilityChanges();
   }

   private void listenViewVisibilityChanges() {
      view.visibilityStream()
            .debounce(DEBOUNCE_VISIBILITY_CHANGE, TimeUnit.MILLISECONDS)
            .filter(Boolean::booleanValue)
            .filter(value -> view.getUserVisibleHint())
            .compose(bindViewToMainComposer())
            .subscribe(o -> viewShown());
   }

   @Override
   protected boolean isNeedToSendAnalytics() {
      return false;
   }

   @Override
   public void sendAnalytic(String action, String name) {
      // Add analytics when click to video
   }

   @Override
   protected void sendViewTrainingVideoAnalytic() {

   }

   @Override
   protected void localesLoaded(List list) {
      super.localesLoaded(list);

      if (!analyticSent) {
         sendViewAnalytics();
         analyticSent = true;
      }
   }

   public void viewShown() {
      if (videoLanguage != null) {
         sendViewAnalytics();
         analyticSent = true;
      } else analyticSent = false;
   }

   private void sendViewAnalytics() {
      String language = LocaleHelper.obtainLanguageCode(videoLanguage.getLocaleName());
      analyticsInteractor.analyticsActionPipe().send(new ViewVideosTabAnalyticAction(language));
   }

   public interface View extends TrainingVideosPresenter.View {

      Observable<Boolean> visibilityStream();

      boolean getUserVisibleHint();
   }

}
