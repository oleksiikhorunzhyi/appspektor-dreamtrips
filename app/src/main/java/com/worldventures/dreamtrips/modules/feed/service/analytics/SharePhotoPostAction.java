package com.worldventures.dreamtrips.modules.feed.service.analytics;


import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.core.utils.tracksystem.AdobeTracker;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsEvent;
import com.worldventures.dreamtrips.core.utils.tracksystem.Attribute;
import com.worldventures.dreamtrips.core.utils.tracksystem.AttributeMap;
import com.worldventures.dreamtrips.core.utils.tracksystem.BaseAnalyticsAction;
import com.worldventures.dreamtrips.modules.common.model.MediaAttachment;
import com.worldventures.dreamtrips.modules.feed.bundle.CreateEntityBundle;
import com.worldventures.dreamtrips.modules.feed.model.FeedEntityHolder;
import com.worldventures.dreamtrips.modules.feed.model.PhotoCreationItem;
import com.worldventures.dreamtrips.modules.feed.model.TextualPost;
import com.worldventures.dreamtrips.modules.feed.model.feed.hashtag.Hashtag;
import com.worldventures.dreamtrips.modules.trips.model.Location;
import com.worldventures.dreamtrips.modules.tripsimages.model.Photo;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public abstract class SharePhotoPostAction extends BaseAnalyticsAction {
   @Attribute("hashtagging") String hashTags;
   @Attribute("addlocation") String addLocation;
   @Attribute("locationadded") String locationAdded;
   @Attribute("uploadamt") String uploadCount;
   @Attribute("photomethodgallery") String photoMethodGallery;
   @Attribute("photomethodcam") String photoMethodCam;
   @Attribute("photomethodfb") String photoMethodFb;
   @Attribute("photoupload") String photoupload = "1";
   @Attribute("phototagged") String photoTagged;
   @Attribute("phototagnum") String photoTagNum;
   @AttributeMap Map<String, String> attributeMap = new HashMap<>();

   private SharePhotoPostAction() {
   }

   public static SharePhotoPostAction createPostAction(TextualPost textualPost,
         List<PhotoCreationItem> photoCreationItems, CreateEntityBundle.Origin origin) {
      final SharePhotoPostAction sharePostAction = getSharePhotoPostAction(origin);

      if (textualPost.getHashtags() != null && !textualPost.getHashtags().isEmpty()) {
         sharePostAction.hashTags = TextUtils.join(",", Queryable.from(textualPost.getHashtags())
               .map(Hashtag::getName)
               .toArray());
      }

      if (textualPost.getLocation() != null && textualPost.getLocation().getName() != null) {
         sharePostAction.addLocation = "1";
         sharePostAction.locationAdded = textualPost.getLocation().getName();
      }

      sharePostAction.uploadCount = String.valueOf(photoCreationItems.size());
      sharePostAction.photoMethodCam = String.valueOf(Queryable.from(photoCreationItems)
            .count(item -> item.getSource() == MediaAttachment.Source.CAMERA));
      sharePostAction.photoMethodFb = String.valueOf(Queryable.from(photoCreationItems)
            .count(item -> item.getSource() == MediaAttachment.Source.FACEBOOK));
      sharePostAction.photoMethodGallery = String.valueOf(Queryable.from(photoCreationItems)
            .count(item -> item.getSource() == MediaAttachment.Source.GALLERY));

      int photosTaggedCount = Queryable.from(textualPost.getAttachments())
            .filter(feedEntityHolder -> feedEntityHolder.getType() == FeedEntityHolder.Type.PHOTO)
            .map(feedEntityHolder -> (Photo) feedEntityHolder.getItem())
            .map((photo, idx) -> fullfillLocation(sharePostAction, photoCreationItems, photo, idx))
            .sum();

      if (photosTaggedCount > 0) {
         sharePostAction.photoTagged = "1";
         sharePostAction.photoTagNum = String.valueOf(photosTaggedCount);
      }

      return sharePostAction;
   }

   @NonNull
   private static Integer fullfillLocation(SharePhotoPostAction sharePostAction,
         List<PhotoCreationItem> photoCreationItems, Photo photo, int idx) {
      // there is no option to match Photo and PhotoCreationItem, we're assuming that order is not changed
      Location locationFromExif = photoCreationItems.get(idx).getLocationFromExif();
      String photoCoordinates = isLocationValid(locationFromExif)
            ? String.format(Locale.US, "%f,%f", locationFromExif.getLat(), locationFromExif.getLng())
            : "Not Available";
      Location photoLocation = photo.getLocation();
      String addedLocation = !TextUtils.isEmpty(photoLocation.getName())
            ? photoLocation.getName()
            : "Not Available";
      sharePostAction.attributeMap.put("photocoordinates" + (idx + 1), photoCoordinates);
      sharePostAction.attributeMap.put("addedlocation" + (idx + 1), addedLocation);
      return photo.getPhotoTagsCount();
   }

   private static boolean isLocationValid(Location locationFromExif) {
      return locationFromExif != null && locationFromExif.getLat() != 0.0f && locationFromExif.getLng() != 0.0f;
   }

   @NonNull
   private static SharePhotoPostAction getSharePhotoPostAction(CreateEntityBundle.Origin origin) {
      SharePhotoPostAction sharePostAction;
      switch (origin) {
         case PROFILE_TRIP_IMAGES:
            sharePostAction = new SharePhotoPostFromProfileAction();
            break;
         case MY_TRIP_IMAGES:
            sharePostAction = new SharePhotoPostFromMyImagesAction();
            break;
         case MEMBER_TRIP_IMAGES:
            sharePostAction = new SharePhotoPostFromMemberImagesAction();
            break;
         default:
            sharePostAction = new SharePhotoPostFromFeedAction();
            break;
      }
      return sharePostAction;
   }

   @AnalyticsEvent(action = "activity_feed:Photo(s) Added",
                   trackers = AdobeTracker.TRACKER_KEY)
   public static class SharePhotoPostFromFeedAction extends SharePhotoPostAction {
   }

   @AnalyticsEvent(action = "Trip Images:Member Images:Photo(s) Added",
                   trackers = AdobeTracker.TRACKER_KEY)
   public static class SharePhotoPostFromMemberImagesAction extends SharePhotoPostAction {
   }

   @AnalyticsEvent(action = "Trip Images:My Images:Photo(s) Added",
                   trackers = AdobeTracker.TRACKER_KEY)
   public static class SharePhotoPostFromMyImagesAction extends SharePhotoPostAction {
   }

   @AnalyticsEvent(action = "My Profile:Trip Images:Photo(s) Added",
                   trackers = AdobeTracker.TRACKER_KEY)
   public static class SharePhotoPostFromProfileAction extends SharePhotoPostAction {
   }

}
