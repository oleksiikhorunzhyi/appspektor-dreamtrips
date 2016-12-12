package com.worldventures.dreamtrips.wallet.service.command;


import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.wallet.domain.entity.AddressInfo;
import com.worldventures.dreamtrips.wallet.domain.entity.card.BankCard;
import com.worldventures.dreamtrips.wallet.domain.entity.card.ImmutableBankCard;
import com.worldventures.dreamtrips.wallet.util.FormatException;

import javax.inject.Inject;
import javax.inject.Named;

import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import io.techery.janet.smartcard.action.records.EditRecordAction;
import io.techery.janet.smartcard.model.Record;
import io.techery.mappery.MapperyContext;
import rx.Observable;

import static com.worldventures.dreamtrips.core.janet.JanetModule.JANET_WALLET;
import static com.worldventures.dreamtrips.wallet.util.WalletValidateHelper.validateAddressInfoOrThrow;
import static com.worldventures.dreamtrips.wallet.util.WalletValidateHelper.validateCardNameOrThrow;

@CommandAction
public class UpdateBankCardCommand extends Command<BankCard> implements InjectableAction {

   @Inject @Named(JANET_WALLET) Janet janet;
   @Inject MapperyContext mapperyContext;

   private BankCard bankCard;

   private UpdateBankCardCommand(BankCard bankCard) {
      this.bankCard = bankCard;
   }

   public static UpdateBankCardCommand updateNickName(BankCard bankCard, String nickName) {
      return new UpdateBankCardCommand(ImmutableBankCard.builder().from(bankCard).nickName(nickName).build());
   }

   public static UpdateBankCardCommand updateAddress(BankCard bankCard, AddressInfo addressInfo) {
      return new UpdateBankCardCommand(
            ImmutableBankCard.builder()
                  .from(bankCard)
                  .addressInfo(addressInfo)
                  .build()
      );
   }

   @Override
   protected void run(CommandCallback<BankCard> callback) throws Throwable {
      checkCardData();

      Observable.just(bankCard)
            .map(it -> mapperyContext.convert(it, Record.class))
            .flatMap(record -> janet.createPipe(EditRecordAction.class)
                  .createObservableResult(new EditRecordAction(record)))
            .map(recordAction -> mapperyContext.convert(recordAction.record, BankCard.class))
            .subscribe(callback::onSuccess, callback::onFail);
   }

   private void checkCardData() throws FormatException {
      validateCardNameOrThrow(bankCard.nickName());
      validateAddressInfoOrThrow(bankCard.addressInfo());
   }
}
