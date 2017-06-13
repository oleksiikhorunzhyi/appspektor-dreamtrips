package com.worldventures.dreamtrips.modules.common.presenter;

import android.util.Pair;

import com.worldventures.dreamtrips.modules.tripsimages.presenter.VideoPlayerPresenter;
import com.worldventures.dreamtrips.modules.tripsimages.service.ProgressAnalyticInteractor;
import com.worldventures.dreamtrips.modules.tripsimages.service.command.SendVideoAnalyticsIfNeedAction;

import javax.inject.Inject;

import rx.Observable;

public class PlayerPresenter extends VideoPlayerPresenter<PlayerPresenter.View> {

   @Inject ProgressAnalyticInteractor progressAnalyticInteractor;

   private final Class launchComponent;
   private final String language;
   private final String videoName;

   private volatile int expectedAnalyticStep;

   public PlayerPresenter(Class launchComponent, String language, String videoName) {
      this.launchComponent = launchComponent;
      this.language = language;
      this.videoName = videoName;
   }

   @Override
   public void takeView(PlayerPresenter.View view) {
      super.takeView(view);
      listenVideoProgress();
   }

   private void listenVideoProgress() {
      view.videoProgress()
            .compose(bindView())
            .flatMap(progressPair -> progressAnalyticInteractor.sendProgressAnalyticsIfNeedActionPipe()
                  .createObservableResult(new SendVideoAnalyticsIfNeedAction(launchComponent, language, videoName,
                        expectedAnalyticStep, progressPair.first, progressPair.second)))
            .map(action -> (Integer) action.getResult())
            .subscribe(nextAnalyticStep -> expectedAnalyticStep = nextAnalyticStep);
   }

   public interface View extends VideoPlayerPresenter.View {

      Observable<Pair<Long, Long>> videoProgress();
   }

}