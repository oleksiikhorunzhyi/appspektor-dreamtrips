package com.worldventures.dreamtrips.modules.background_uploading.service.command.video;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.modules.background_uploading.model.CompoundOperationState;
import com.worldventures.dreamtrips.modules.background_uploading.service.PingAssetStatusInteractor;
import com.worldventures.dreamtrips.modules.background_uploading.storage.CompoundOperationRepository;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.techery.janet.Command;
import io.techery.janet.command.annotations.CommandAction;
import rx.Observable;

@CommandAction
public class PerformUpdateVideoStatusCommand extends Command<Void> implements InjectableAction {

   private static final int DELAY_START_UPDATE_COMMAND = 1;

   @Inject PingAssetStatusInteractor pingAssetStatusInteractor;
   @Inject CompoundOperationRepository compoundOperationRepository;

   private CommandCallback callback;

   @Override
   protected void run(CommandCallback callback) throws Throwable {
      this.callback = callback;
      tryUpdateVideoStatus();
   }

   private void tryUpdateVideoStatus() {
      if (isProcessingVideoExists()) {
         launchUpdateProcess();
      } else {
         callback.onSuccess(null);
      }
   }

   private void launchUpdateProcess() {
      Observable.timer(DELAY_START_UPDATE_COMMAND, TimeUnit.MINUTES)
            .flatMap(v -> pingAssetStatusInteractor.updateVideoProcessStatusPipe()
                  .createObservableResult(new UpdateVideoProcessStatusCommand()))
            .subscribe(v -> tryUpdateVideoStatus(), callback::onFail);
   }

   private boolean isProcessingVideoExists() {
      return Queryable.from(compoundOperationRepository.readCompoundOperations())
            .firstOrDefault(element -> element.state() == CompoundOperationState.PROCESSING) != null;
   }

}