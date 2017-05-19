package com.worldventures.dreamtrips.wallet.analytics;


import com.worldventures.dreamtrips.core.utils.tracksystem.Attribute;
import com.worldventures.dreamtrips.wallet.domain.entity.record.Record;

abstract class BaseSetDefaultCardAction extends BaseCardDetailsWithDefaultAction {

   @Attribute("setdefaultcard") final String setDefaultCard = "1";
   @Attribute("setdefaultwhere") String setDefaultWhere;

   @Override
   public void fillRecordDetails(Record record) {
      super.fillRecordDetails(record);
      defaultPaycard = "Yes";
   }
}
