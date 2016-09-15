package com.worldventures.dreamtrips.modules.common;

import com.worldventures.dreamtrips.core.api.uploadery.SimpleUploaderyCommand;
import com.worldventures.dreamtrips.core.api.uploadery.UploaderyImageCommand;
import com.worldventures.dreamtrips.modules.auth.api.command.LoginCommand;
import com.worldventures.dreamtrips.modules.auth.api.command.UnsubribeFromPushCommand;
import com.worldventures.dreamtrips.modules.auth.api.command.UpdateUserCommand;
import com.worldventures.dreamtrips.modules.bucketlist.service.command.AddBucketItemPhotoCommand;
import com.worldventures.dreamtrips.modules.bucketlist.service.command.BucketListCommand;
import com.worldventures.dreamtrips.modules.bucketlist.service.command.DeleteItemPhotoCommand;
import com.worldventures.dreamtrips.modules.bucketlist.service.command.FindBucketItemByPhotoCommand;
import com.worldventures.dreamtrips.modules.bucketlist.service.command.MergeBucketItemPhotosWithStorageCommand;
import com.worldventures.dreamtrips.modules.common.api.janet.command.AcceptTermsCommand;
import com.worldventures.dreamtrips.modules.common.api.janet.command.CirclesCommand;
import com.worldventures.dreamtrips.modules.common.api.janet.command.ClearMemoryStorageCommand;
import com.worldventures.dreamtrips.modules.common.api.janet.command.SubscribeToPushNotificationsCommand;
import com.worldventures.dreamtrips.modules.common.api.janet.command.TripsFilterDataCommand;
import com.worldventures.dreamtrips.modules.feed.service.command.FeedByHashtagCommand;
import com.worldventures.dreamtrips.modules.feed.service.command.GetAccountFeedCommand;
import com.worldventures.dreamtrips.modules.feed.service.command.GetAccountTimelineCommand;
import com.worldventures.dreamtrips.modules.feed.service.command.GetCommentsCommand;
import com.worldventures.dreamtrips.modules.feed.service.command.GetUserTimelineCommand;
import com.worldventures.dreamtrips.modules.feed.service.command.HashtagSuggestionCommand;
import com.worldventures.dreamtrips.modules.feed.service.command.SuggestedPhotoCommand;
import com.worldventures.dreamtrips.modules.feed.service.command.TranslateTextCachedCommand;
import com.worldventures.dreamtrips.modules.feed.service.command.TranslateUidItemCommand;
import com.worldventures.dreamtrips.modules.membership.command.PodcastCommand;
import com.worldventures.dreamtrips.modules.settings.command.SettingsCommand;
import com.worldventures.dreamtrips.modules.trips.command.GetActivitiesCommand;
import com.worldventures.dreamtrips.modules.trips.command.GetRegionsCommand;
import com.worldventures.dreamtrips.modules.trips.command.GetTripDetailsCommand;
import com.worldventures.dreamtrips.modules.trips.command.GetTripsCommand;

import dagger.Module;

@Module(injects = {UploaderyImageCommand.class, SimpleUploaderyCommand.class, BucketListCommand.class, DeleteItemPhotoCommand.class, FindBucketItemByPhotoCommand.class, AddBucketItemPhotoCommand.class, MergeBucketItemPhotosWithStorageCommand.class, TripsFilterDataCommand.class, CirclesCommand.class, GetCommentsCommand.class, LoginCommand.class, UpdateUserCommand.class, PodcastCommand.class, SuggestedPhotoCommand.class, GetAccountFeedCommand.Refresh.class, GetAccountFeedCommand.LoadNext.class, GetAccountTimelineCommand.Refresh.class, GetAccountTimelineCommand.LoadNext.class, GetUserTimelineCommand.Refresh.class, GetUserTimelineCommand.LoadNext.class, FeedByHashtagCommand.Refresh.class, FeedByHashtagCommand.LoadNext.class, TranslateUidItemCommand.TranslateCommentCommand.class, TranslateUidItemCommand.TranslatePostCommand.class, TranslateTextCachedCommand.class, AcceptTermsCommand.class, UnsubribeFromPushCommand.class, GetTripDetailsCommand.class, GetActivitiesCommand.class, GetRegionsCommand.class, GetTripsCommand.LoadNextTripsCommand.class, GetTripsCommand.ReloadTripsCommand.class, HashtagSuggestionCommand.class, ClearMemoryStorageCommand.class, SubscribeToPushNotificationsCommand.class, SettingsCommand.class},

        complete = false, library = true)
public class SocialJanetCommandModule {}
