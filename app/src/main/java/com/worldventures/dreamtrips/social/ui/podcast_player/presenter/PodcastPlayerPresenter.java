package com.worldventures.dreamtrips.social.ui.podcast_player.presenter;

import android.util.Pair;

import com.worldventures.dreamtrips.modules.common.presenter.ActivityPresenter;
import com.worldventures.dreamtrips.social.ui.podcast_player.service.SendPodcastAnalyticsIfNeedAction;
import com.worldventures.dreamtrips.social.ui.tripsimages.service.ProgressAnalyticInteractor;

import javax.inject.Inject;

import rx.subjects.PublishSubject;

public class PodcastPlayerPresenter extends ActivityPresenter {

   @Inject ProgressAnalyticInteractor progressAnalyticInteractor;

   private PublishSubject<Pair<Integer, Integer>> progressObservable = PublishSubject.<Pair<Integer, Integer>>create();
   private final String podcastName;
   private int expectedAnalyticStep = 0;

   public PodcastPlayerPresenter(String podcastName) {
      this.podcastName = podcastName;
   }

   @Override
   public void takeView(View view) {
      super.takeView(view);
      progressObservable
            .flatMap(progressPair -> progressAnalyticInteractor.getSendAnalyticsPipe()
                  .createObservableResult(new SendPodcastAnalyticsIfNeedAction(podcastName, expectedAnalyticStep, progressPair.first, progressPair.second)))
            .map(action -> (Integer) action.getResult())
            .subscribe(nextAnalyticStep -> expectedAnalyticStep = nextAnalyticStep);
   }

   public void onPodcastProgressChanged(int duration, int currentPosition, int bufferPercentage) {
      if (duration != 0) {
         progressObservable.onNext(new Pair<>(currentPosition, duration));
      }
   }

}
