package com.worldventures.dreamtrips.social.di;

import com.worldventures.core.converter.Converter;
import com.worldventures.core.modules.infopages.model.converter.DocumentTypeReverseConverter;
import com.worldventures.core.modules.infopages.model.converter.DocumentsConverter;
import com.worldventures.core.modules.infopages.model.converter.FeedbackImageAttachmentConverter;
import com.worldventures.core.modules.infopages.model.converter.FeedbackTypeConverter;
import com.worldventures.core.modules.video.model.converter.CategoryConverter;
import com.worldventures.core.modules.video.model.converter.VideoConverter;
import com.worldventures.core.modules.video.model.converter.VideoLanguageConverter;
import com.worldventures.core.modules.video.model.converter.VideoLocaleConverter;
import com.worldventures.dreamtrips.modules.config.model.converter.ConfigurationConverter;
import com.worldventures.dreamtrips.modules.mapping.converter.LocationConverter;
import com.worldventures.dreamtrips.modules.mapping.converter.ReverseLocationConverter;
import com.worldventures.dreamtrips.modules.mapping.converter.UserAvatarConverter;
import com.worldventures.dreamtrips.modules.trips.model.converter.ActivityConverter;
import com.worldventures.dreamtrips.modules.trips.model.converter.ContentItemConverter;
import com.worldventures.dreamtrips.modules.trips.model.converter.RegionConverter;
import com.worldventures.dreamtrips.modules.trips.model.converter.TripImageConverter;
import com.worldventures.dreamtrips.modules.trips.model.converter.TripPinToPinConverter;
import com.worldventures.dreamtrips.modules.trips.model.converter.TripWithDetailsToTripConverter;
import com.worldventures.dreamtrips.modules.trips.model.converter.TripWithoutDetailsToTripConverter;
import com.worldventures.dreamtrips.social.domain.mapping.CircleConverter;
import com.worldventures.dreamtrips.social.domain.mapping.FeedMetaDataConverter;
import com.worldventures.dreamtrips.social.domain.mapping.FlagConverter;
import com.worldventures.dreamtrips.social.domain.mapping.ImageConverter;
import com.worldventures.dreamtrips.social.domain.mapping.InspirationModelsConverter;
import com.worldventures.dreamtrips.social.domain.mapping.MemberImageConverter;
import com.worldventures.dreamtrips.social.domain.mapping.PhotoTagConverter;
import com.worldventures.dreamtrips.social.domain.mapping.PhotoTagsParamsConverter;
import com.worldventures.dreamtrips.social.domain.mapping.PhotoUpdateParamsConverter;
import com.worldventures.dreamtrips.social.domain.mapping.PodcastsMapper;
import com.worldventures.dreamtrips.social.domain.mapping.PrivateProfileConverter;
import com.worldventures.dreamtrips.social.domain.mapping.PublicProfileConverter;
import com.worldventures.dreamtrips.social.domain.mapping.RelationshipConverter;
import com.worldventures.dreamtrips.social.domain.mapping.ReverseBucketBodyConverter;
import com.worldventures.dreamtrips.social.domain.mapping.ReverseBucketCoverBodyToUpdateBodyConverter;
import com.worldventures.dreamtrips.social.domain.mapping.ReverseBucketPostBodyConverter;
import com.worldventures.dreamtrips.social.domain.mapping.ReverseBucketPostBodyToUpdateBodyConverter;
import com.worldventures.dreamtrips.social.domain.mapping.ReverseBucketUpdateBodyConverter;
import com.worldventures.dreamtrips.social.domain.mapping.ShortProfilesConverter;
import com.worldventures.dreamtrips.social.domain.mapping.TaggedUserConverter;
import com.worldventures.dreamtrips.social.domain.mapping.YSBHPhotoConverter;
import com.worldventures.dreamtrips.social.ui.bucketlist.model.converter.BucketCategoryConverter;
import com.worldventures.dreamtrips.social.ui.bucketlist.model.converter.BucketCoverPhotoConverter;
import com.worldventures.dreamtrips.social.ui.bucketlist.model.converter.BucketDiningItemConverter;
import com.worldventures.dreamtrips.social.ui.bucketlist.model.converter.BucketItemSimpleConverter;
import com.worldventures.dreamtrips.social.ui.bucketlist.model.converter.BucketItemSocializedConverter;
import com.worldventures.dreamtrips.social.ui.bucketlist.model.converter.BucketLocationConverter;
import com.worldventures.dreamtrips.social.ui.bucketlist.model.converter.BucketPhotoBodyConverter;
import com.worldventures.dreamtrips.social.ui.bucketlist.model.converter.BucketPhotoConverter;
import com.worldventures.dreamtrips.social.ui.bucketlist.model.converter.BucketTagConverter;
import com.worldventures.dreamtrips.social.ui.bucketlist.model.converter.BucketTypeConverter;
import com.worldventures.dreamtrips.social.ui.bucketlist.model.converter.PopularBucketItemFromActivityConverter;
import com.worldventures.dreamtrips.social.ui.bucketlist.model.converter.PopularBucketItemFromDinningConverter;
import com.worldventures.dreamtrips.social.ui.bucketlist.model.converter.PopularBucketItemFromLocationConverter;
import com.worldventures.dreamtrips.social.ui.feed.converter.CommentConverter;
import com.worldventures.dreamtrips.social.ui.feed.converter.FeedItemConverter;
import com.worldventures.dreamtrips.social.ui.feed.converter.HashtagSimpleConverter;
import com.worldventures.dreamtrips.social.ui.feed.converter.HashtagSuggestionConverter;
import com.worldventures.dreamtrips.social.ui.feed.converter.LinksConverter;
import com.worldventures.dreamtrips.social.ui.feed.converter.PhotoAttachmentConverter;
import com.worldventures.dreamtrips.social.ui.feed.converter.PhotoSimpleConverter;
import com.worldventures.dreamtrips.social.ui.feed.converter.PhotoSocializedConverter;
import com.worldventures.dreamtrips.social.ui.feed.converter.PhotoWithAuthorConverter;
import com.worldventures.dreamtrips.social.ui.feed.converter.PostSocializedConverter;
import com.worldventures.dreamtrips.social.ui.feed.converter.ReversePostAttachmentsConverter;
import com.worldventures.dreamtrips.social.ui.feed.converter.ReversePostDataConverter;
import com.worldventures.dreamtrips.social.ui.feed.converter.SimplePostConverter;
import com.worldventures.dreamtrips.social.ui.feed.converter.VideoAttachmentConverter;
import com.worldventures.dreamtrips.social.service.friends.model.converter.ApiUserToUserConverter;
import com.worldventures.dreamtrips.social.service.friends.model.converter.FriendCandidateToUserConverter;
import com.worldventures.dreamtrips.social.service.friends.model.converter.FriendProfileToUserConverter;
import com.worldventures.dreamtrips.social.service.friends.model.converter.MutualsConverter;
import com.worldventures.dreamtrips.social.domain.converter.InviteTemplateConverter;
import com.worldventures.dreamtrips.social.domain.converter.InviteTemplateFromInvitationPreviewConverter;
import com.worldventures.dreamtrips.social.domain.converter.SentInviteConverter;
import com.worldventures.dreamtrips.social.ui.reptools.model.converter.SuccessStoryConverter;
import com.worldventures.dreamtrips.social.ui.tripsimages.model.converter.MediaEntityConverter;
import com.worldventures.dreamtrips.social.ui.tripsimages.model.converter.VideoSocializedConverter;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(library = true, complete = false)
public class SocialMappingModule {

