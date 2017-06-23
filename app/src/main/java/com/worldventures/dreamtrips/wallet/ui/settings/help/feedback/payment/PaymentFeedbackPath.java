package com.worldventures.dreamtrips.wallet.ui.settings.help.feedback.payment;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.flow.path.MasterDetailPath;
import com.worldventures.dreamtrips.core.flow.path.StyledPath;
import com.worldventures.dreamtrips.core.flow.util.Layout;

@Layout(R.layout.screen_wallet_settings_help_payment_feedback)
public class PaymentFeedbackPath extends StyledPath {

   @Override
   public MasterDetailPath getMaster() {
      return this;
   }
}
