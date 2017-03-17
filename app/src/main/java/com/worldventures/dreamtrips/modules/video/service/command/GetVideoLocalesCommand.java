package com.worldventures.dreamtrips.modules.video.service.command;


import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.api.member_videos.GetMemberVideoLocalesHttpAction;
import com.worldventures.dreamtrips.core.api.action.CommandWithError;
import com.worldventures.dreamtrips.core.janet.JanetModule;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.modules.video.model.VideoLocale;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

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