   @Provides(type = Provides.Type.SET)
   @Singleton
   Converter provideFlagsConverter() {
      return new FlagConverter();
   }

   @Provides(type = Provides.Type.SET)
   @Singleton
   Converter providePinConverter() {
      return new TripPinToPinConverter();
   }

   @Provides(type = Provides.Type.SET)
   @Singleton
   Converter provideTripConverter() {
      return new TripWithoutDetailsToTripConverter();
   }

   @Provides(type = Provides.Type.SET)
   @Singleton
   Converter provideTripWithDetailsConverter() {
      return new TripWithDetailsToTripConverter();
   }

   @Provides(type = Provides.Type.SET)
   @Singleton
   Converter provideShortProfilesConverter() {
      return new ShortProfilesConverter();
   }

   @Provides(type = Provides.Type.SET)
   @Singleton
   Converter provideActivityConverter() {
      return new ActivityConverter();
   }

   @Provides(type = Provides.Type.SET)
   @Singleton
   Converter provideRegionConverter() {
      return new RegionConverter();
   }

   @Provides(type = Provides.Type.SET)
   @Singleton
   Converter provideTripImageConverter() {
      return new TripImageConverter();
   }

   @Provides(type = Provides.Type.SET)
   @Singleton
   Converter provideMemberImageConverter() {
      return new MemberImageConverter();
   }

