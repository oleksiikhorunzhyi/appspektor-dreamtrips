package com.worldventures.dreamtrips.modules.common;

import com.worldventures.dreamtrips.core.api.uploadery.SimpleUploaderyCommand;
import com.worldventures.dreamtrips.core.api.uploadery.UploaderyImageCommand;
import com.worldventures.dreamtrips.modules.bucketlist.service.command.AddBucketItemPhotoCommand;
import com.worldventures.dreamtrips.modules.bucketlist.service.command.BucketListCommand;
import com.worldventures.dreamtrips.modules.bucketlist.service.command.DeleteItemPhotoCommand;
import com.worldventures.dreamtrips.modules.bucketlist.service.command.FindBucketItemByPhotoCommand;
import com.worldventures.dreamtrips.modules.bucketlist.service.command.MergeBucketItemPhotosWithStorageCommand;
import com.worldventures.dreamtrips.modules.common.api.janet.command.AppSettingsCommand;
import com.worldventures.dreamtrips.modules.common.api.janet.command.CirclesCommand;
import com.worldventures.dreamtrips.modules.common.api.janet.command.GlobalConfigCommand;
import com.worldventures.dreamtrips.modules.common.api.janet.command.LocalesCommand;
import com.worldventures.dreamtrips.modules.common.api.janet.command.StaticPageConfigCommand;
import com.worldventures.dreamtrips.modules.common.api.janet.command.TripsFilterDataCommand;
import com.worldventures.dreamtrips.modules.common.api.janet.command.UpdateAuthInfoCommand;
import com.worldventures.dreamtrips.modules.feed.service.SuggestedPhotoCommand;
import com.worldventures.dreamtrips.modules.feed.service.command.FeedByHashtagCommand;
import com.worldventures.dreamtrips.modules.feed.service.command.GetAccountFeedQueryCommand;
import com.worldventures.dreamtrips.modules.feed.service.command.HashtagSuggestionCommand;
import com.worldventures.dreamtrips.modules.feed.service.command.TranslateUidItemCommand;
import com.worldventures.dreamtrips.modules.membership.command.PodcastCommand;

import dagger.Module;

@Module(injects = {UploaderyImageCommand.class,
        SimpleUploaderyCommand.class,
        BucketListCommand.class,
        DeleteItemPhotoCommand.class,
        FindBucketItemByPhotoCommand.class,
        AddBucketItemPhotoCommand.class,
        MergeBucketItemPhotosWithStorageCommand.class,
        LocalesCommand.class,
        TripsFilterDataCommand.class,
        StaticPageConfigCommand.class,
        GlobalConfigCommand.class,
        CirclesCommand.class,
        AppSettingsCommand.class,
        UpdateAuthInfoCommand.class,
        PodcastCommand.class,
        SuggestedPhotoCommand.class,
        GetAccountFeedQueryCommand.Refresh.class,
        GetAccountFeedQueryCommand.LoadNext.class,
        FeedByHashtagCommand.Refresh.class,
        FeedByHashtagCommand.LoadNext.class,
        TranslateUidItemCommand.TranslateCommentCommand.class,
        TranslateUidItemCommand.TranslatePostCommand.class,
        HashtagSuggestionCommand.class},

        complete = false, library = true)
public class SocialJanetCommandModule {
}
