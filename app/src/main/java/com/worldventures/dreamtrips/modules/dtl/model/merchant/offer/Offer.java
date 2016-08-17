package com.worldventures.dreamtrips.modules.dtl.model.merchant.offer;

import android.support.annotation.StringDef;

import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.SOURCE;

public class Offer {
   @OfferType public final String name;

   public Offer(@OfferType String name) {
      this.name = name;
   }

   @Retention(SOURCE)
   @StringDef({POINT_REWARD, PERKS})
   public @interface OfferType {}

   public static final String POINT_REWARD = "points";
   public static final String PERKS = "perk";
}
