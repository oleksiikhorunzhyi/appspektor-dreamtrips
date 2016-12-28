package com.worldventures.dreamtrips.modules.mapping;

import com.worldventures.dreamtrips.modules.bucketlist.model.converter.BucketCategoryConverter;
import com.worldventures.dreamtrips.modules.bucketlist.model.converter.BucketCoverPhotoConverter;
import com.worldventures.dreamtrips.modules.bucketlist.model.converter.BucketItemSimpleConverter;
import com.worldventures.dreamtrips.modules.bucketlist.model.converter.BucketItemSocializedConverter;
import com.worldventures.dreamtrips.modules.bucketlist.model.converter.BucketLocationConverter;
import com.worldventures.dreamtrips.modules.bucketlist.model.converter.BucketPhotoConverter;
import com.worldventures.dreamtrips.modules.bucketlist.model.converter.BucketTagConverter;
import com.worldventures.dreamtrips.modules.bucketlist.model.converter.BucketTypeConverter;
import com.worldventures.dreamtrips.modules.feed.converter.CommentConverter;
import com.worldventures.dreamtrips.modules.feed.converter.FeedItemConverter;
import com.worldventures.dreamtrips.modules.feed.converter.HashtagSimpleConverter;
import com.worldventures.dreamtrips.modules.feed.converter.HashtagSuggestionConverter;
import com.worldventures.dreamtrips.modules.feed.converter.LinksConverter;
import com.worldventures.dreamtrips.modules.feed.converter.PhotoSocializedConverter;
import com.worldventures.dreamtrips.modules.feed.converter.PostSocializedConverter;
import com.worldventures.dreamtrips.modules.feed.converter.ReversePostAttachmentsConverter;
import com.worldventures.dreamtrips.modules.feed.converter.ReversePostDataConverter;
import com.worldventures.dreamtrips.modules.feed.converter.SimplePostConverter;
import com.worldventures.dreamtrips.modules.infopages.model.FeedbackTypeConverter;
import com.worldventures.dreamtrips.modules.mapping.converter.CircleConverter;
import com.worldventures.dreamtrips.modules.mapping.converter.Converter;
import com.worldventures.dreamtrips.modules.mapping.converter.FeedbackImageAttachmentConverter;
import com.worldventures.dreamtrips.modules.mapping.converter.FlagConverter;
import com.worldventures.dreamtrips.modules.mapping.converter.LocationConverter;
import com.worldventures.dreamtrips.modules.mapping.converter.PrivateProfileConverter;
import com.worldventures.dreamtrips.modules.mapping.converter.PublicProfileConverter;
import com.worldventures.dreamtrips.modules.mapping.converter.RelationshipConverter;
import com.worldventures.dreamtrips.modules.mapping.converter.ReverseLocationConverter;
import com.worldventures.dreamtrips.modules.mapping.converter.ShortProfilesConverter;
import com.worldventures.dreamtrips.modules.mapping.converter.UserAvatarConverter;
import com.worldventures.dreamtrips.modules.mapping.mapper.PodcastsMapper;
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
import com.worldventures.dreamtrips.wallet.domain.converter.SmartCardConverterModule;

import java.util.Set;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.techery.mappery.Mappery;
import io.techery.mappery.MapperyContext;
import timber.log.Timber;

@Module(
      includes = {
            SmartCardConverterModule.class
      }, library = true, complete = false)
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
   Converter provideContentItemConverter() {
      return new ContentItemConverter();
   }

   @Provides(type = Provides.Type.SET)
   @Singleton
   Converter provideFeedbackTypeConverter() {
      return new FeedbackTypeConverter();
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
   Converter provideBucketTagConverter() {
      return new BucketTagConverter();
   }
}
