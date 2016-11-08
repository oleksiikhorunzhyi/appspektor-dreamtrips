package com.worldventures.dreamtrips.modules.video.service;


import com.worldventures.dreamtrips.core.janet.SessionActionPipeCreator;
import com.worldventures.dreamtrips.modules.video.service.command.GetMemberVideosCommand;
import com.worldventures.dreamtrips.modules.video.service.command.GetVideoLocalesCommand;

import io.techery.janet.ActionPipe;
import rx.schedulers.Schedulers;

public class MemberVideosInteractor {

   private final ActionPipe<GetMemberVideosCommand> getMemberVideosPipe;
   private final ActionPipe<GetVideoLocalesCommand> getVideoLocalesPipe;

   public MemberVideosInteractor(SessionActionPipeCreator sessionActionPipeCreator) {
      getMemberVideosPipe = sessionActionPipeCreator.createPipe(GetMemberVideosCommand.class, Schedulers.io());
      getVideoLocalesPipe = sessionActionPipeCreator.createPipe(GetVideoLocalesCommand.class, Schedulers.io());
   }

   public ActionPipe<GetMemberVideosCommand> getMemberVideosPipe() {
      return getMemberVideosPipe;
   }

   public ActionPipe<GetVideoLocalesCommand> getVideoLocalesPipe() {
      return getVideoLocalesPipe;
   }
}
