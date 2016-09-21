package com.worldventures.dreamtrips.modules.dtl.helper.inflater;

import android.content.Context;
import android.text.Spannable;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.api.dtl.merchants.model.MerchantType;
import com.worldventures.dreamtrips.api.dtl.merchants.model.OfferType;
import com.worldventures.dreamtrips.api.dtl.merchants.model.PartnerStatus;
import com.worldventures.dreamtrips.modules.dtl.helper.MerchantHelper;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.Coordinates;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.MerchantMedia;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.ThinAttribute;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.disclaimer.Disclaimer;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.offer.Currency;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.offer.Offer;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.operational_hour.OperationDay;

import org.immutables.value.Value;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@Value.Immutable
public abstract class MerchantAttributes {

   public abstract String id();
   public abstract MerchantType type();
   public abstract PartnerStatus partnerStatus();
   public abstract String displayName();
   @Nullable public abstract String address();
   @Nullable public abstract String city();
   @Nullable public abstract String state();
   @Nullable public abstract String country();
   @Nullable public abstract Coordinates coordinates();
   @Nullable public abstract String description();
   @Nullable public abstract Integer budget();
   @Nullable public abstract Double distance();
   @Nullable public abstract String zip();
   @Nullable public abstract Double rating();
   @Nullable public abstract String phone();
   @Nullable public abstract String email();
   @Nullable public abstract String website();
   @Nullable public abstract List<Currency> currencies();
   @Nullable public abstract List<Offer> offers();
   @Nullable public abstract String timeZone();
   @Nullable public abstract List<ThinAttribute> categories();
   @Nullable public abstract List<ThinAttribute> amenities();
   @Nullable public abstract List<MerchantMedia> images();
   @Nullable public abstract List<OperationDay> operationDays();
   @Nullable public abstract List<Disclaimer> disclaimers();

   @Value.Derived public boolean hasPoints() {
      return offersCount(OfferType.POINTS) > 0;
   }

   @Value.Derived public boolean hasPerks() {
      return offersCount(OfferType.PERK) > 0;
   }

   @Value.Derived public boolean hasOperationDays() {
      return operationDays() != null && !operationDays().isEmpty();
   }

   @Value.Derived public boolean hasOffers() {
      return offers() != null && !offers().isEmpty();
   }

   @Value.Derived public int offersCount(OfferType type) {
      return !hasOffers() ? 0 : Queryable.from(offers()).filter(offer -> offer.type() == type).count();
   }

   @Value.Derived public int timeOffset() {
      try {
         return Integer.valueOf(timeZone());
      } catch (Exception e) {
         return 0;
      }
   }

   @Value.Derived public Spannable getOperationalTime(Context context, boolean includeTime) throws Exception {
      return MerchantHelper.getOperationalTime(context, operationDays(), timeOffset(), includeTime);
   }

   @Value.Derived @Nullable public Currency defaultCurrency() {
      return currencies() != null && hasPoints() ? Queryable.from(currencies()).first(Currency::isDefault) : null;
   }

}
