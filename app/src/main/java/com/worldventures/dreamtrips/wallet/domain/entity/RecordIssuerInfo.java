package com.worldventures.dreamtrips.wallet.domain.entity;


import com.worldventures.dreamtrips.wallet.domain.entity.record.FinancialService;
import com.worldventures.dreamtrips.wallet.domain.entity.record.RecordType;

import org.immutables.gson.Gson;
import org.immutables.value.Value;

@Value.Immutable
@Gson.TypeAdapters
public abstract class RecordIssuerInfo {

   @Value.Default
   public String bankName() {
      return "";
   }

   @Value.Default
   public FinancialService financialService() {
      return FinancialService.GENERIC;
   }

   @Value.Default
   public RecordType cardType() {
      return RecordType.FINANCIAL;
   }
}
