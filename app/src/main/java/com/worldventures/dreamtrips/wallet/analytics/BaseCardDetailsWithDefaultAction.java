package com.worldventures.dreamtrips.wallet.analytics;

import com.worldventures.dreamtrips.core.utils.tracksystem.Attribute;
import com.worldventures.dreamtrips.wallet.domain.entity.record.Record;

public abstract class BaseCardDetailsWithDefaultAction extends BaseCardDetailsAction {

   @Attribute("defaultpaycard") String defaultPaycard;

   public void fillPaycardInfo(Record record, boolean isDefault) {
      fillRecordDetails(record);
      defaultPaycard = isDefault ? "Yes" : "No";
   }
}
