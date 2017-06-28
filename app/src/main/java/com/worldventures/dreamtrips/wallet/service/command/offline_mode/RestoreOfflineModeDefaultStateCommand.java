package com.worldventures.dreamtrips.wallet.service.command.offline_mode;

import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;

import javax.inject.Inject;

import io.techery.janet.Command;
import io.techery.janet.command.annotations.CommandAction;
import rx.Observable;

/**
 * Checks whether offline mode is enabled.
 * Then sets offline mode to default state (disabled) if needed.
 */
@CommandAction
public class RestoreOfflineModeDefaultStateCommand extends Command<Void> implements InjectableAction {

   @Inject SmartCardInteractor smartCardInteractor;

   @Override
   protected void run(CommandCallback<Void> callback) throws Throwable {
      smartCardInteractor.offlineModeStatusPipe()
            .createObservableResult(OfflineModeStatusCommand.fetch())
            .map(Command::getResult)
            .flatMap(offlineModeEnabled -> offlineModeEnabled ? smartCardInteractor.switchOfflineModePipe()
                  .createObservableResult(new SwitchOfflineModeCommand()) : Observable.just(null))
            .map(switchOfflineModeCommand -> (Void) null)
            .subscribe(callback::onSuccess, callback::onFail);
   }

}
