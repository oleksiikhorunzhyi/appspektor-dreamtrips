package com.worldventures.dreamtrips.wallet.service.command;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
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
import io.techery.janet.smartcard.model.Record;
import io.techery.mappery.MapperyContext;
import rx.Observable;

import static com.worldventures.dreamtrips.core.janet.JanetModule.JANET_WALLET;

@CommandAction
public class UpdateCardDetailsDataCommand extends Command<BankCard> implements InjectableAction {

   @Inject @Named(JANET_WALLET) Janet janet;
   @Inject MapperyContext mapperyContext;

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

      BankCard updatedCard = ImmutableBankCard.builder()
            .from(bankCard)
            .addressInfo(manualAddressInfo)
            .build();
      Observable.just(mapperyContext.convert(updatedCard, Record.class))
            .flatMap(record ->
                  Observable.zip(Queryable.from(record.metadata().entrySet())
                        .map(element -> {
                           return actionFor(element.getKey(), element.getValue());
                        }).toList(), args -> updatedCard))
            .subscribe(callback::onSuccess, callback::onFail);
   }

   private Observable<EditRecordMetadataAction> actionFor(String key, String value) {
      return editMetadataPipe.createObservableResult(new EditRecordMetadataAction(Integer.valueOf(bankCard.id()), key, value));
   }

   private void checkCardData() throws FormatException {
      if (!WalletValidateHelper.validateAddressInfo(manualAddressInfo)) throw new FormatException();
   }
}
