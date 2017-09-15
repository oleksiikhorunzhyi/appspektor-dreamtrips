package com.worldventures.dreamtrips.social.ui.background_uploading.service.command;

import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.social.ui.background_uploading.service.PingAssetStatusInteractor;
import com.worldventures.dreamtrips.social.ui.background_uploading.service.command.video.PerformUpdateVideoStatusCommand;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.techery.janet.ActionState;
import io.techery.janet.Command;
import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class LaunchUpdatingVideoProcessingCommand extends Command<Void> implements InjectableAction {

   private static final long TIMEOUT_FOR_WAIT_ANY_COMMAND = 100L;

   @Inject PingAssetStatusInteractor pingAssetStatusInteractor;

   public LaunchUpdatingVideoProcessingCommand() {
   }

   @Override
   protected void run(CommandCallback<Void> callback) throws Throwable {
      pingAssetStatusInteractor.performUpdateVideoStatusPipe()
            .observeWithReplay()
            .timeout(TIMEOUT_FOR_WAIT_ANY_COMMAND, TimeUnit.MILLISECONDS)
            .first()
            .subscribe(actionState -> {
               if (actionState.status == ActionState.Status.FAIL || actionState.status == ActionState.Status.SUCCESS) {
                  startPerformUpdateCommand();
               }
               callback.onSuccess(null);
            }, throwable -> { // it means pipe does not have any command previously run
               startPerformUpdateCommand();
               callback.onSuccess(null);
            });
   }

   private void startPerformUpdateCommand() {
      pingAssetStatusInteractor.performUpdateVideoStatusPipe().send(new PerformUpdateVideoStatusCommand());
   }

}
