package com.worldventures.dreamtrips.wallet.service;

import com.worldventures.dreamtrips.wallet.service.command.AttachCardCommand;
import com.worldventures.dreamtrips.wallet.service.command.CardCountCommand;
import com.worldventures.dreamtrips.wallet.service.command.CardListCommand;
import com.worldventures.dreamtrips.wallet.service.command.CardStacksCommand;
import com.worldventures.dreamtrips.wallet.service.command.ConnectSmartCardCommand;
import com.worldventures.dreamtrips.wallet.service.command.FetchDefaultCardCommand;
import com.worldventures.dreamtrips.wallet.service.command.GetActiveSmartCardCommand;
import com.worldventures.dreamtrips.wallet.service.command.GetDefaultAddressCommand;
import com.worldventures.dreamtrips.wallet.service.command.SaveCardDetailsDataCommand;
import com.worldventures.dreamtrips.wallet.service.command.SetLockStateCommand;
import com.worldventures.dreamtrips.wallet.service.command.SetStealthModeCommand;
import com.worldventures.dreamtrips.wallet.service.command.SmartCardModifier;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import io.techery.janet.ActionPipe;
import io.techery.janet.Janet;
import io.techery.janet.ReadActionPipe;
import io.techery.janet.WriteActionPipe;
import io.techery.janet.helper.ActionStateSubscriber;
import io.techery.janet.smartcard.action.records.DeleteRecordAction;
import rx.Observable;
import rx.schedulers.Schedulers;

import static com.worldventures.dreamtrips.core.janet.JanetModule.JANET_WALLET;
import static com.worldventures.dreamtrips.wallet.service.command.CardListCommand.add;
import static com.worldventures.dreamtrips.wallet.service.command.CardListCommand.remove;
import static java.lang.String.valueOf;

@Singleton
public final class SmartCardInteractor {
   private final ActionPipe<ConnectSmartCardCommand> connectionPipe;
   private final ActionPipe<CardListCommand> cardsListPipe;
   private final ActionPipe<AttachCardCommand> addRecordPipe;
   private final ActionPipe<CardStacksCommand> cardStacksPipe;
   private final ActionPipe<GetActiveSmartCardCommand> getActiveSmartCardPipe;
   private final ActionPipe<CardCountCommand> cardCountCommandPipe;
   private final ActionPipe<GetDefaultAddressCommand> getDefaultAddressCommandPipe;
   private final WriteActionPipe<SaveCardDetailsDataCommand> saveCardDetailsDataCommandPipe;
   private final ActionPipe<SetStealthModeCommand> setStealthModePipe;
   private final ActionPipe<SetLockStateCommand> setLockPipe;
   private final ReadActionPipe<SmartCardModifier> smartCardModifierPipe;
   private final ActionPipe<FetchDefaultCardCommand> fetchDefaultCardCommandActionPipe;
   private final ActionPipe<DeleteRecordAction> deleteCardPipe;

   @Inject
   public SmartCardInteractor(@Named(JANET_WALLET) Janet janet) {
      connectionPipe = janet.createPipe(ConnectSmartCardCommand.class, Schedulers.io());
      cardsListPipe = janet.createPipe(CardListCommand.class, Schedulers.io());
      addRecordPipe = janet.createPipe(AttachCardCommand.class, Schedulers.io());
      cardStacksPipe = janet.createPipe(CardStacksCommand.class, Schedulers.io());
      getActiveSmartCardPipe = janet.createPipe(GetActiveSmartCardCommand.class, Schedulers.io());
      setStealthModePipe = janet.createPipe(SetStealthModeCommand.class, Schedulers.io());

      smartCardModifierPipe = janet.createPipe(SmartCardModifier.class, Schedulers.io());
      setLockPipe = janet.createPipe(SetLockStateCommand.class, Schedulers.io());

      cardCountCommandPipe = janet.createPipe(CardCountCommand.class, Schedulers.io());
      getDefaultAddressCommandPipe = janet.createPipe(GetDefaultAddressCommand.class, Schedulers.io());
      saveCardDetailsDataCommandPipe = janet.createPipe(SaveCardDetailsDataCommand.class, Schedulers.io());
      fetchDefaultCardCommandActionPipe = janet.createPipe(FetchDefaultCardCommand.class, Schedulers.io());
      deleteCardPipe = janet.createPipe(DeleteRecordAction.class, Schedulers.io());

      connect();
   }

   public ActionPipe<ConnectSmartCardCommand> connectActionPipe() {
      return connectionPipe;
   }

   public WriteActionPipe<AttachCardCommand> addRecordPipe() {
      return addRecordPipe;
   }

   public ActionPipe<CardStacksCommand> cardStacksPipe() {
      return cardStacksPipe;
   }

   public ActionPipe<FetchDefaultCardCommand> fetchDefaultCardCommandActionPipe() {
      return fetchDefaultCardCommandActionPipe;
   }

   public ActionPipe<DeleteRecordAction> deleteCardPipe() {
      return deleteCardPipe;
   }

   public ActionPipe<GetActiveSmartCardCommand> getActiveSmartCardPipe() {
      return getActiveSmartCardPipe;
   }

   public ActionPipe<CardCountCommand> cardCountCommandPipe() {
      return cardCountCommandPipe;
   }

   public ActionPipe<GetDefaultAddressCommand> getDefaultAddressCommandPipe() {
      return getDefaultAddressCommandPipe;
   }

   public WriteActionPipe<SaveCardDetailsDataCommand> saveCardDetailsDataCommandPipe() {
      return saveCardDetailsDataCommandPipe;
   }

   public ActionPipe<SetStealthModeCommand> setStealthModePipe() {
      return setStealthModePipe;
   }

   public ReadActionPipe<SmartCardModifier> smartCardModifierPipe() {
      return smartCardModifierPipe;
   }

   public ActionPipe<SetLockStateCommand> lockPipe() {
      return setLockPipe;
   }

   public WriteActionPipe<SaveCardDetailsDataCommand> saveCardDetailsDataPipe() {
      return saveCardDetailsDataCommandPipe;
   }

   private void connect() {
      Observable.merge(deleteCardPipe.observeSuccess()
            .flatMap(deleteCommand -> cardsListPipe.createObservable(remove(valueOf(deleteCommand.recordId)))), addRecordPipe
            .observeSuccess()
            .flatMap(attachCardCommand -> cardsListPipe.createObservable(add(attachCardCommand.bankCard()))))
            .subscribe(new ActionStateSubscriber<CardListCommand>().onSuccess(cardListCommand -> cardStacksPipe.send(CardStacksCommand
                  .get(false))).onFail((cardListCommand, throwable) -> {
               throw new IllegalStateException("Cannot perform operation onto card list cache", throwable);
            }));
   }
}