   @Provides(type = Provides.Type.SET)
   @Singleton
   Converter provideContentItemConverter() {
      return new ContentItemConverter();
   }

   @Provides
   @Singleton
   PodcastsMapper providePodcastsMapper() {
      return new PodcastsMapper();
   }

   @Provides(type = Provides.Type.SET)
   @Singleton
   Converter providePrivateProfileConverter() {
      return new PrivateProfileConverter();
   }

   @Provides(type = Provides.Type.SET)
   @Singleton
   Converter providePublicProfileConverter() {
      return new PublicProfileConverter();
   }

   @Provides(type = Provides.Type.SET)
   @Singleton
   Converter provideRelationshipConverter() {
      return new RelationshipConverter();
   }

   @Provides(type = Provides.Type.SET)
   @Singleton
   Converter provideCircleConverter() {
      return new CircleConverter();
   }

   @Provides(type = Provides.Type.SET)
   @Singleton
   Converter provideUserAvatarConverter() {
      return new UserAvatarConverter();
   }

   @Provides(type = Provides.Type.SET)
   @Singleton
   Converter provideVideoCategoryConverter() {
      return new CategoryConverter();
   }

   @Provides(type = Provides.Type.SET)
   @Singleton
   Converter provideVideoConverter() {
      return new VideoConverter();
   }

   @Provides(type = Provides.Type.SET)
   @Singleton
   Converter provideVideoLanguageConverter() {
      return new VideoLanguageConverter();
   }

   //todo move to core
   @Provides(type = Provides.Type.SET)
   @Singleton
   Converter provideVideoLocaleConverter() {
      return new VideoLocaleConverter();
   }

   @Provides(type = Provides.Type.SET)
   @Singleton
   Converter provideFeedbackBodyConverter() {
      return new FeedbackImageAttachmentConverter();
   }

   @Provides(type = Provides.Type.SET)
   @Singleton
   Converter provideInspirationModelsConverter() {
      return new InspirationModelsConverter();
   }

   @Provides(type = Provides.Type.SET)
   @Singleton
   Converter provideFeedbackTypeConverter() {
      return new FeedbackTypeConverter();
   }

   @Provides(type = Provides.Type.SET)
   @Singleton
   Converter provideReverseLocationConverter() {
      return new ReverseLocationConverter();
   }

   @Provides(type = Provides.Type.SET)
   @Singleton
   Converter provideLocationConverter() {
      return new LocationConverter();
   }

   @Provides(type = Provides.Type.SET)
   @Singleton
   Converter provideFeedItemConverter() {
      return new FeedItemConverter();
   }

   @Provides(type = Provides.Type.SET)
   @Singleton
   Converter provideLinksConverter() {
      return new LinksConverter();
   }

   @Provides(type = Provides.Type.SET)
   @Singleton
   Converter provideCommentConverter() {
      return new CommentConverter();
   }

   @Provides(type = Provides.Type.SET)
   @Singleton
   Converter provideReversePostAttachmentsConverter() {
      return new ReversePostAttachmentsConverter();
   }

