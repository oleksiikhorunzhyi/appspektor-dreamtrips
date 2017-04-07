package com.worldventures.dreamtrips.wallet.service.command;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.wallet.service.RecordInteractor;
import com.worldventures.dreamtrips.wallet.service.command.record.DefaultRecordIdCommand;

import javax.inject.Inject;
import javax.inject.Named;

import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import io.techery.janet.smartcard.action.records.SetRecordAsDefaultAction;
import io.techery.janet.smartcard.action.records.UnsetDefaultRecordAction;
import rx.Observable;
import rx.schedulers.Schedulers;

import static com.worldventures.dreamtrips.core.janet.JanetModule.JANET_WALLET;

@CommandAction
public class SetDefaultCardOnDeviceCommand extends Command<Void> implements InjectableAction {

   @Inject @Named(JANET_WALLET) Janet janet;
   @Inject RecordInteractor recordInteractor;

   private final String cardId;
   private final boolean isSetDefault;

   private SetDefaultCardOnDeviceCommand(String cardId, boolean isSetDefault) {
      this.cardId = cardId;
      this.isSetDefault = isSetDefault;
   }

   public static SetDefaultCardOnDeviceCommand setAsDefault(String cardId) {
      return new SetDefaultCardOnDeviceCommand(cardId, true);
   }

   public static SetDefaultCardOnDeviceCommand unsetDefaultCard() {
      return new SetDefaultCardOnDeviceCommand(null, false);
   }

   @Override
   protected void run(CommandCallback<Void> callback) throws Throwable {

      Observable<String> defaultRecordIdObservable;

      if (isSetDefault) {
         defaultRecordIdObservable = janet.createPipe(SetRecordAsDefaultAction.class, Schedulers.io())
               .createObservableResult(new SetRecordAsDefaultAction(Integer.parseInt(cardId)))
               .map(action -> String.valueOf(action.recordId));
      } else {
         defaultRecordIdObservable = janet.createPipe(UnsetDefaultRecordAction.class)
               .createObservableResult(new UnsetDefaultRecordAction())
               .map(action -> null);
      }

      defaultRecordIdObservable
            .flatMap(this::saveDefaultRecordIdLocally)
            .subscribe(callback::onSuccess, callback::onFail);
   }

   @NonNull
   private Observable<Void> saveDefaultRecordIdLocally(@Nullable String recordId) {
      return recordInteractor.defaultRecordIdPipe().createObservableResult(DefaultRecordIdCommand.set(recordId))
            .map(command -> (Void) null);
   }
}
