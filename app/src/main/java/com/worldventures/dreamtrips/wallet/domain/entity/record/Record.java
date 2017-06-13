package com.worldventures.dreamtrips.wallet.domain.entity.record;

import org.immutables.gson.Gson;
import org.immutables.value.Value;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
@Gson.TypeAdapters
public abstract class Record {

   @Nullable
   public abstract String id();

   @Value.Default
   public RecordType recordType() {
      return RecordType.FINANCIAL;
   }

   public abstract String number();

   @Nullable
   public abstract String numberLastFourDigits();

   public abstract String expDate();

   @Value.Default
   public String cardHolderFirstName() {
      return "";
   }

   @Value.Default
   public String cardHolderMiddleName() {
      return "";
   }

   @Value.Default
   public String cardHolderLastName() {
      return "";
   }


   @Value.Default
   public String nickName() { // TODO: 12/6/16 remove to cardName
      return "";
   }

   @Value.Default
   public String bankName() {
      return "";
   }

   @Value.Default
   public FinancialService financialService() {
      return FinancialService.GENERIC;
   }

   @Value.Default
   public String cvv() {
      return "";
   }

   @Nullable
   public abstract String track1();

   @Nullable
   public abstract String track2();

   @Nullable
   public abstract String track3();

   @Value.Default
   public String version() { // TODO: 12/6/16 remove to cardName
      return "";
   }

   @Override
   public boolean equals(Object obj) {
      if (super.equals(obj)) return true;
      if (obj instanceof Record) {
         final Record record = (Record) obj;
         final String recordId = record.id();
         final String id = id();
         return recordId != null && id != null && recordId.equals(id);
      }
      return false;
   }

}