   @Provides(type = Provides.Type.SET)
   @Singleton
   Converter provideReversePostConverter() {
      return new ReversePostDataConverter();
   }

   @Provides(type = Provides.Type.SET)
   @Singleton
   Converter provideSimplePostConverter() {
      return new SimplePostConverter();
   }

   @Provides(type = Provides.Type.SET)
   @Singleton
   Converter provideSocializedPostConverter() {
      return new PostSocializedConverter();
   }

   @Provides(type = Provides.Type.SET)
   @Singleton
   Converter provideSocializedPhotoConverter() {
      return new PhotoSocializedConverter();
   }

   @Provides(type = Provides.Type.SET)
   @Singleton
   Converter provideHashTagSimpleConverter() {
      return new HashtagSimpleConverter();
   }

   @Provides(type = Provides.Type.SET)
   @Singleton
   Converter provideHashtagSuggestionConverter() {
      return new HashtagSuggestionConverter();
   }

   @Provides(type = Provides.Type.SET)
   @Singleton
   Converter provideBucketSocializedConverter() {
      return new BucketItemSocializedConverter();
   }

   @Provides(type = Provides.Type.SET)
   @Singleton
   Converter provideBucketSimpleConverter() {
      return new BucketItemSimpleConverter();
   }

   @Provides(type = Provides.Type.SET)
   @Singleton
   Converter provideBucketDiningConverter() {
      return new BucketDiningItemConverter();
   }

   @Provides(type = Provides.Type.SET)
   @Singleton
   Converter provideBucketCategoryConverter() {
      return new BucketCategoryConverter();
   }

   @Provides(type = Provides.Type.SET)
   @Singleton
   Converter provideBucketTypeConverter() {
      return new BucketTypeConverter();
   }

   @Provides(type = Provides.Type.SET)
   @Singleton
   Converter provideBucketLocationConverter() {
      return new BucketLocationConverter();
   }

   @Provides(type = Provides.Type.SET)
   @Singleton
   Converter provideBucketCoverPhotoConverter() {
      return new BucketCoverPhotoConverter();
   }

   @Provides(type = Provides.Type.SET)
   @Singleton
   Converter provideBucketPhotoConverter() {
      return new BucketPhotoConverter();
   }

   @Provides(type = Provides.Type.SET)
   @Singleton
   Converter provideBucketPhotoBodyConverter() {
      return new BucketPhotoBodyConverter();
   }

   @Provides(type = Provides.Type.SET)
   @Singleton
   Converter provideBucketTagConverter() {
      return new BucketTagConverter();
   }

   @Provides(type = Provides.Type.SET)
   @Singleton
   Converter provideReverseBucketBodyConverter() {
      return new ReverseBucketBodyConverter();
   }

   @Provides(type = Provides.Type.SET)
   @Singleton
   Converter provideReverseBucketCoverBodyToUpdateBodyConverter() {
      return new ReverseBucketCoverBodyToUpdateBodyConverter();
   }

   @Provides(type = Provides.Type.SET)
   @Singleton
   Converter provideReverseBucketPostBodyToUpdateBodyConverter() {
      return new ReverseBucketPostBodyToUpdateBodyConverter();
   }

   @Provides(type = Provides.Type.SET)
   @Singleton
   Converter provideReverseBucketUpdateBodyConverter() {
      return new ReverseBucketUpdateBodyConverter();
   }

   @Provides(type = Provides.Type.SET)
   @Singleton
   Converter provideReversePostBucketBodyConverter() {
      return new ReverseBucketPostBodyConverter();
   }

   @Provides(type = Provides.Type.SET)
   @Singleton
   Converter provideInviteTemplateConverter() {
      return new InviteTemplateConverter();
   }

   @Provides(type = Provides.Type.SET)
   @Singleton
   Converter provideSentInviteConverter() {
      return new SentInviteConverter();
   }

   @Provides(type = Provides.Type.SET)
   @Singleton
   Converter provideSuccessStoryConverter() {
      return new SuccessStoryConverter();
   }

