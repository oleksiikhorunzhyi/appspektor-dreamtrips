package com.worldventures.dreamtrips.social.ui.video.presenter;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.core.modules.video.model.Video;
import com.worldventures.core.modules.video.model.VideoCategory;
import com.worldventures.core.modules.video.service.command.GetMemberVideosCommand;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.social.ui.membership.model.MediaHeader;
import com.worldventures.dreamtrips.social.ui.tripsimages.service.analytics.TripImageVideo360DownloadedAction;
import com.worldventures.dreamtrips.social.ui.tripsimages.service.analytics.TripImageVideo360StartedDownloadingAction;
import com.worldventures.dreamtrips.social.ui.tripsimages.service.analytics.TripImageVideo360StartedPlaying;
import com.worldventures.dreamtrips.social.ui.tripsimages.service.analytics.TripImageVideo360ViewedAction;
import com.worldventures.dreamtrips.social.ui.tripsimages.service.analytics.TripImagesTabViewAnalyticsEvent;

import java.util.ArrayList;
import java.util.List;

public class ThreeSixtyVideosPresenter extends PresentationVideosPresenter<ThreeSixtyVideosPresenter.View> {

   @Override
   protected GetMemberVideosCommand getMemberVideosRequest() {
      return GetMemberVideosCommand.forThreeSixtyVideos();
   }

   @Override
   protected boolean isNeedToSendAnalytics() {
      return false;
   }

   @Override
   protected void addCategories(List<VideoCategory> categories) {
      currentItems = new ArrayList<>();

      List<Video> recentVideos = new ArrayList<>();
      List<Video> featuredVideos = new ArrayList<>();

      Queryable.from(categories).forEachR(cat -> {
         recentVideos.addAll(Queryable.from(cat.getVideos()).filter(Video::isRecent).toList());
         featuredVideos.addAll(Queryable.from(cat.getVideos()).filter(Video::isFeatured).toList());
      });

      currentItems.add(new MediaHeader(context.getString(R.string.featured_header)));
      currentItems.addAll(featuredVideos);
      currentItems.add(new MediaHeader(context.getString(R.string.recent_header)));
      currentItems.addAll(recentVideos);

      view.setItems(currentItems);
   }

   public void onSelected() {
      analyticsInteractor.analyticsActionPipe().send(TripImagesTabViewAnalyticsEvent.for360Video());
   }

   public void onVideo360Opened(Video video) {
      analyticsInteractor.analyticsActionPipe().send(new TripImageVideo360ViewedAction(video.getVideoName()));
      analyticsInteractor.analyticsActionPipe().send(new TripImageVideo360StartedPlaying());
   }

   @Override
   protected void sendVideoDownloadingAnalytics(Video video) {
      analyticsInteractor.analyticsActionPipe().send(new TripImageVideo360StartedDownloadingAction());
      analyticsInteractor.analyticsActionPipe().send(new TripImageVideo360DownloadedAction(video.getVideoName()));
   }

   public interface View extends PresentationVideosPresenter.View {}
}
