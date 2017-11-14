package com.worldventures.wallet.service.command.http;

import com.worldventures.janet.injection.InjectableAction;
import com.worldventures.wallet.domain.entity.record.FinancialService;
import com.worldventures.wallet.domain.entity.record.ImmutableRecord;
import com.worldventures.wallet.domain.entity.record.Record;
import com.worldventures.wallet.util.WalletRecordUtil;

import javax.inject.Inject;

import io.techery.janet.Command;
import io.techery.janet.command.annotations.CommandAction;
import io.techery.mappery.MapperyContext;

@CommandAction
public class CreateRecordCommand extends Command<Record> implements InjectableAction {

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
      if (WalletRecordUtil.isAmexBank(record.number())) {
         return ImmutableRecord.builder().from(record).financialService(FinancialService.AMEX).build();
      }
      return record;
   }
}