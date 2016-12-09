package com.worldventures.dreamtrips.modules.mapping;

import com.worldventures.dreamtrips.modules.bucketlist.model.converter.BucketCategoryConverter;
import com.worldventures.dreamtrips.modules.bucketlist.model.converter.BucketCoverPhotoConverter;
import com.worldventures.dreamtrips.modules.bucketlist.model.converter.BucketItemSimpleConverter;
import com.worldventures.dreamtrips.modules.bucketlist.model.converter.BucketItemSocializedConverter;
import com.worldventures.dreamtrips.modules.bucketlist.model.converter.BucketLocationConverter;
import com.worldventures.dreamtrips.modules.bucketlist.model.converter.BucketPhotoBodyConverter;
import com.worldventures.dreamtrips.modules.bucketlist.model.converter.BucketPhotoConverter;
import com.worldventures.dreamtrips.modules.bucketlist.model.converter.BucketTagConverter;
import com.worldventures.dreamtrips.modules.bucketlist.model.converter.BucketTypeConverter;
import com.worldventures.dreamtrips.modules.bucketlist.model.converter.PopularBucketItemFromActivityConverter;
import com.worldventures.dreamtrips.modules.bucketlist.model.converter.PopularBucketItemFromDinningConverter;
import com.worldventures.dreamtrips.modules.bucketlist.model.converter.PopularBucketItemFromLocationConverter;
import com.worldventures.dreamtrips.modules.feed.converter.CommentConverter;
import com.worldventures.dreamtrips.modules.feed.converter.FeedItemConverter;
import com.worldventures.dreamtrips.modules.feed.converter.HashtagSimpleConverter;
import com.worldventures.dreamtrips.modules.feed.converter.HashtagSuggestionConverter;
import com.worldventures.dreamtrips.modules.feed.converter.LinksConverter;
import com.worldventures.dreamtrips.modules.feed.converter.PhotoAttachmentConverter;
import com.worldventures.dreamtrips.modules.feed.converter.PhotoSimpleConverter;
import com.worldventures.dreamtrips.modules.feed.converter.PhotoSocializedConverter;
import com.worldventures.dreamtrips.modules.feed.converter.PhotoWithAuthorConverter;
import com.worldventures.dreamtrips.modules.feed.converter.PostSocializedConverter;
import com.worldventures.dreamtrips.modules.feed.converter.ReversePostAttachmentsConverter;
import com.worldventures.dreamtrips.modules.feed.converter.ReversePostDataConverter;
import com.worldventures.dreamtrips.modules.feed.converter.SimplePostConverter;
import com.worldventures.dreamtrips.modules.friends.model.converter.ApiUserToUserConverter;
import com.worldventures.dreamtrips.modules.friends.model.converter.FriendProfileToUserConverter;
import com.worldventures.dreamtrips.modules.friends.model.converter.FriendСandidateToUserConverter;
import com.worldventures.dreamtrips.modules.friends.model.converter.MutualsConverter;
import com.worldventures.dreamtrips.modules.infopages.model.FeedbackTypeConverter;
import com.worldventures.dreamtrips.modules.mapping.converter.AccountToUserConverter;
import com.worldventures.dreamtrips.modules.mapping.converter.CircleConverter;
import com.worldventures.dreamtrips.modules.mapping.converter.Converter;
import com.worldventures.dreamtrips.modules.mapping.converter.DocumentsConverter;
import com.worldventures.dreamtrips.modules.mapping.converter.FeatureConverter;
import com.worldventures.dreamtrips.modules.mapping.converter.FeedMetaDataConverter;
import com.worldventures.dreamtrips.modules.mapping.converter.FeedbackImageAttachmentConverter;
import com.worldventures.dreamtrips.modules.mapping.converter.FlagConverter;
import com.worldventures.dreamtrips.modules.mapping.converter.ImageConverter;
import com.worldventures.dreamtrips.modules.mapping.converter.InspirationModelsConverter;
import com.worldventures.dreamtrips.modules.mapping.converter.LocationConverter;
import com.worldventures.dreamtrips.modules.mapping.converter.MemberImageConverter;
import com.worldventures.dreamtrips.modules.mapping.converter.PhotoTagConverter;
import com.worldventures.dreamtrips.modules.mapping.converter.PhotoTagsParamsConverter;
import com.worldventures.dreamtrips.modules.mapping.converter.PhotoUpdateParamsConverter;
import com.worldventures.dreamtrips.modules.mapping.converter.PrivateProfileConverter;
import com.worldventures.dreamtrips.modules.mapping.converter.PublicProfileConverter;
import com.worldventures.dreamtrips.modules.mapping.converter.RelationshipConverter;
import com.worldventures.dreamtrips.modules.mapping.converter.ReverseBucketBodyConverter;
import com.worldventures.dreamtrips.modules.mapping.converter.ReverseBucketCoverBodyToUpdateBodyConverter;
import com.worldventures.dreamtrips.modules.mapping.converter.ReverseBucketPostBodyConverter;
import com.worldventures.dreamtrips.modules.mapping.converter.ReverseBucketPostBodyToUpdateBodyConverter;
import com.worldventures.dreamtrips.modules.mapping.converter.ReverseBucketUpdateBodyConverter;
import com.worldventures.dreamtrips.modules.mapping.converter.ReverseLocationConverter;
import com.worldventures.dreamtrips.modules.mapping.converter.SessionConverter;
import com.worldventures.dreamtrips.modules.mapping.converter.SettingConverter;
import com.worldventures.dreamtrips.modules.mapping.converter.ShortProfilesConverter;
import com.worldventures.dreamtrips.modules.mapping.converter.TaggedUserConverter;
import com.worldventures.dreamtrips.modules.mapping.converter.UserAvatarConverter;
import com.worldventures.dreamtrips.modules.mapping.converter.YSBHPhotoConverter;
import com.worldventures.dreamtrips.modules.mapping.mapper.PodcastsMapper;
import com.worldventures.dreamtrips.modules.membership.model.converter.InviteTemplateConverter;
import com.worldventures.dreamtrips.modules.membership.model.converter.InviteTemplateFromInvitationPreviewConverter;
import com.worldventures.dreamtrips.modules.membership.model.converter.SentInviteConverter;
import com.worldventures.dreamtrips.modules.reptools.model.converter.SuccessStoryConverter;
import com.worldventures.dreamtrips.modules.trips.model.converter.ActivityConverter;
import com.worldventures.dreamtrips.modules.trips.model.converter.ContentItemConverter;
import com.worldventures.dreamtrips.modules.trips.model.converter.RegionConverter;
import com.worldventures.dreamtrips.modules.trips.model.converter.TripImageConverter;
import com.worldventures.dreamtrips.modules.trips.model.converter.TripPinToPinConverter;
import com.worldventures.dreamtrips.modules.trips.model.converter.TripWithDetailsToTripConverter;
import com.worldventures.dreamtrips.modules.trips.model.converter.TripWithoutDetailsToTripConverter;
import com.worldventures.dreamtrips.modules.video.model.converter.CategoryConverter;
import com.worldventures.dreamtrips.modules.video.model.converter.VideoConverter;
import com.worldventures.dreamtrips.modules.video.model.converter.VideoLanguageConverter;
import com.worldventures.dreamtrips.modules.video.model.converter.VideoLocaleConverter;
import com.worldventures.dreamtrips.wallet.domain.converter.BankCardToRecordConverter;
import com.worldventures.dreamtrips.wallet.domain.converter.FirmwareResponseToFirmwareDataConverter;
import com.worldventures.dreamtrips.wallet.domain.converter.ProfileAddressToUserAddressConverter;
import com.worldventures.dreamtrips.wallet.domain.converter.RecordToBankCardConverter;
import com.worldventures.dreamtrips.wallet.domain.converter.SmartCardDetailsConverter;

