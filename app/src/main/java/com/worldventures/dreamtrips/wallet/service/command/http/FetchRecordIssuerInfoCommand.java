package com.worldventures.dreamtrips.wallet.service.command.http;

import com.worldventures.dreamtrips.api.smart_card.bank_info.GetBankInfoHttpAction;
import com.worldventures.dreamtrips.core.janet.JanetModule;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.wallet.domain.converter.BankInfoConverter;
import com.worldventures.dreamtrips.wallet.domain.entity.RecordIssuerInfo;

import javax.inject.Inject;
import javax.inject.Named;

import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;

import static com.worldventures.dreamtrips.core.janet.JanetModule.*;
import static com.worldventures.dreamtrips.core.janet.JanetModule.JANET_WALLET;

@CommandAction
public class FetchRecordIssuerInfoCommand extends Command<RecordIssuerInfo> implements InjectableAction {

   @Inject @Named(JANET_API_LIB) Janet janet;

   private final long iin;//First 6 card digests

   public FetchRecordIssuerInfoCommand(long iin) {
      this.iin = iin;
   }

   @Override
   protected void run(CommandCallback<RecordIssuerInfo> callback) throws Throwable {
      janet.createPipe(GetBankInfoHttpAction.class)
            .createObservableResult(new GetBankInfoHttpAction(iin))
            .map(it -> it.response())
            .map(bankInfo -> new BankInfoConverter().from(bankInfo))
            .subscribe(callback::onSuccess, callback::onFail);
   }
}
