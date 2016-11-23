package com.worldventures.dreamtrips.core.session.acl;

import android.support.annotation.StringDef;

import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.SOURCE;

public class Feature {
   @FeatureName public final String name;

   public Feature(String name) {
      this.name = name;
   }

   @Retention(SOURCE)
   @StringDef({TRIPS, REP_TOOLS, SOCIAL, DTL, REP_SUGGEST_MERCHANT, BOOK_TRAVEL, BOOK_TRIP, MEMBERSHIP, WALLET, WALLET_PROVISIONING})
   public @interface FeatureName {}

   public static final String TRIPS = "trips";
   public static final String REP_TOOLS = "repTools";
   public static final String SOCIAL = "social";
   public static final String DTL = "discover";
   public static final String REP_SUGGEST_MERCHANT = "repSuggestMerchant";
   public static final String BOOK_TRAVEL = "bookTravel";
   public static final String BOOK_TRIP = "bookTrip";
   public static final String MEMBERSHIP = "membership";
   public static final String WALLET = "wallet";
   public static final String WALLET_PROVISIONING = "wallet_provisioning";
   public static final String UNKNOWN = "unknown";

}
