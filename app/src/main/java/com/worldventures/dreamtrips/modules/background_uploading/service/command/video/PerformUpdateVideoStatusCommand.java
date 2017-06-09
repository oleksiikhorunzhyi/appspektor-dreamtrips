package com.worldventures.dreamtrips.modules.background_uploading.service.command.video;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.modules.background_uploading.model.CompoundOperationState;
import com.worldventures.dreamtrips.modules.background_uploading.service.CompoundOperationsInteractor;
import com.worldventures.dreamtrips.modules.background_uploading.service.PingAssetStatusInteractor;
import com.worldventures.dreamtrips.modules.background_uploading.service.command.QueryCompoundOperationsCommand;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.techery.janet.Command;
import io.techery.janet.command.annotations.CommandAction;
import rx.Observable;

@CommandAction
public class PerformUpdateVideoStatusCommand extends Command<Void> implements InjectableAction {

   private static final int DELAY_START_UPDATE_COMMAND = 30;

   @Inject PingAssetStatusInteractor pingAssetStatusInteractor;
   @Inject CompoundOperationsInteractor compoundOperationsInteractor;

   private CommandCallback callback;

   @Override
   protected void run(CommandCallback callback) throws Throwable {
      this.callback = callback;
      tryUpdateVideoStatus();
   }

   private void tryUpdateVideoStatus() {
      compoundOperationsInteractor.compoundOperationsPipe()
            .createObservableResult(new QueryCompoundOperationsCommand())
            .map(Command::getResult)
            .subscribe(items -> {
               boolean itemsExists = Queryable.from(items)
                     .firstOrDefault(element -> element.state() == CompoundOperationState.PROCESSING) != null;
               if (itemsExists) {
                  launchUpdateProcess();
               } else {
                  callback.onSuccess(null);
               }
            });
   }

   private void launchUpdateProcess() {
      Observable.timer(DELAY_START_UPDATE_COMMAND, TimeUnit.SECONDS)
            .flatMap(v -> pingAssetStatusInteractor.updateVideoProcessStatusPipe()
                  .createObservableResult(new UpdateVideoProcessStatusCommand()))
            .subscribe(v -> tryUpdateVideoStatus(), callback::onFail);
   }
}