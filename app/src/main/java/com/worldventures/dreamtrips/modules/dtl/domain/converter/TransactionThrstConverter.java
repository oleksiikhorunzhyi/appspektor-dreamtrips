package com.worldventures.dreamtrips.modules.dtl.domain.converter;

import com.worldventures.core.converter.Converter;
import com.worldventures.dreamtrips.api.dtl.merchants.model.TransactionDetailsThrst;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.thrst.ImmutableTransactionDetails;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.thrst.TransactionDetails;

import io.techery.mappery.MapperyContext;

public class TransactionThrstConverter implements Converter<TransactionDetailsThrst, TransactionDetails> {

    @Override
    public Class<com.worldventures.dreamtrips.api.dtl.merchants.model.TransactionDetailsThrst> sourceClass() {
        return com.worldventures.dreamtrips.api.dtl.merchants.model.TransactionDetailsThrst.class;
    }

    @Override
    public Class<TransactionDetails> targetClass() {
        return TransactionDetails.class;
    }

    @Override
    public TransactionDetails convert(MapperyContext mapperyContext, com.worldventures.dreamtrips.api.dtl.merchants.model.TransactionDetailsThrst errors) {
        return ImmutableTransactionDetails.builder()
                .transactionId(errors.transactionId())
                .creditedAmount(errors.creditedAmount())
                .currentBalance(errors.currentBalance())
                .build();
    }
}
