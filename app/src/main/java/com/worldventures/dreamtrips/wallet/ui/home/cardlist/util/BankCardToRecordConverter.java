package com.worldventures.dreamtrips.wallet.ui.home.cardlist.util;

import com.worldventures.dreamtrips.wallet.domain.entity.card.BankCard;

import io.techery.janet.smartcard.model.ImmutableRecord;
import io.techery.janet.smartcard.model.Record;
import rx.functions.Func1;

public class BankCardToRecordConverter  {

     public static Record convert(BankCard card) {
        return ImmutableRecord.builder()
                .csc(card.number())
                .title(card.title())
                .idx((byte) 1)
                .recType((byte) 2)
                .financialService((byte) 3)
                .exp("1216")
                .pan("Pan??")
                .t1("T1??")
                .t2("T2??")
                .t3("T3??")
                .numMeta((byte) 5)
                .build();
    }


}
