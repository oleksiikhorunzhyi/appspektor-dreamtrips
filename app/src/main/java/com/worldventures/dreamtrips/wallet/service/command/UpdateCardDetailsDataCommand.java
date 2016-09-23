package com.worldventures.dreamtrips.wallet.service.command;

import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.wallet.domain.converter.BankCardConverter;
import com.worldventures.dreamtrips.wallet.domain.entity.AddressInfo;
import com.worldventures.dreamtrips.wallet.domain.entity.card.BankCard;
import com.worldventures.dreamtrips.wallet.domain.entity.card.ImmutableBankCard;
import com.worldventures.dreamtrips.wallet.util.FormatException;
import com.worldventures.dreamtrips.wallet.util.WalletValidateHelper;

import javax.inject.Inject;
import javax.inject.Named;

import io.techery.janet.ActionPipe;
import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import io.techery.janet.smartcard.action.records.EditRecordMetadataAction;
import rx.Observable;

import static com.worldventures.dreamtrips.core.janet.JanetModule.JANET_WALLET;

@CommandAction
public class UpdateCardDetailsDataCommand extends Command<BankCard> implements InjectableAction {

   @Inject @Named(JANET_WALLET) Janet janet;

   private final BankCard bankCard;
   private final AddressInfo manualAddressInfo;

   private ActionPipe<EditRecordMetadataAction> editMetadataPipe;

   public UpdateCardDetailsDataCommand(BankCard bankCard, AddressInfo manualAddressInfo) {
      this.bankCard = bankCard;
      this.manualAddressInfo = manualAddressInfo;
   }

   @Override
   protected void run(CommandCallback<BankCard> callback) throws Throwable {
      checkCardData();
      editMetadataPipe = janet.createPipe(EditRecordMetadataAction.class);

      Observable.zip(
            actionFor(BankCardConverter.ADDRESS1_FIELD, manualAddressInfo.address1()),
            actionFor(BankCardConverter.ADDRESS2_FIELD, manualAddressInfo.address2()),
            actionFor(BankCardConverter.CITY_FIELD, manualAddressInfo.city()),
            actionFor(BankCardConverter.STATE_FIELD, manualAddressInfo.state()),
            actionFor(BankCardConverter.ZIP_FIELD, manualAddressInfo.zip()),
            (addressAction, address2Action, cityAction, stateAction, zipAction) ->
                  ImmutableBankCard.builder().from(bankCard).addressInfo(manualAddressInfo).build()
      ).subscribe(callback::onSuccess, callback::onFail);
   }

   public BankCard bankCard() {
      return bankCard;
   }

   private Observable<EditRecordMetadataAction> actionFor(String key, String value) {
      return editMetadataPipe.createObservableResult(new EditRecordMetadataAction(Integer.valueOf(bankCard.id()), key, value));
   }

   private void checkCardData() throws FormatException {
      if (!WalletValidateHelper.validateAddressInfo(manualAddressInfo)) throw new FormatException();
   }
}
