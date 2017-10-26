package com.worldventures.core.modules.video.service.command;


import com.worldventures.core.R;
import com.worldventures.janet.injection.InjectableAction;
import com.worldventures.dreamtrips.api.member_videos.GetMemberVideoLocalesHttpAction;
import com.worldventures.core.janet.CommandWithError;
import com.worldventures.core.modules.video.model.VideoLocale;

import java.util.List;

import javax.inject.Inject;

import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import io.techery.mappery.MapperyContext;

@CommandAction
public class GetVideoLocalesCommand extends CommandWithError<List<VideoLocale>> implements InjectableAction {

   @Inject Janet janet;
   @Inject MapperyContext mapperyContext;

   @Override
   protected void run(CommandCallback<List<VideoLocale>> callback) throws Throwable {
      janet.createPipe(GetMemberVideoLocalesHttpAction.class)
            .createObservableResult(new GetMemberVideoLocalesHttpAction())
            .map(GetMemberVideoLocalesHttpAction::response)
            .map(videoLocales -> mapperyContext.convert(videoLocales, VideoLocale.class))
            .subscribe(callback::onSuccess, callback::onFail);
   }

   @Override
   public int getFallbackErrorMessage() {
      return R.string.error_fail_to_locales;
   }
}