   @Provides(type = Provides.Type.SET)
   @Singleton
   Converter provideImageConverter() {
      return new ImageConverter();
   }

   @Provides(type = Provides.Type.SET)
   @Singleton
   Converter providePhotoTagConverter() {
      return new PhotoTagConverter();
   }

   @Provides(type = Provides.Type.SET)
   @Singleton
   Converter provideYSBHPhotoConverter() {
      return new YSBHPhotoConverter();
   }

   @Provides(type = Provides.Type.SET)
   @Singleton
   Converter provideTaggedUserConverter() {
      return new TaggedUserConverter();
   }

   @Provides(type = Provides.Type.SET)
   @Singleton
   Converter providePhotoTagsParamsConverter() {
      return new PhotoTagsParamsConverter();
   }

   @Provides(type = Provides.Type.SET)
   @Singleton
   Converter providePhotoUpdateParamsConverter() {
      return new PhotoUpdateParamsConverter();
   }

   @Provides(type = Provides.Type.SET)
   @Singleton
   Converter provideInviteTemplateFromInvitationPreviewConverter() {
      return new InviteTemplateFromInvitationPreviewConverter();
   }

   @Provides(type = Provides.Type.SET)
   @Singleton
   Converter provideApiUserToUserConverter() {
      return new ApiUserToUserConverter();
   }

   @Provides(type = Provides.Type.SET)
   @Singleton
   Converter provideFriendCandidateToUserConverter() {
      return new FriendCandidateToUserConverter();
   }

   @Provides(type = Provides.Type.SET)
   @Singleton
   Converter provideFriendProfileToUserConverter() {
      return new FriendProfileToUserConverter();
   }

   @Provides(type = Provides.Type.SET)
   @Singleton
   Converter provideMutualsConverter() {
      return new MutualsConverter();
   }

   @Provides(type = Provides.Type.SET)
   @Singleton
   Converter providePopularBucketItemFromActivityConverter() {
      return new PopularBucketItemFromActivityConverter();
   }

   @Provides(type = Provides.Type.SET)
   @Singleton
   Converter providePopularBucketItemFromDinningConverter() {
      return new PopularBucketItemFromDinningConverter();
   }

   @Provides(type = Provides.Type.SET)
   @Singleton
   Converter providePopularBucketItemFromLocationConverter() {
      return new PopularBucketItemFromLocationConverter();
   }

   @Provides(type = Provides.Type.SET)
   @Singleton
   Converter provideFeedMetaDataConverter() {
      return new FeedMetaDataConverter();
   }

   @Provides(type = Provides.Type.SET)
   @Singleton
   Converter providePhotoAttachmentConverter() {
      return new PhotoAttachmentConverter();
   }

   @Provides(type = Provides.Type.SET)
   @Singleton
   Converter providePhotoSimpleConverter() {
      return new PhotoSimpleConverter();
   }

   @Provides(type = Provides.Type.SET)
   @Singleton
   Converter providePhotoWithAuthorConverter() {
      return new PhotoWithAuthorConverter();
   }

   @Provides(type = Provides.Type.SET)
   @Singleton
   Converter provideDocumentsConverter() {
      return new DocumentsConverter();
   }

   @Provides(type = Provides.Type.SET)
   @Singleton
   Converter provideDocumentsTypeReverseConverter() {
      return new DocumentTypeReverseConverter();
   }

   @Provides(type = Provides.Type.SET)
   @Singleton
   Converter provideVideoAttachmentConverter() {
      return new VideoAttachmentConverter();
   }

   @Provides(type = Provides.Type.SET)
   @Singleton
   Converter provideConfigurationConverter() {
      return new ConfigurationConverter();
   }

   @Provides(type = Provides.Type.SET)
   @Singleton
   Converter provideMediaEntityConverter() {
      return new MediaEntityConverter();
   }

   @Provides(type = Provides.Type.SET)
   @Singleton
   Converter provideVideoSocializedConverter() {
      return new VideoSocializedConverter();
   }

}
