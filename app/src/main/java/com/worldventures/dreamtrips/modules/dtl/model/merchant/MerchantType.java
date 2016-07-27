package com.worldventures.dreamtrips.modules.dtl.model.merchant;

import java.util.Locale;

public enum MerchantType {
    RESTAURANT, BAR, UNKNOWN;

    @Override
    public String toString() {
        return super.toString().toLowerCase(Locale.US);
    }
}
