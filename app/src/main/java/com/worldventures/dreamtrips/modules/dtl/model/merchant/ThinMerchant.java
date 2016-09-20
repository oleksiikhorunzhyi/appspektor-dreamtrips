package com.worldventures.dreamtrips.modules.dtl.model.merchant;

import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.serializers.CompatibleFieldSerializer;
import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.api.dtl.merchants.model.MerchantType;
import com.worldventures.dreamtrips.api.dtl.merchants.model.OfferType;
import com.worldventures.dreamtrips.api.dtl.merchants.model.PartnerStatus;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.offer.Currency;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.offer.Offer;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.operational_hour.OperationDay;

import org.immutables.value.Value;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.util.List;

@DefaultSerializer(CompatibleFieldSerializer.class)
@Value.Immutable
public abstract class ThinMerchant implements Serializable {

   public abstract String id();
   public abstract MerchantType type();
   public abstract PartnerStatus partnerStatus();
   public abstract String displayName();
   public abstract @Nullable Coordinates coordinates();
   public abstract @Nullable String city();
   public abstract @Nullable String state();
   public abstract @Nullable String country();
   public abstract @Nullable Integer budget();
   public abstract @Nullable Double rating();
   public abstract @Nullable Double distance();
   public abstract @Nullable List<Offer> offers();
   public abstract @Nullable String timeZone();
   public abstract @Nullable List<ThinAttribute> categories();
   public abstract @Nullable List<MerchantMedia> images();
   public abstract @Nullable List<OperationDay> operationDays();

   @Value.Derived
   public boolean hasPoints() {
      return offersCount(OfferType.POINTS) > 0;
   }

   @Value.Derived
   public boolean hasPerks() {
      return offersCount(OfferType.PERK) > 0;
   }

   @Value.Derived
   public int offersCount(OfferType type) {
      return !hasOffers() ? 0 : Queryable.from(offers()).filter(offer -> offer.type() == type).count();
   }

   @Value.Derived
   public boolean hasOffers() {
      return offers() != null && !offers().isEmpty();
   }

   @Value.Derived
   public boolean hasOperationDays() {
      return operationDays() != null && operationDays().isEmpty();
   }

   @Value.Derived
   public int timeOffset() {
      try {
         return Integer.valueOf(timeZone());
      } catch (Exception e) {
         return 0;
      }
   }

}
