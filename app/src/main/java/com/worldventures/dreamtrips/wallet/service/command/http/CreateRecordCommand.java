package com.worldventures.dreamtrips.wallet.service.command.http;

import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.wallet.domain.entity.record.FinancialService;
import com.worldventures.dreamtrips.wallet.domain.entity.record.ImmutableRecord;
import com.worldventures.dreamtrips.wallet.domain.entity.record.Record;
import com.worldventures.dreamtrips.wallet.util.WalletRecordUtil;

import javax.inject.Inject;

import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import io.techery.mappery.MapperyContext;

@CommandAction
public class CreateRecordCommand extends Command<Record> implements InjectableAction {

   @Inject Janet janet;
   @Inject MapperyContext mappery;

   private final io.techery.janet.smartcard.model.Record swipedCard;

   public CreateRecordCommand(io.techery.janet.smartcard.model.Record swipedCard) {
      this.swipedCard = swipedCard;
   }

   @Override
   protected void run(CommandCallback<Record> callback) throws Throwable {
      Record record = mappery.convert(swipedCard, Record.class);
      callback.onSuccess(withExtraInfo(record));
   }

   private Record withExtraInfo(Record record) {
      final ImmutableRecord.Builder builder = ImmutableRecord.builder()
            .from(record)
            .numberLastFourDigits(WalletRecordUtil.obtainLastCardDigits(record.number()));

      if (WalletRecordUtil.isAmexBank(record.number())) {
         builder.financialService(FinancialService.AMEX);
      }
      return builder.build();
   }

}