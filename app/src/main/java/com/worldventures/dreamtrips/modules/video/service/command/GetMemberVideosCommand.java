package com.worldventures.dreamtrips.modules.video.service.command;


import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.api.member_videos.GetMemberVideosHttpAction;
import com.worldventures.dreamtrips.api.member_videos.model.ImmutableVideoLanguage;
import com.worldventures.dreamtrips.api.member_videos.model.VideoLanguage;
import com.worldventures.dreamtrips.api.member_videos.model.VideoType;
import com.worldventures.dreamtrips.core.api.action.CommandWithError;
import com.worldventures.dreamtrips.core.janet.JanetModule;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.modules.video.model.VideoCategory;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import io.techery.mappery.MapperyContext;

@CommandAction
public class GetMemberVideosCommand extends CommandWithError<List<VideoCategory>> implements InjectableAction {

   @Inject @Named(JanetModule.JANET_API_LIB) Janet janet;
   @Inject MapperyContext mapperyContext;

   private VideoType videoType;
   private VideoLanguage videoLanguage;

   public GetMemberVideosCommand(VideoType videoType) {
      this.videoType = videoType;
   }

   private GetMemberVideosCommand(VideoType videoType, VideoLanguage videoLanguage) {
      this.videoType = videoType;
      this.videoLanguage = videoLanguage;
   }

   @Override
   public int getFallbackErrorMessage() {
      return R.string.error_fail_to_load_videos;
   }

   @Override
   protected void run(CommandCallback<List<VideoCategory>> callback) throws Throwable {
      janet.createPipe(GetMemberVideosHttpAction.class)
            .createObservableResult(videoLanguage == null ?
                  new GetMemberVideosHttpAction(videoType) :
                  new GetMemberVideosHttpAction(videoType, videoLanguage))
            .map(GetMemberVideosHttpAction::response)
            .map(videoCategories -> mapperyContext.convert(videoCategories, VideoCategory.class))
            .subscribe(callback::onSuccess, callback::onFail);
   }

   public static GetMemberVideosCommand forThreeSixtyVideos() {
      return new GetMemberVideosCommand(VideoType.DTAPP360);
   }

   public static GetMemberVideosCommand forRepVideos(com.worldventures.dreamtrips.modules.video.model.VideoLanguage videoLanguage) {
      if (videoLanguage == null) return new GetMemberVideosCommand(VideoType.DTAPPREP);
      return new GetMemberVideosCommand(VideoType.DTAPPREP, ImmutableVideoLanguage.builder()
            .title(videoLanguage.getTitle())
            .localeName(videoLanguage.getLocaleName())
            .build());
   }

   public static GetMemberVideosCommand forMemberVideos() {
      return new GetMemberVideosCommand(VideoType.DTAPP);
   }

   public static GetMemberVideosCommand forHelpVideos(com.worldventures.dreamtrips.modules.video.model.VideoLanguage videoLanguage) {
      if (videoLanguage == null) return new GetMemberVideosCommand(VideoType.DTAPPHELP);
      return new GetMemberVideosCommand(VideoType.DTAPP, ImmutableVideoLanguage.builder()
            .title(videoLanguage.getTitle())
            .localeName(videoLanguage.getLocaleName())
            .build());
   }
}
