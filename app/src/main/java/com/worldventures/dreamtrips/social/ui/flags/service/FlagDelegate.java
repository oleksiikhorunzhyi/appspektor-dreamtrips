package com.worldventures.dreamtrips.social.ui.flags.service;

import com.worldventures.dreamtrips.social.ui.flags.model.FlagData;
import com.worldventures.dreamtrips.social.ui.feed.view.cell.Flaggable;
import com.worldventures.dreamtrips.social.ui.flags.command.FlagItemCommand;
import com.worldventures.dreamtrips.social.ui.flags.command.GetFlagsCommand;

import io.techery.janet.Command;
import io.techery.janet.helper.ActionStateSubscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action2;

public class FlagDelegate {

   private FlagsInteractor flagsInteractor;

   public FlagDelegate(FlagsInteractor flagsInteractor) {
      this.flagsInteractor = flagsInteractor;
   }

   public void loadFlags(Flaggable flaggable, Action2<Command, Throwable> errorAction) {
      flagsInteractor.getFlagsPipe()
            .createObservable(new GetFlagsCommand())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new ActionStateSubscriber<GetFlagsCommand>()
                  .onSuccess(command -> flaggable.showFlagDialog(command.getResult()))
                  .onFail(errorAction::call));
   }

   public void flagItem(FlagData data, View view, Action2<Command, Throwable> errorAction) {
      flagsInteractor.getFlagItemPipe()
            .createObservable(new FlagItemCommand(data))
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new ActionStateSubscriber<FlagItemCommand>()
                  .onSuccess(command -> view.flagSentSuccess())
                  .onFail(errorAction::call));
   }

   public interface View {

      void flagSentSuccess();
   }
}