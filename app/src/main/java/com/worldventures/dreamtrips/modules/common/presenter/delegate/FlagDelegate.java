package com.worldventures.dreamtrips.modules.common.presenter.delegate;

import com.worldventures.dreamtrips.modules.common.model.FlagData;
import com.worldventures.dreamtrips.modules.feed.view.cell.Flaggable;
import com.worldventures.dreamtrips.modules.flags.command.FlagItemCommand;
import com.worldventures.dreamtrips.modules.flags.command.GetFlagsCommand;
import com.worldventures.dreamtrips.modules.flags.service.FlagsInteractor;

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