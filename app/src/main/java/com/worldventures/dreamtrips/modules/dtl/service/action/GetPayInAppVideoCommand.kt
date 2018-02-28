package com.worldventures.dreamtrips.modules.dtl.service.action

import com.worldventures.core.modules.video.model.Video
import com.worldventures.core.modules.video.model.VideoCategory
import com.worldventures.dreamtrips.api.member_videos.GetMemberVideosHttpAction
import com.worldventures.dreamtrips.api.member_videos.model.ImmutableVideoLanguage
import com.worldventures.dreamtrips.api.member_videos.model.VideoType
import com.worldventures.janet.injection.InjectableAction
import io.techery.janet.Command
import io.techery.janet.Janet
import io.techery.janet.command.annotations.CommandAction
import io.techery.mappery.MapperyContext
import java.util.Locale
import javax.inject.Inject

@CommandAction
class GetPayInAppVideoCommand : Command<Video>(), InjectableAction {

   @Inject lateinit var janet: Janet
   @Inject lateinit var mapperyContext: MapperyContext

   override fun run(callback: CommandCallback<Video>) {
      callback.onProgress(0)

      janet.createPipe(GetMemberVideosHttpAction::class.java)
            .createObservableResult(GetMemberVideosHttpAction(VideoType.DT_APP_HELP_GENERAL,
                  ImmutableVideoLanguage.builder()
                        .title("English")
                        .localeName("en-us")
                        .build()))
            .map({ it.response() })
            .map { videoCategories -> mapperyContext.convert(videoCategories, VideoCategory::class.java) }
            .map { it.first { videoCategory -> videoCategory.category.toLowerCase(Locale.US) == "pay in app" } }
            .map { it.videos.first { video -> video.category.toLowerCase(Locale.US) == "pay in app" } }
            .subscribe(callback::onSuccess, callback::onFail)
   }
}
