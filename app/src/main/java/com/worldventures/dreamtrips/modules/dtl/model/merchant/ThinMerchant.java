package com.worldventures.dreamtrips.modules.dtl.model.merchant;

import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.serializers.CompatibleFieldSerializer;
import com.worldventures.dreamtrips.api.dtl.merchants.model.MerchantType;
import com.worldventures.dreamtrips.api.dtl.merchants.model.PartnerStatus;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.offer.Offer;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.operational_hour.OperationDay;

import org.immutables.value.Value;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.util.List;

@DefaultSerializer(CompatibleFieldSerializer.class)
@Value.Immutable
public interface ThinMerchant extends Serializable {

   String id();
   MerchantType type();
   PartnerStatus partnerStatus();
   String displayName();
   @Nullable Coordinates coordinates();
   @Nullable String city();
   @Nullable String state();
   @Nullable String country();
   @Nullable Integer budget();
   @Nullable Double rating();
   @Nullable Double distance();
   @Nullable List<Offer> offers();
   @Nullable String timeZone();
   @Nullable List<ThinAttribute> categories();
   @Nullable List<MerchantMedia> images();
   @Nullable List<OperationDay> operationDays();

}
