package com.worldventures.dreamtrips.modules.common.presenter.delegate;

import com.messenger.delegate.FlagsInteractor;
import com.worldventures.dreamtrips.modules.common.model.FlagData;
import com.worldventures.dreamtrips.modules.common.presenter.RequestingPresenter;
import com.worldventures.dreamtrips.modules.feed.api.FlagItemCommand;
import com.worldventures.dreamtrips.modules.feed.view.cell.Flaggable;
import com.worldventures.dreamtrips.modules.flags.command.GetFlagsCommand;

import io.techery.janet.Command;
import io.techery.janet.helper.ActionStateSubscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Action2;

public class UidItemDelegate {

   private RequestingPresenter requestingPresenter;

   private FlagsInteractor flagsInteractor;

   public UidItemDelegate(RequestingPresenter requestingPresenter, FlagsInteractor flagsInteractor) {
      this.requestingPresenter = requestingPresenter;
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

   public void flagItem(FlagData data, View view) {
      requestingPresenter.doRequest(new FlagItemCommand(data), aVoid -> view.flagSentSuccess());
   }

   public interface View {

      void flagSentSuccess();
   }
}