package com.worldventures.dreamtrips.wallet.domain.entity.card;

import android.support.annotation.Nullable;

import com.worldventures.dreamtrips.wallet.domain.entity.AddressInfo;

import org.immutables.value.Value;

import io.techery.janet.smartcard.model.Record;

@Value.Immutable
public abstract class BankCard implements Card {

    @Nullable
    public abstract String title();

    @Nullable
    public abstract CardType cardType();

    @Nullable
    public abstract AddressInfo addressInfo();

    public abstract Record.FinancialService type();

    @Value.Default
    public int cvv(){
        return 0;
    }

    public enum CardType {
        DEBIT, CREDIT
    }
}