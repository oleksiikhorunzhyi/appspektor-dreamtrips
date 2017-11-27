package com.worldventures.dreamtrips.social.di;

import com.worldventures.core.janet.SessionActionPipeCreator;
import com.worldventures.core.modules.auth.api.command.LogoutAction;
import com.worldventures.dreamtrips.modules.common.command.ClearStoragesCommand;
import com.worldventures.dreamtrips.modules.common.service.ClearStoragesInteractor;
import com.worldventures.dreamtrips.modules.config.service.command.LoadConfigurationCommand;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.comment.fragments.CreateReviewPhotoCreationItemCommand;
import com.worldventures.dreamtrips.modules.media_picker.service.command.GetVideoMetadataCommand;
import com.worldventures.dreamtrips.modules.media_picker.service.command.RecognizeFacesCommand;
import com.worldventures.dreamtrips.modules.trips.command.CheckTripsByUidCommand;
import com.worldventures.dreamtrips.modules.trips.command.GetActivitiesCommand;
import com.worldventures.dreamtrips.modules.trips.command.GetRegionsCommand;
import com.worldventures.dreamtrips.modules.trips.command.GetTripDetailsCommand;
import com.worldventures.dreamtrips.modules.trips.command.GetTripsByUidCommand;
import com.worldventures.dreamtrips.modules.trips.command.GetTripsCommand;
import com.worldventures.dreamtrips.modules.trips.command.GetTripsLocationsCommand;
import com.worldventures.dreamtrips.social.service.invites.AddContactCommand;
import com.worldventures.dreamtrips.social.service.invites.CreateFilledInviteCommand;
import com.worldventures.dreamtrips.social.service.invites.GetInviteTemplatesCommand;
import com.worldventures.dreamtrips.social.service.invites.SendInvitesCommand;
import com.worldventures.dreamtrips.social.service.invites.UpdateContactsCommand;
import com.worldventures.dreamtrips.social.service.invites.UpdateContactsWithSentInvitesCommand;
import com.worldventures.dreamtrips.social.ui.background_uploading.service.command.CreatePostCompoundOperationCommand;
import com.worldventures.dreamtrips.social.ui.background_uploading.service.command.UploadVideoFileCommand;
import com.worldventures.dreamtrips.social.ui.background_uploading.service.command.video.FeedItemsVideoProcessingStatusCommand;
import com.worldventures.dreamtrips.social.ui.bucketlist.service.action.CreateBucketItemCommand;
import com.worldventures.dreamtrips.social.ui.bucketlist.service.action.UpdateBucketItemCommand;
import com.worldventures.dreamtrips.social.ui.bucketlist.service.command.AddBucketItemPhotoCommand;
import com.worldventures.dreamtrips.social.ui.bucketlist.service.command.BucketListCommand;
import com.worldventures.dreamtrips.social.ui.bucketlist.service.command.ChangeBucketListOrderCommand;
import com.worldventures.dreamtrips.social.ui.bucketlist.service.command.DeleteBucketItemCommand;
import com.worldventures.dreamtrips.social.ui.bucketlist.service.command.DeleteItemPhotoCommand;
import com.worldventures.dreamtrips.social.ui.bucketlist.service.command.FindBucketItemByPhotoCommand;
import com.worldventures.dreamtrips.social.ui.bucketlist.service.command.GetCategoriesCommand;
import com.worldventures.dreamtrips.social.ui.bucketlist.service.command.GetPopularBucketItemSuggestionsCommand;
import com.worldventures.dreamtrips.social.ui.bucketlist.service.command.GetPopularBucketItemsCommand;
import com.worldventures.dreamtrips.social.ui.bucketlist.service.command.MergeBucketItemPhotosWithStorageCommand;
import com.worldventures.dreamtrips.social.ui.bucketlist.service.command.TranslateBucketItemCommand;
import com.worldventures.dreamtrips.social.ui.feed.service.command.ChangeFeedEntityLikedStatusCommand;
import com.worldventures.dreamtrips.social.ui.feed.service.command.CreateCommentCommand;
import com.worldventures.dreamtrips.social.ui.feed.service.command.CreatePhotosCommand;
import com.worldventures.dreamtrips.social.ui.feed.service.command.CreatePostCommand;
import com.worldventures.dreamtrips.social.ui.feed.service.command.CreateVideoCommand;
import com.worldventures.dreamtrips.social.ui.feed.service.command.DeleteCommentCommand;
import com.worldventures.dreamtrips.social.ui.feed.service.command.DeletePostCommand;
import com.worldventures.dreamtrips.social.ui.feed.service.command.EditCommentCommand;
import com.worldventures.dreamtrips.social.ui.feed.service.command.EditPostCommand;
import com.worldventures.dreamtrips.social.ui.feed.service.command.FeedByHashtagCommand;
import com.worldventures.dreamtrips.social.ui.feed.service.command.GetAccountFeedCommand;
import com.worldventures.dreamtrips.social.ui.feed.service.command.GetAccountTimelineCommand;
import com.worldventures.dreamtrips.social.ui.feed.service.command.GetCommentsCommand;
import com.worldventures.dreamtrips.social.ui.feed.service.command.GetFeedEntityCommand;
import com.worldventures.dreamtrips.social.ui.feed.service.command.GetNotificationsCommand;
import com.worldventures.dreamtrips.social.ui.feed.service.command.GetUserTimelineCommand;
import com.worldventures.dreamtrips.social.ui.feed.service.command.HashtagSuggestionCommand;
import com.worldventures.dreamtrips.social.ui.feed.service.command.LikeEntityCommand;
import com.worldventures.dreamtrips.social.ui.feed.service.command.MarkNotificationAsReadCommand;
import com.worldventures.dreamtrips.social.ui.feed.service.command.MarkNotificationsAsReadCommand;
import com.worldventures.dreamtrips.social.ui.feed.service.command.PostCreatedCommand;
import com.worldventures.dreamtrips.social.ui.feed.service.command.ProcessAttachmentsAndPost;
import com.worldventures.dreamtrips.social.ui.feed.service.command.SuggestedPhotoCommand;
import com.worldventures.dreamtrips.social.ui.feed.service.command.TranslateTextCachedCommand;
import com.worldventures.dreamtrips.social.ui.feed.service.command.TranslateUidItemCommand;
import com.worldventures.dreamtrips.social.ui.feed.service.command.UnlikeEntityCommand;
import com.worldventures.dreamtrips.social.ui.flags.command.FlagItemCommand;
import com.worldventures.dreamtrips.social.ui.flags.command.GetFlagsCommand;
import com.worldventures.dreamtrips.social.ui.friends.service.command.AcceptAllFriendRequestsCommand;
import com.worldventures.dreamtrips.social.ui.friends.service.command.ActOnFriendRequestCommand;
import com.worldventures.dreamtrips.social.ui.friends.service.command.AddFriendCommand;
import com.worldventures.dreamtrips.social.ui.friends.service.command.DeleteFriendRequestCommand;
import com.worldventures.dreamtrips.social.ui.friends.service.command.GetCirclesCommand;
import com.worldventures.dreamtrips.social.ui.friends.service.command.GetFriendsCommand;
import com.worldventures.dreamtrips.social.ui.friends.service.command.GetLikersCommand;
import com.worldventures.dreamtrips.social.ui.friends.service.command.GetMutualFriendsCommand;
import com.worldventures.dreamtrips.social.ui.friends.service.command.GetRequestsCommand;
import com.worldventures.dreamtrips.social.ui.friends.service.command.GetSearchUsersCommand;
import com.worldventures.dreamtrips.social.ui.friends.service.command.RemoveFriendCommand;
import com.worldventures.dreamtrips.social.ui.membership.service.command.GetPodcastsCommand;
import com.worldventures.dreamtrips.social.ui.podcast_player.service.SendPodcastAnalyticsIfNeedAction;
import com.worldventures.dreamtrips.social.ui.profile.service.command.AddFriendToCircleCommand;
import com.worldventures.dreamtrips.social.ui.profile.service.command.GetPrivateProfileCommand;
import com.worldventures.dreamtrips.social.ui.profile.service.command.GetPublicProfileCommand;
import com.worldventures.dreamtrips.social.ui.profile.service.command.RemoveFriendFromCircleCommand;
import com.worldventures.dreamtrips.social.ui.profile.service.command.UploadAvatarCommand;
import com.worldventures.dreamtrips.social.ui.profile.service.command.UploadBackgroundCommand;
import com.worldventures.dreamtrips.social.ui.reptools.service.command.GetSuccessStoriesCommand;
import com.worldventures.dreamtrips.social.ui.reptools.service.command.LikeSuccessStoryCommand;
import com.worldventures.dreamtrips.social.ui.reptools.service.command.UnlikeSuccessStoryCommand;
import com.worldventures.dreamtrips.social.ui.tripsimages.service.command.AddPhotoTagsCommand;
import com.worldventures.dreamtrips.social.ui.tripsimages.service.command.CheckVideoProcessingStatusCommand;
import com.worldventures.dreamtrips.social.ui.tripsimages.service.command.CreatePhotoCreationItemCommand;
import com.worldventures.dreamtrips.social.ui.tripsimages.service.command.DeletePhotoCommand;
import com.worldventures.dreamtrips.social.ui.tripsimages.service.command.DeletePhotoTagsCommand;
import com.worldventures.dreamtrips.social.ui.tripsimages.service.command.DeleteVideoCommand;
import com.worldventures.dreamtrips.social.ui.tripsimages.service.command.DownloadImageCommand;
import com.worldventures.dreamtrips.social.ui.tripsimages.service.command.EditPhotoCommand;
import com.worldventures.dreamtrips.social.ui.tripsimages.service.command.EditPhotoWithTagsCommand;
import com.worldventures.dreamtrips.social.ui.tripsimages.service.command.GetInspireMePhotosCommand;
import com.worldventures.dreamtrips.social.ui.tripsimages.service.command.GetMemberMediaCommand;
import com.worldventures.dreamtrips.social.ui.tripsimages.service.command.GetUsersMediaCommand;
import com.worldventures.dreamtrips.social.ui.tripsimages.service.command.GetYSBHPhotosCommand;
import com.worldventures.dreamtrips.social.ui.tripsimages.service.command.SendAnalyticsIfNeedAction;
import com.worldventures.dreamtrips.social.ui.tripsimages.service.command.SendVideoAnalyticsIfNeedAction;
import com.worldventures.dreamtrips.social.ui.tripsimages.service.command.TranslatePhotoCommand;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(injects = {
      BucketListCommand.class,
      DeleteItemPhotoCommand.class,
      FindBucketItemByPhotoCommand.class,
      AddBucketItemPhotoCommand.class,
      MergeBucketItemPhotosWithStorageCommand.class,
      GetCirclesCommand.class,
      GetCommentsCommand.class,
      GetPodcastsCommand.class,
      SuggestedPhotoCommand.class,
      GetAccountFeedCommand.Refresh.class,
      GetAccountFeedCommand.LoadNext.class,
      GetAccountTimelineCommand.Refresh.class,
      GetAccountTimelineCommand.LoadNext.class,
      GetUserTimelineCommand.Refresh.class,
      GetUserTimelineCommand.LoadNext.class,
      FeedByHashtagCommand.Refresh.class,
      FeedByHashtagCommand.LoadNext.class,
      TranslateUidItemCommand.TranslateCommentCommand.class,
      TranslateUidItemCommand.TranslateFeedEntityCommand.class,
      TranslateTextCachedCommand.class,
      GetSuccessStoriesCommand.class,
      LikeSuccessStoryCommand.class,
      UnlikeSuccessStoryCommand.class,
      GetTripDetailsCommand.class,
      GetActivitiesCommand.class,
      GetRegionsCommand.class,
      GetTripsCommand.class,
      GetTripsLocationsCommand.class,
      GetTripsByUidCommand.class,
      HashtagSuggestionCommand.class,
      ClearStoragesCommand.class,
      CreatePostCommand.class,
      PostCreatedCommand.class,
      EditPostCommand.class,
      DeletePostCommand.class,
      CreateCommentCommand.class,
      EditCommentCommand.class,
      LikeEntityCommand.class,
      UnlikeEntityCommand.class,
      DeleteCommentCommand.class,
      DeleteFriendRequestCommand.class,
      AcceptAllFriendRequestsCommand.class,
      AddFriendCommand.class,
      RemoveFriendCommand.class,
      DeleteFriendRequestCommand.class,
      ActOnFriendRequestCommand.Accept.class,
      ActOnFriendRequestCommand.Reject.class,
      GetNotificationsCommand.class,
      MarkNotificationsAsReadCommand.class,
      MarkNotificationAsReadCommand.class,
      GetPrivateProfileCommand.class,
      UploadAvatarCommand.class,
      UploadBackgroundCommand.class,
      GetPublicProfileCommand.class,
      CreatePhotoCreationItemCommand.class,
      CreateReviewPhotoCreationItemCommand.class,
      GetInspireMePhotosCommand.class,
      GetYSBHPhotosCommand.class,
      DeletePhotoCommand.class,
      DeletePhotoTagsCommand.class,
      SocialJanetCommandModule.class,
      DownloadImageCommand.class,
      AddPhotoTagsCommand.class,
      EditPhotoCommand.class,
      EditPhotoWithTagsCommand.class,
      GetInviteTemplatesCommand.class,
      CreateFilledInviteCommand.class,
      UpdateContactsCommand.class,
      UpdateContactsWithSentInvitesCommand.class,
      GetFriendsCommand.class,
      GetLikersCommand.class,
      GetMutualFriendsCommand.class,
      GetSearchUsersCommand.class,
      GetRequestsCommand.class,
      SendInvitesCommand.class,
      AddFriendToCircleCommand.class,
      RemoveFriendFromCircleCommand.class,
      GetCategoriesCommand.class,
      GetPopularBucketItemSuggestionsCommand.class,
      GetPopularBucketItemsCommand.class,
      GetFeedEntityCommand.class,
      CreatePhotosCommand.class,
      ProcessAttachmentsAndPost.class,
      CreateBucketItemCommand.class,
      UpdateBucketItemCommand.class,
      ChangeBucketListOrderCommand.class,
      DeleteBucketItemCommand.class,
      ChangeFeedEntityLikedStatusCommand.class,
      CreatePostCompoundOperationCommand.class,
      CheckTripsByUidCommand.class,
      TranslatePhotoCommand.class,
      TranslateBucketItemCommand.class,
      SendAnalyticsIfNeedAction.class,
      SendVideoAnalyticsIfNeedAction.class,
      SendPodcastAnalyticsIfNeedAction.class,
      RecognizeFacesCommand.class,
      UploadVideoFileCommand.class,
      CreateVideoCommand.class,
      FeedItemsVideoProcessingStatusCommand.class,
      GetVideoMetadataCommand.class,
      LoadConfigurationCommand.class,
      GetMemberMediaCommand.class,
      GetUsersMediaCommand.class,
      DeleteVideoCommand.class,
      CheckVideoProcessingStatusCommand.class,
      FlagItemCommand.class,
      GetFlagsCommand.class,
      AddContactCommand.class,
}, complete = false, library = true)
public class SocialJanetCommandModule {

   @Provides
   @Singleton
   ClearStoragesInteractor provideClearStoragesInteractor(SessionActionPipeCreator sessionActionPipeCreator) {
      return new ClearStoragesInteractor(sessionActionPipeCreator);
   }

   @Provides(type = Provides.Type.SET)
   LogoutAction provideClearStoragesInteractorLogoutAction(ClearStoragesInteractor clearStoragesInteractor) {
      return () -> clearStoragesInteractor.clearMemoryStorageActionPipe().send(new ClearStoragesCommand());
   }
}
