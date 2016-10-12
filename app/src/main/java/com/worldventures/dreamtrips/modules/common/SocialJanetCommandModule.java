package com.worldventures.dreamtrips.modules.common;

import com.worldventures.dreamtrips.core.api.uploadery.SimpleUploaderyCommand;
import com.worldventures.dreamtrips.core.api.uploadery.UploaderyImageCommand;
import com.worldventures.dreamtrips.modules.auth.api.command.LoginCommand;
import com.worldventures.dreamtrips.modules.auth.api.command.LogoutCommand;
import com.worldventures.dreamtrips.modules.auth.api.command.UnsubribeFromPushCommand;
import com.worldventures.dreamtrips.modules.auth.api.command.UpdateUserCommand;
import com.worldventures.dreamtrips.modules.bucketlist.service.command.AddBucketItemPhotoCommand;
import com.worldventures.dreamtrips.modules.bucketlist.service.command.BucketListCommand;
import com.worldventures.dreamtrips.modules.bucketlist.service.command.DeleteItemPhotoCommand;
import com.worldventures.dreamtrips.modules.bucketlist.service.command.FindBucketItemByPhotoCommand;
import com.worldventures.dreamtrips.modules.bucketlist.service.command.MergeBucketItemPhotosWithStorageCommand;
import com.worldventures.dreamtrips.modules.common.api.janet.command.AcceptTermsCommand;
import com.worldventures.dreamtrips.modules.common.api.janet.command.CirclesCommand;
import com.worldventures.dreamtrips.modules.common.api.janet.command.ClearStoragesCommand;
import com.worldventures.dreamtrips.modules.common.api.janet.command.SubscribeToPushNotificationsCommand;
import com.worldventures.dreamtrips.modules.common.api.janet.command.TripsFilterDataCommand;
import com.worldventures.dreamtrips.modules.common.command.DeleteCachedEntityCommand;
import com.worldventures.dreamtrips.modules.common.command.DownloadCachedEntityCommand;
import com.worldventures.dreamtrips.modules.common.command.ResetCachedEntitiesInProgressCommand;
import com.worldventures.dreamtrips.modules.feed.service.command.FeedByHashtagCommand;
import com.worldventures.dreamtrips.modules.feed.service.command.GetAccountFeedCommand;
import com.worldventures.dreamtrips.modules.feed.service.command.GetAccountTimelineCommand;
import com.worldventures.dreamtrips.modules.feed.service.command.GetCommentsCommand;
import com.worldventures.dreamtrips.modules.feed.service.command.GetNotificationsCommand;
import com.worldventures.dreamtrips.modules.feed.service.command.GetUserTimelineCommand;
import com.worldventures.dreamtrips.modules.feed.service.command.HashtagSuggestionCommand;
import com.worldventures.dreamtrips.modules.feed.service.command.MarkNotificationAsReadCommand;
import com.worldventures.dreamtrips.modules.feed.service.command.MarkNotificationsAsReadCommand;
import com.worldventures.dreamtrips.modules.feed.service.command.SuggestedPhotoCommand;
import com.worldventures.dreamtrips.modules.feed.service.command.TranslateTextCachedCommand;
import com.worldventures.dreamtrips.modules.feed.service.command.TranslateUidItemCommand;
import com.worldventures.dreamtrips.modules.friends.janet.AcceptAllFriendRequestsCommand;
import com.worldventures.dreamtrips.modules.friends.janet.ActOnFriendRequestCommand;
import com.worldventures.dreamtrips.modules.friends.janet.AddFriendCommand;
import com.worldventures.dreamtrips.modules.friends.janet.DeleteFriendRequestCommand;
import com.worldventures.dreamtrips.modules.friends.janet.RemoveFriendCommand;
import com.worldventures.dreamtrips.modules.infopages.service.command.GetFeedbackCommand;
import com.worldventures.dreamtrips.modules.infopages.service.command.SendFeedbackCommand;
import com.worldventures.dreamtrips.modules.infopages.service.command.UploadFeedbackAttachmentCommand;
import com.worldventures.dreamtrips.modules.membership.command.GetPodcastsCommand;
import com.worldventures.dreamtrips.modules.profile.service.command.GetPrivateProfileCommand;
import com.worldventures.dreamtrips.modules.profile.service.command.GetPublicProfileCommand;
import com.worldventures.dreamtrips.modules.profile.service.command.UploadAvatarCommand;
import com.worldventures.dreamtrips.modules.profile.service.command.UploadBackgroundCommand;
import com.worldventures.dreamtrips.modules.settings.command.SettingsCommand;
import com.worldventures.dreamtrips.modules.trips.command.GetActivitiesCommand;
import com.worldventures.dreamtrips.modules.trips.command.GetRegionsCommand;
import com.worldventures.dreamtrips.modules.trips.command.GetTripDetailsCommand;
import com.worldventures.dreamtrips.modules.trips.command.GetTripsByUidCommand;
import com.worldventures.dreamtrips.modules.trips.command.GetTripsCommand;
import com.worldventures.dreamtrips.modules.trips.command.GetTripsLocationsCommand;

import dagger.Module;

@Module(injects = {
      UploaderyImageCommand.class,
      SimpleUploaderyCommand.class,
      BucketListCommand.class,
      DeleteItemPhotoCommand.class,
      FindBucketItemByPhotoCommand.class,
      AddBucketItemPhotoCommand.class,
      MergeBucketItemPhotosWithStorageCommand.class,
      TripsFilterDataCommand.class,
      CirclesCommand.class,
      GetCommentsCommand.class,
      LoginCommand.class,
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
      TranslateUidItemCommand.TranslatePostCommand.class,
      TranslateTextCachedCommand.class,
      AcceptTermsCommand.class,
      UnsubribeFromPushCommand.class,
      GetTripDetailsCommand.class,
      GetActivitiesCommand.class,
      GetRegionsCommand.class,
      GetTripsCommand.class,
      GetTripsLocationsCommand.class,
      GetTripsByUidCommand.class,
      HashtagSuggestionCommand.class,
      SubscribeToPushNotificationsCommand.class,
      HashtagSuggestionCommand.class,
      ClearStoragesCommand.class, SubscribeToPushNotificationsCommand.class,
      SettingsCommand.class,
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
      DeleteCachedEntityCommand.class,
      DownloadCachedEntityCommand.class,
      ResetCachedEntitiesInProgressCommand.class,
      LogoutCommand.class,
      GetPrivateProfileCommand.class,
      UploadAvatarCommand.class,
      UploadBackgroundCommand.class,
      GetPublicProfileCommand.class,
      SendFeedbackCommand.class,
      UploadFeedbackAttachmentCommand.class,
      GetFeedbackCommand.class
}, complete = false, library = true)
public class SocialJanetCommandModule {}
