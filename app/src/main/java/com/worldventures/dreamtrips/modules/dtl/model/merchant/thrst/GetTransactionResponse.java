package com.worldventures.dreamtrips.modules.dtl.model.merchant.thrst;

      import android.support.annotation.Nullable;

      import com.esotericsoftware.kryo.DefaultSerializer;
      import com.esotericsoftware.kryo.serializers.CompatibleFieldSerializer;
      import com.google.gson.annotations.SerializedName;

      import org.immutables.value.Value;

      import java.io.Serializable;

@DefaultSerializer(CompatibleFieldSerializer.class)
@Value.Immutable
public interface GetTransactionResponse extends Serializable {

   @Nullable String transactionId();
   @Nullable String merchantId();
   @Nullable String userId();
   @Nullable String transactionType();
   @Nullable String checkinTimestamp();
   @Nullable String billImagePath();
   @Nullable String pointsAmount();
   @Nullable String billTotal();
   @Nullable String transactionStatus();
   @Nullable String tax();
   @Nullable String tip();
   @Nullable String subTotal();

}