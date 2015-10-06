package com.worldventures.dreamtrips.modules.dtl.model;

import android.support.annotation.StringRes;

import com.worldventures.dreamtrips.R;

public class DtlPlace {

    public enum PlaceType {
        PLACES("offers", R.string.dtl_place_tab_offers),
        DINING("dining", R.string.dtl_place_tab_dining);

        protected String name;
        protected int res;

        PlaceType(String name, @StringRes int res) {
            this.name = name;
            this.res = res;
        }

        public String getName() {
            return name;
        }

        @StringRes
        public int getRes() {
            return res;
        }
    }
}
