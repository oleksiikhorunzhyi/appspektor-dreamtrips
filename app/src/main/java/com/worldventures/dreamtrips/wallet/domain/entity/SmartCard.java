package com.worldventures.dreamtrips.wallet.domain.entity;

import android.support.annotation.Nullable;

import org.immutables.value.Value;

@Value.Immutable
public abstract class SmartCard {

    public abstract String getSmartCardId();

    public abstract CardStatus getStatus();

    @Value.Default
    public String getCardName() {
        return "";
    }

    @Nullable
    public abstract String getUserPhoto();

    public enum CardStatus {
        ACTIVE, INACTIVE, DRAFT
    }
}
