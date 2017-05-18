package com.worldventures.dreamtrips.modules.common;

import com.worldventures.dreamtrips.core.api.uploadery.SimpleUploaderyCommand;
import com.worldventures.dreamtrips.core.api.uploadery.SmartCardUploaderyCommand;
import com.worldventures.dreamtrips.core.api.uploadery.UploaderyImageCommand;
import com.worldventures.dreamtrips.modules.auth.api.command.LoginCommand;
import com.worldventures.dreamtrips.modules.auth.api.command.LogoutCommand;
import com.worldventures.dreamtrips.modules.auth.api.command.UnsubribeFromPushCommand;
import com.worldventures.dreamtrips.modules.auth.api.command.UpdateUserCommand;
import com.worldventures.dreamtrips.modules.background_uploading.service.command.CreatePostCompoundOperationCommand;
import com.worldventures.dreamtrips.modules.bucketlist.service.action.CreateBucketItemCommand;
import com.worldventures.dreamtrips.modules.bucketlist.service.action.UpdateBucketItemCommand;
import com.worldventures.dreamtrips.modules.bucketlist.service.command.AddBucketItemPhotoCommand;
import com.worldventures.dreamtrips.modules.bucketlist.service.command.BucketListCommand;
import com.worldventures.dreamtrips.modules.bucketlist.service.command.ChangeBucketListOrderCommand;
import com.worldventures.dreamtrips.modules.bucketlist.service.command.DeleteBucketItemCommand;
import com.worldventures.dreamtrips.modules.bucketlist.service.command.DeleteItemPhotoCommand;
import com.worldventures.dreamtrips.modules.bucketlist.service.command.FindBucketItemByPhotoCommand;
import com.worldventures.dreamtrips.modules.bucketlist.service.command.GetCategoriesCommand;
import com.worldventures.dreamtrips.modules.bucketlist.service.command.GetPopularBucketItemSuggestionsCommand;
import com.worldventures.dreamtrips.modules.bucketlist.service.command.GetPopularBucketItemsCommand;
import com.worldventures.dreamtrips.modules.bucketlist.service.command.MergeBucketItemPhotosWithStorageCommand;
import com.worldventures.dreamtrips.modules.bucketlist.service.command.TranslateBucketItemCommand;
import com.worldventures.dreamtrips.modules.common.api.janet.command.AcceptTermsCommand;
import com.worldventures.dreamtrips.modules.common.api.janet.command.ClearStoragesCommand;
import com.worldventures.dreamtrips.modules.common.api.janet.command.GetCirclesCommand;
import com.worldventures.dreamtrips.modules.common.api.janet.command.SubscribeToPushNotificationsCommand;
import com.worldventures.dreamtrips.modules.common.api.janet.command.TripsFilterDataCommand;
import com.worldventures.dreamtrips.modules.common.command.CopyFileCommand;
import com.worldventures.dreamtrips.modules.common.command.DeleteCachedModelCommand;
import com.worldventures.dreamtrips.modules.common.command.DownloadCachedModelCommand;
import com.worldventures.dreamtrips.modules.common.command.InitializeCommand;
import com.worldventures.dreamtrips.modules.common.command.ResetCachedModelsInProgressCommand;
import com.worldventures.dreamtrips.modules.common.service.CleanTempDirectoryCommand;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.comment.fragments.CreateReviewPhotoCreationItemCommand;
import com.worldventures.dreamtrips.modules.facebook.service.command.GetAlbumsCommand;
import com.worldventures.dreamtrips.modules.feed.service.command.ChangeFeedEntityLikedStatusCommand;
import com.worldventures.dreamtrips.modules.feed.service.command.CreateCommentCommand;
import com.worldventures.dreamtrips.modules.feed.service.command.CreatePhotosCommand;
import com.worldventures.dreamtrips.modules.feed.service.command.CreatePostCommand;
import com.worldventures.dreamtrips.modules.feed.service.command.DeleteCommentCommand;
import com.worldventures.dreamtrips.modules.feed.service.command.DeletePostCommand;
import com.worldventures.dreamtrips.modules.feed.service.command.EditCommentCommand;
import com.worldventures.dreamtrips.modules.feed.service.command.EditPostCommand;
import com.worldventures.dreamtrips.modules.feed.service.command.FeedByHashtagCommand;
import com.worldventures.dreamtrips.modules.feed.service.command.GetAccountFeedCommand;
import com.worldventures.dreamtrips.modules.feed.service.command.GetAccountTimelineCommand;
import com.worldventures.dreamtrips.modules.feed.service.command.GetCommentsCommand;
import com.worldventures.dreamtrips.modules.feed.service.command.GetFeedEntityCommand;
import com.worldventures.dreamtrips.modules.feed.service.command.GetNotificationsCommand;
import com.worldventures.dreamtrips.modules.feed.service.command.GetUserTimelineCommand;
import com.worldventures.dreamtrips.modules.feed.service.command.HashtagSuggestionCommand;
import com.worldventures.dreamtrips.modules.feed.service.command.LikeEntityCommand;
import com.worldventures.dreamtrips.modules.feed.service.command.MarkNotificationAsReadCommand;
import com.worldventures.dreamtrips.modules.feed.service.command.MarkNotificationsAsReadCommand;
import com.worldventures.dreamtrips.modules.feed.service.command.PostCreatedCommand;
import com.worldventures.dreamtrips.modules.feed.service.command.SuggestedPhotoCommand;
import com.worldventures.dreamtrips.modules.feed.service.command.TranslateTextCachedCommand;
import com.worldventures.dreamtrips.modules.feed.service.command.TranslateUidItemCommand;
import com.worldventures.dreamtrips.modules.feed.service.command.UnlikeEntityCommand;
import com.worldventures.dreamtrips.modules.friends.service.command.AcceptAllFriendRequestsCommand;
import com.worldventures.dreamtrips.modules.friends.service.command.ActOnFriendRequestCommand;
import com.worldventures.dreamtrips.modules.friends.service.command.AddFriendCommand;
import com.worldventures.dreamtrips.modules.friends.service.command.DeleteFriendRequestCommand;
import com.worldventures.dreamtrips.modules.friends.service.command.GetFriendsCommand;
import com.worldventures.dreamtrips.modules.friends.service.command.GetLikersCommand;
import com.worldventures.dreamtrips.modules.friends.service.command.GetMutualFriendsCommand;
import com.worldventures.dreamtrips.modules.friends.service.command.GetRequestsCommand;
import com.worldventures.dreamtrips.modules.friends.service.command.GetSearchUsersCommand;
import com.worldventures.dreamtrips.modules.friends.service.command.RemoveFriendCommand;
import com.worldventures.dreamtrips.modules.infopages.service.command.GetDocumentsCommand;
import com.worldventures.dreamtrips.modules.infopages.service.command.GetFeedbackCommand;
import com.worldventures.dreamtrips.modules.infopages.service.command.SendFeedbackCommand;
import com.worldventures.dreamtrips.modules.infopages.service.command.UploadFeedbackAttachmentCommand;
import com.worldventures.dreamtrips.modules.membership.service.command.CreateFilledInviteTemplateCommand;
import com.worldventures.dreamtrips.modules.membership.service.command.GetFilledInviteTemplateCommand;
import com.worldventures.dreamtrips.modules.membership.service.command.GetInviteTemplatesCommand;
import com.worldventures.dreamtrips.modules.membership.service.command.GetPhoneContactsCommand;
import com.worldventures.dreamtrips.modules.membership.service.command.GetPodcastsCommand;
import com.worldventures.dreamtrips.modules.membership.service.command.GetSentInvitesCommand;
import com.worldventures.dreamtrips.modules.membership.service.command.SendInvitesCommand;
import com.worldventures.dreamtrips.modules.player.service.SendPodcastAnalyticsIfNeedAction;
import com.worldventures.dreamtrips.modules.profile.service.command.AddFriendToCircleCommand;
import com.worldventures.dreamtrips.modules.profile.service.command.GetPrivateProfileCommand;
import com.worldventures.dreamtrips.modules.profile.service.command.GetPublicProfileCommand;
import com.worldventures.dreamtrips.modules.profile.service.command.RemoveFriendFromCircleCommand;
import com.worldventures.dreamtrips.modules.profile.service.command.UploadAvatarCommand;
import com.worldventures.dreamtrips.modules.profile.service.command.UploadBackgroundCommand;
import com.worldventures.dreamtrips.modules.reptools.service.command.GetSuccessStoriesCommand;
import com.worldventures.dreamtrips.modules.reptools.service.command.LikeSuccessStoryCommand;
import com.worldventures.dreamtrips.modules.reptools.service.command.UnlikeSuccessStoryCommand;
import com.worldventures.dreamtrips.modules.settings.command.SettingsCommand;
import com.worldventures.dreamtrips.modules.trips.command.CheckTripsByUidCommand;
import com.worldventures.dreamtrips.modules.trips.command.GetActivitiesCommand;
import com.worldventures.dreamtrips.modules.trips.command.GetRegionsCommand;
import com.worldventures.dreamtrips.modules.trips.command.GetTripDetailsCommand;
import com.worldventures.dreamtrips.modules.trips.command.GetTripsByUidCommand;
import com.worldventures.dreamtrips.modules.trips.command.GetTripsCommand;
import com.worldventures.dreamtrips.modules.trips.command.GetTripsLocationsCommand;
import com.worldventures.dreamtrips.modules.tripsimages.service.command.AddPhotoTagsCommand;
import com.worldventures.dreamtrips.modules.tripsimages.service.command.CreatePhotoCreationItemCommand;
import com.worldventures.dreamtrips.modules.tripsimages.service.command.DeletePhotoCommand;
import com.worldventures.dreamtrips.modules.tripsimages.service.command.DeletePhotoTagsCommand;
import com.worldventures.dreamtrips.modules.tripsimages.service.command.DownloadImageCommand;
import com.worldventures.dreamtrips.modules.tripsimages.service.command.EditPhotoCommand;
import com.worldventures.dreamtrips.modules.tripsimages.service.command.EditPhotoWithTagsCommand;
import com.worldventures.dreamtrips.modules.tripsimages.service.command.GetInspireMePhotosCommand;
import com.worldventures.dreamtrips.modules.tripsimages.service.command.GetMembersPhotosCommand;
import com.worldventures.dreamtrips.modules.tripsimages.service.command.GetUserPhotosCommand;
import com.worldventures.dreamtrips.modules.tripsimages.service.command.GetYSBHPhotosCommand;
import com.worldventures.dreamtrips.modules.tripsimages.service.command.SendVideoAnalyticsIfNeedAction;
import com.worldventures.dreamtrips.modules.tripsimages.service.command.TranslatePhotoCommand;
import com.worldventures.dreamtrips.modules.tripsimages.service.command.PaginatedTripImagesCommand;
import com.worldventures.dreamtrips.modules.video.service.command.GetMemberVideosCommand;
import com.worldventures.dreamtrips.modules.video.service.command.GetVideoLocalesCommand;
import com.worldventures.dreamtrips.modules.video.service.command.MigrateFromCachedEntity;

