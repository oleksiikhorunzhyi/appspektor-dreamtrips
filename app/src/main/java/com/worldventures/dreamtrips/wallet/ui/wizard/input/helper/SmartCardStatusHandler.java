package com.worldventures.dreamtrips.wallet.ui.wizard.input.helper;

import android.support.annotation.NonNull;

import com.worldventures.dreamtrips.api.smart_card.status.model.SmartCardStatus;

import rx.functions.Action1;

/**
 * Handle response for SmartCardStatusHttpAction
 * For check assigned of SmartCard.
 */
public class SmartCardStatusHandler {

   public static void handleSmartCardStatus(@NonNull SmartCardStatus smartCardStatus,
         @NonNull Action1<SmartCardStatus> cardIsUnassign,
         @NonNull Action1<SmartCardStatus> cardIsAssignToAnotherDevice,
         @NonNull Action1<SmartCardStatus> cardIsAssignToAnotherUser) {

      switch (smartCardStatus) {
         case ASSIGNED_TO_CURRENT_DEVICE:
         case UNASSIGNED:
            cardIsUnassign.call(smartCardStatus);
            break;
         case ASSIGNED_TO_ANOTHER_DEVICE:
            cardIsAssignToAnotherDevice.call(smartCardStatus);
            break;
         case ASSIGNED_TO_ANOTHER_USER:
         default:
            cardIsAssignToAnotherUser.call(smartCardStatus);
            break;
      }
   }
}