import java.util.Set;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.techery.mappery.Mappery;
import io.techery.mappery.MapperyContext;
import timber.log.Timber;

@Module(
      injects = {},
      library = true, complete = false)
public class MappingModule {

   @Provides
   @Singleton
   MapperyContext provideMappery(Set<Converter> converters) {
      Mappery.Builder builder = new Mappery.Builder();
      for (Converter converter : converters) {
         if (converter.sourceClass() != null && converter.targetClass() != null) {
            builder.map(converter.sourceClass()).to(converter.targetClass(), converter);
         } else {
            Timber.w("sourceClass or targetClass is null, converter %s will be ignored",
                  converter.getClass().getName());
         }
      }
      return builder.build();
   }

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

   @Provides(type = Provides.Type.SET)
   @Singleton
   Converter provideBankCardToRecordConverter() {
      return new BankCardToRecordConverter();
   }

   @Provides(type = Provides.Type.SET)
   @Singleton
   Converter provideSmartCardDetailsConverter() {
      return new SmartCardDetailsConverter();
   }

   @Provides(type = Provides.Type.SET)
   @Singleton
   Converter provideRecordToBankCardConverter() {
      return new RecordToBankCardConverter();
   }

   @Provides(type = Provides.Type.SET)
   @Singleton
   Converter provideProfileAddressToUserAddressConverter() {
      return new ProfileAddressToUserAddressConverter();
   }

   @Provides(type = Provides.Type.SET)
   @Singleton
   Converter provideFirmwareRepsonseToFirmwareConverter() {
      return new FirmwareResponseToFirmwareDataConverter();
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
   Converter provideFriendСandidateToUserConverter() {
      return new FriendСandidateToUserConverter();
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
   Converter provideAccountToUserConverter() {
      return new AccountToUserConverter();
   }

   @Provides(type = Provides.Type.SET)
   @Singleton
   Converter provideSessionConverter() {
      return new SessionConverter();
   }

   @Provides(type = Provides.Type.SET)
   @Singleton
   Converter provideFeatureConverter() {
      return new FeatureConverter();
   }

   @Provides(type = Provides.Type.SET)
   @Singleton
   Converter provideSettingConverter() {
      return new SettingConverter();
   }

   @Provides(type = Provides.Type.SET)
   @Singleton
   Converter provideDocumentsConverter() {
      return new DocumentsConverter();
   }
}