import dagger.Module;

@Module(injects = {
      CopyFileCommand.class,
      UploaderyImageCommand.class,
      SimpleUploaderyCommand.class,
      SmartCardUploaderyCommand.class,
      BucketListCommand.class,
      DeleteItemPhotoCommand.class,
      FindBucketItemByPhotoCommand.class,
      AddBucketItemPhotoCommand.class,
      MergeBucketItemPhotosWithStorageCommand.class,
      TripsFilterDataCommand.class,
      GetCirclesCommand.class,
      GetCommentsCommand.class,
      LoginCommand.class,
      InitializeCommand.class,
      UpdateUserCommand.class,
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
      AcceptTermsCommand.class,
      GetSuccessStoriesCommand.class,
      LikeSuccessStoryCommand.class,
      UnlikeSuccessStoryCommand.class,
      UnsubribeFromPushCommand.class,
      GetTripDetailsCommand.class,
      GetActivitiesCommand.class,
      GetRegionsCommand.class,
      GetTripsCommand.class,
      GetTripsLocationsCommand.class,
      GetTripsByUidCommand.class,
      HashtagSuggestionCommand.class,
      SubscribeToPushNotificationsCommand.class,
      ClearStoragesCommand.class,
      SubscribeToPushNotificationsCommand.class,
      SettingsCommand.class,
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
      DeleteCachedModelCommand.class,
      DownloadCachedModelCommand.class,
      ResetCachedModelsInProgressCommand.class,
      LogoutCommand.class,
      GetPrivateProfileCommand.class,
      UploadAvatarCommand.class,
      UploadBackgroundCommand.class,
      GetPublicProfileCommand.class,
      SendFeedbackCommand.class,
      GetFeedbackCommand.class,
      UploadFeedbackAttachmentCommand.class,
      CreatePhotoCreationItemCommand.class,
      CreateReviewPhotoCreationItemCommand.class,
      GetMemberVideosCommand.class,
      GetVideoLocalesCommand.class,
      GetInspireMePhotosCommand.class,
      GetMembersPhotosCommand.class,
      GetUserPhotosCommand.class,
      GetYSBHPhotosCommand.class,
      PaginatedTripImagesCommand.class,
      DeletePhotoCommand.class,
      DeletePhotoTagsCommand.class,
      SocialJanetCommandModule.class,
      DownloadImageCommand.class,
      AddPhotoTagsCommand.class,
      EditPhotoCommand.class,
      EditPhotoWithTagsCommand.class,
      GetInviteTemplatesCommand.class,
      CreateFilledInviteTemplateCommand.class,
      GetFilledInviteTemplateCommand.class,
      GetPhoneContactsCommand.class,
      GetSentInvitesCommand.class,
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
      CreateBucketItemCommand.class,
      UpdateBucketItemCommand.class,
      ChangeBucketListOrderCommand.class,
      DeleteBucketItemCommand.class,
      ChangeFeedEntityLikedStatusCommand.class,
      CreatePostCompoundOperationCommand.class,
      CleanTempDirectoryCommand.class,
      CheckTripsByUidCommand.class,
      GetDocumentsCommand.class,
      TranslatePhotoCommand.class,
      TranslateBucketItemCommand.class,
      SendVideoAnalyticsIfNeedAction.class,
      SendPodcastAnalyticsIfNeedAction.class,
      GetAlbumsCommand.class,
      MigrateFromCachedEntity.class
}, complete = false, library = true)
public class SocialJanetCommandModule {}
