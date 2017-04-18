package com.worldventures.dreamtrips.wallet.analytics.oncard.action;

import com.worldventures.dreamtrips.core.utils.tracksystem.AdobeTracker;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsEvent;
import com.worldventures.dreamtrips.wallet.domain.entity.record.Record;

import io.techery.janet.smartcard.model.analytics.AnalyticsLog;
import io.techery.janet.smartcard.model.analytics.AnalyticsLogEntryCardSwipe;
import io.techery.janet.smartcard.model.analytics.AnalyticsLogEntryPaymentMode;

@AnalyticsEvent(action = "wallet:oncard:pay", trackers = AdobeTracker.TRACKER_KEY)
public class SmartCardPaymentAction extends SmartCardAnalyticsAction {

   private int recordId;

   SmartCardPaymentAction(AnalyticsLog logEntry) {
      super(logEntry);
   }

   public int getRecordId() {
      return recordId;
   }

   public void setRecord(Record record) {
      if (record != null) {
         attributeMap.put("paycardnickname", record.nickName());
         fillRecordDetails(record);
      }
   }

   @Override
   protected void processLog(int type, AnalyticsLog logEntry) {
      super.processLog(type, logEntry);
      switch (type) {
         case AnalyticsLog.PAYMENT_MODE:
            AnalyticsLogEntryPaymentMode paymentMode = (AnalyticsLogEntryPaymentMode) logEntry;
            recordId = paymentMode.cardId();

            attributeMap.put("paymentMode", "1");
            break;
         case AnalyticsLog.CARD_SWIPE:
            AnalyticsLogEntryCardSwipe cardSwipe = (AnalyticsLogEntryCardSwipe) logEntry;
            recordId = cardSwipe.cardId();

            attributeMap.put("ocpayswipe", String.valueOf(cardSwipe.swipeSequenceNumber()));
            attributeMap.put("ocpayswipespd", String.valueOf(cardSwipe.swipeSpeed()));
            attributeMap.put("ocpaytrack", String.valueOf(cardSwipe.trackIdentifier()));
            break;
      }
   }
}
