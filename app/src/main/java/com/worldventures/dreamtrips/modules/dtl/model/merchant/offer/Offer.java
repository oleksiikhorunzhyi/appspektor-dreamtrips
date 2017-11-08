package com.worldventures.dreamtrips.modules.dtl.model.merchant.offer;

import android.support.annotation.Nullable;

import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.serializers.CompatibleFieldSerializer;
import com.worldventures.dreamtrips.api.dtl.merchants.model.OfferType;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.MerchantMedia;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.operational_hour.OperationDay;

import org.immutables.gson.Gson;
import org.immutables.value.Value;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@DefaultSerializer(CompatibleFieldSerializer.class)
@Gson.TypeAdapters
@Value.Immutable
public interface Offer extends Serializable {

   String id();
   OfferType type();
   String title();
   String description();
   String disclaimer();
   @Nullable Date startDate();
   @Nullable Date endDate();
   @Nullable List<OperationDay> operationDays();
   @Nullable List<MerchantMedia> images();

}
