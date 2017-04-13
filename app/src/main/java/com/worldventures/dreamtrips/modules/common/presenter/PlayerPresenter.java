package com.worldventures.dreamtrips.modules.common.presenter;

import android.util.Pair;

import com.worldventures.dreamtrips.modules.tripsimages.presenter.VideoPlayerPresenter;
import com.worldventures.dreamtrips.modules.tripsimages.service.VideoInteractor;
import com.worldventures.dreamtrips.modules.tripsimages.service.command.SendAnalyticsIfNeedAction;

import javax.inject.Inject;

import io.techery.janet.Command;
import rx.Observable;

public class PlayerPresenter extends VideoPlayerPresenter<PlayerPresenter.View> {

   @Inject VideoInteractor videoInteractor;

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
            .flatMap(progressPair -> videoInteractor.sendAnalyticsIfNeedActionPipe()
                  .createObservableResult(new SendAnalyticsIfNeedAction(launchComponent, language, videoName,
                        expectedAnalyticStep, progressPair.first, progressPair.second)))
            .map(Command::getResult)
            .subscribe(nextAnalyticStep -> expectedAnalyticStep = nextAnalyticStep);
   }

   public interface View extends VideoPlayerPresenter.View {

      Observable<Pair<Long, Long>> videoProgress();
   }

}