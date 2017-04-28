package com.worldventures.dreamtrips.modules.video.presenter;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.membership.model.MediaHeader;
import com.worldventures.dreamtrips.modules.tripsimages.service.analytics.TripImagesTabViewAnalyticsEvent;
import com.worldventures.dreamtrips.modules.video.model.Video;
import com.worldventures.dreamtrips.modules.video.model.VideoCategory;
import com.worldventures.dreamtrips.modules.video.service.command.GetMemberVideosCommand;

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

   public interface View extends PresentationVideosPresenter.View {}
}
