package com.worldventures.dreamtrips.wallet.service.command;

import com.worldventures.dreamtrips.core.janet.cache.CacheOptions;
import com.worldventures.dreamtrips.core.janet.cache.CachedAction;
import com.worldventures.dreamtrips.core.janet.cache.ImmutableCacheOptions;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.wallet.domain.converter.BankCardConverter;
import com.worldventures.dreamtrips.wallet.domain.entity.card.BankCard;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import io.techery.janet.ActionHolder;
import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import io.techery.janet.helper.ActionStateToActionTransformer;
import io.techery.janet.smartcard.action.records.AddRecordAction;
import io.techery.janet.smartcard.model.Record;
import rx.Observable;
import rx.schedulers.Schedulers;

import static com.worldventures.dreamtrips.core.janet.JanetModule.JANET_WALLET;

@CommandAction
public class AttachCardCommand extends Command<Record> implements InjectableAction, CachedAction<List<BankCard>> {

   @Inject @Named(JANET_WALLET) Janet janet;

   private final BankCardConverter converter = new BankCardConverter();

   private BankCard card;
   private final boolean setAsDefaultCard;

   public AttachCardCommand(BankCard card, boolean setAsDefaultCard) {
      this.card = card;
      this.setAsDefaultCard = setAsDefaultCard;
   }

   @Override
   protected void run(CommandCallback<Record> callback) throws Throwable {
      Record record = converter.to(card);
      janet.createPipe(AddRecordAction.class)
            .createObservable(new AddRecordAction(record))
            .compose(new ActionStateToActionTransformer<>())
            .map(it -> it.record)
            .flatMap(addedRecord -> saveDefaultCard(addedRecord))
            .subscribe(addedRecord -> {
               card = converter.from(addedRecord);
               callback.onSuccess(addedRecord);
            }, callback::onFail);
   }

   private Observable<Record> saveDefaultCard(Record record) {
      return setAsDefaultCard ?
            janet.createPipe(SetDefaultCardOnDeviceCommand.class, Schedulers.io())
                  .createObservableResult(new SetDefaultCardOnDeviceCommand(String.valueOf(record.id())))
                  .map(setDefaultCardOnDeviceAction -> record) :
            Observable.just(record);
   }

   public BankCard bankCard() {
      return card;
   }

   @Override
   public List<BankCard> getCacheData() {
      return Collections.singletonList(card);
   }

   @Override
   public void onRestore(ActionHolder holder, List<BankCard> cache) {
   }

   @Override
   public CacheOptions getCacheOptions() {
      return ImmutableCacheOptions.builder()
            .restoreFromCache(false)
            .saveToCache(true)
            .build();
   }
}