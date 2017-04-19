package com.worldventures.dreamtrips.wallet.ui.settings.help.feedback;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.flow.path.MasterDetailPath;
import com.worldventures.dreamtrips.core.flow.path.StyledPath;
import com.worldventures.dreamtrips.core.flow.util.Layout;

@Layout(R.layout.wallet_settings_help_feedback)
public class SendFeedbackPath extends StyledPath {

   @Override
   public MasterDetailPath getMaster() {
      return this;
   }
}
