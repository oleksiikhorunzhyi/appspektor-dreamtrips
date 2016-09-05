package com.worldventures.dreamtrips.modules.common.presenter.delegate;

import com.worldventures.dreamtrips.modules.flags.command.GetFlagsCommand;
import com.messenger.delegate.FlagsInteractor;
import com.worldventures.dreamtrips.modules.common.model.FlagData;
import com.worldventures.dreamtrips.modules.common.presenter.RequestingPresenter;
import com.worldventures.dreamtrips.modules.feed.api.FlagItemCommand;
import com.worldventures.dreamtrips.modules.feed.view.cell.Flaggable;

import io.techery.janet.Command;
import rx.android.schedulers.AndroidSchedulers;
import timber.log.Timber;

public class UidItemDelegate {

   private RequestingPresenter requestingPresenter;

   private FlagsInteractor flagsInteractor;

   public UidItemDelegate(RequestingPresenter requestingPresenter, FlagsInteractor flagsInteractor) {
      this.requestingPresenter = requestingPresenter;
      this.flagsInteractor = flagsInteractor;
   }

   public void loadFlags(Flaggable flaggable) {
      flagsInteractor.getFlagsPipe()
            .createObservableResult(new GetFlagsCommand())
            .observeOn(AndroidSchedulers.mainThread())
            .map(Command::getResult)
            .subscribe(flaggable::showFlagDialog, e -> Timber.e(e, "Could not load flags"));
   }

   public void flagItem(FlagData data, View view) {
      requestingPresenter.doRequest(new FlagItemCommand(data), aVoid -> {
         if (view != null) {
            view.flagSentSuccess();
         }
      });
   }

   public interface View {

      void flagSentSuccess();
   }
}