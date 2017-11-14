package com.worldventures.wallet.analytics;


import com.worldventures.core.service.analytics.Attribute;
import com.worldventures.wallet.domain.entity.record.Record;

abstract class BaseSetDefaultCardAction extends BaseCardDetailsWithDefaultAction {

   @Attribute("setdefaultcard") final String setDefaultCard = "1";
   @Attribute("setdefaultwhere") String setDefaultWhere;

   @Override
   public void fillRecordDetails(Record record) {
      super.fillRecordDetails(record);
      defaultPaycard = "Yes";
   }
}
