package com.worldventures.dreamtrips.wallet.service.command;

import android.support.annotation.NonNull;

import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsInteractor;
import com.worldventures.dreamtrips.wallet.analytics.oncard.GetOnCardAnalyticsCommand;
import com.worldventures.dreamtrips.wallet.analytics.oncard.SendOnCardAnalyticsCommand;
import com.worldventures.dreamtrips.wallet.service.RecordInteractor;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.command.device.SmartCardFirmwareCommand;
import com.worldventures.dreamtrips.wallet.service.command.record.SyncRecordsCommand;
import com.worldventures.dreamtrips.wallet.util.SCFirmwareUtils;

import java.util.Collections;

import javax.inject.Inject;

import io.techery.janet.Command;
import io.techery.janet.command.annotations.CommandAction;
import rx.Observable;
import rx.functions.Func1;

@CommandAction
public class SyncSmartCardCommand extends Command<Void> implements InjectableAction {

   @Inject SmartCardInteractor smartCardInteractor;
   @Inject RecordInteractor recordInteractor;
   @Inject AnalyticsInteractor analyticsInteractor;

   @Override
   protected void run(CommandCallback<Void> callback) throws Throwable {
      recordInteractor.recordsSyncPipe()
            .createObservableResult(new SyncRecordsCommand())
            .flatMap(processSmartCardAnalytics())
            .subscribe(callback::onSuccess, callback::onFail);
   }

   @NonNull
   private Func1<SyncRecordsCommand, Observable<Void>> processSmartCardAnalytics() {
      return syncRecordsCommand -> smartCardInteractor.smartCardFirmwarePipe()
            .createObservableResult(SmartCardFirmwareCommand.fetch())
            .map(SmartCardFirmwareCommand::getResult)
            .flatMap(firmware -> {
               if (SCFirmwareUtils.supportOnCardAnalytics(firmware)) {
                  return smartCardInteractor
                        .getOnCardAnalyticsPipe()
                        .createObservableResult(new GetOnCardAnalyticsCommand())
                        .map(Command::getResult)
                        .onErrorReturn(throwable -> Collections.emptyList())
                        .doOnNext(analyticsLogs -> analyticsInteractor.onCardAnalyticsPipe()
                              .send(new SendOnCardAnalyticsCommand(analyticsLogs)))
                        .map(analyticsLogs -> (Void) null);
               } else {
                  return Observable.just(null);
               }
            });
   }

}
