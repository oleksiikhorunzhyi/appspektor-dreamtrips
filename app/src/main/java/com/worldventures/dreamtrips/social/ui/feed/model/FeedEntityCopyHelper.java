package com.worldventures.dreamtrips.social.ui.feed.model;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.modules.trips.model.TripModel;
import com.worldventures.dreamtrips.social.ui.bucketlist.model.BucketItem;
import com.worldventures.dreamtrips.social.ui.feed.model.video.Video;
import com.worldventures.dreamtrips.social.ui.tripsimages.model.Photo;

import java.util.List;

//TODO this class is workaround for mutability issues in feeds, should be removed when feed models will be rewritten
public abstract class FeedEntityCopyHelper {

   public static List<FeedItem> copyFeedItems(List<FeedItem> items) {
      if (items == null) {
         return null;
      } else {
         return Queryable.from(items)
               .map(element -> {
                  FeedItem feedItem = FeedItem.create(element.getItem());
                  feedItem.setAction(element.getAction());
                  feedItem.setCreatedAt(element.getCreatedAt());
                  feedItem.setLinks(element.getLinks());
                  feedItem.setMetaData(element.getMetaData());
                  feedItem.setType(element.getType());
                  feedItem.setReadAt(element.getReadAt());
                  feedItem.setItem(copyFeedEntity(element.getItem()));
                  return feedItem;
               })
               .toList();
      }
   }

   public static FeedEntity copyFeedEntity(FeedEntity feedEntity) {
      if (feedEntity instanceof BucketItem) {
         return copyBucketItem((BucketItem) feedEntity);
      } else if (feedEntity instanceof TextualPost) {
         return copyTextualPost((TextualPost) feedEntity);
      } else if (feedEntity instanceof Photo) {
         return copyPhoto((Photo) feedEntity);
      } else if (feedEntity instanceof TripModel) {
         return copyTrip((TripModel) feedEntity);
      } else if (feedEntity instanceof Video) {
         return copyVideo((Video) feedEntity);
      } else {
         return new UndefinedFeedEntity();
      }
   }

   private static BucketItem copyBucketItem(BucketItem bucketItem) {
      BucketItem newBucketItem = new BucketItem();
      fillWithCommonData(bucketItem, newBucketItem);

      newBucketItem.setName(bucketItem.getName());
      newBucketItem.setStatus(bucketItem.getStatus());
      newBucketItem.setTargetDate(bucketItem.getTargetDate());
      newBucketItem.setCompletionDate(bucketItem.getCompletionDate());
      newBucketItem.setDescription(bucketItem.getDescription());
      newBucketItem.setTags(bucketItem.getTags());
      newBucketItem.setCategory(bucketItem.getCategory());
      newBucketItem.setFriends(bucketItem.getFriendsList());
      newBucketItem.setPhotos(bucketItem.getPhotos());
      newBucketItem.setCoverPhoto(bucketItem.getCoverPhoto());
      newBucketItem.setLocation(bucketItem.getLocation());
      newBucketItem.setType(bucketItem.getType());
      newBucketItem.setLink(bucketItem.getUrl());
      newBucketItem.setDining(bucketItem.getDining());
      newBucketItem.setSelected(bucketItem.isSelected());
      newBucketItem.setTranslationDescription(bucketItem.getTranslationDescription());
      return newBucketItem;
   }

   private static TextualPost copyTextualPost(TextualPost textualPost) {
      TextualPost newTextualPost = new TextualPost();
      fillWithCommonData(textualPost, newTextualPost);

      newTextualPost.setDescription(textualPost.getDescription());
      newTextualPost.setAttachments(textualPost.getAttachments());
      newTextualPost.setLocation(textualPost.getLocation());
      newTextualPost.setHashtags(textualPost.getHashtags());
      return newTextualPost;
   }

   private static Photo copyPhoto(Photo photo) {
      Photo newPhoto = new Photo();
      fillWithCommonData(photo, newPhoto);

      newPhoto.setTitle(photo.getTitle());
      newPhoto.setShotAt(photo.getShotAt());
      newPhoto.setCreatedAt(photo.getCreatedAt());
      newPhoto.setLocation(photo.getLocation());
      newPhoto.setTags(photo.getTags());
      newPhoto.setUrl(photo.getUrl());
      newPhoto.setPhotoTags(photo.getPhotoTags());
      newPhoto.setPhotoTagsCount(photo.getPhotoTagsCount());
      newPhoto.setWidth(photo.getWidth());
      newPhoto.setHeight(photo.getHeight());
      return newPhoto;
   }

   private static TripModel copyTrip(TripModel tripModel) {
      TripModel newTrip = new TripModel();
      fillWithCommonData(tripModel, newTrip);

      newTrip.setTripId(tripModel.getTripId());
      newTrip.setName(tripModel.getName());
      newTrip.setDescription(tripModel.getDescription());
      newTrip.setThumbnailUrl(tripModel.getThumbnailUrl());
      newTrip.setImageUrls(tripModel.getImageUrls());
      newTrip.setDuration(tripModel.getDuration());
      newTrip.setHasMultipleDates(tripModel.getHasMultipleDates());
      newTrip.setSoldOut(tripModel.isSoldOut());
      newTrip.setFeatured(tripModel.isFeatured());
      newTrip.setPlatinum(tripModel.isPlatinum());
      newTrip.setInBucketList(tripModel.isInBucketList());
      newTrip.setRewardsLimit(tripModel.getRewardsLimit());
      newTrip.setPrice(tripModel.getPrice());
      newTrip.setLocation(tripModel.getLocation());
      newTrip.setAvailabilityDates(tripModel.getAvailabilityDates());
      newTrip.setContent(tripModel.getContent());
      return newTrip;
   }

   private static Video copyVideo(Video video) {
      Video newVideo = new Video();
      fillWithCommonData(video, newVideo);

      newVideo.setUploadId(video.getUploadId());
      newVideo.setThumbnail(video.getThumbnail());
      newVideo.setAspectRatio(video.getAspectRatio());
      newVideo.setCreatedAt(video.getCreatedAt());
      newVideo.setDuration(video.getDuration());
      newVideo.setQualities(video.getQualities());
      return newVideo;
   }

   private static void fillWithCommonData(BaseFeedEntity baseFeedEntity, BaseFeedEntity newBaseFeedEntity) {
      newBaseFeedEntity.setUid(baseFeedEntity.getUid());
      newBaseFeedEntity.setOwner(baseFeedEntity.getOwner());
      newBaseFeedEntity.setCommentsCount(baseFeedEntity.getCommentsCount());
      newBaseFeedEntity.setComments(baseFeedEntity.getComments());
      newBaseFeedEntity.setLiked(baseFeedEntity.isLiked());
      newBaseFeedEntity.setLikesCount(baseFeedEntity.getLikesCount());
      newBaseFeedEntity.setLanguage(baseFeedEntity.getLanguage());
      newBaseFeedEntity.setFirstLikerName(baseFeedEntity.getFirstLikerName());
      newBaseFeedEntity.setTranslation(baseFeedEntity.getTranslation());
      newBaseFeedEntity.setTranslated(baseFeedEntity.isTranslated());
   }
}
