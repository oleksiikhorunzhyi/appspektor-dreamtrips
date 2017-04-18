package com.worldventures.dreamtrips.wallet.ui.wizard.unassign;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.flow.path.MasterDetailPath;
import com.worldventures.dreamtrips.core.flow.path.StyledPath;
import com.worldventures.dreamtrips.core.flow.util.Layout;

@Layout(R.layout.screen_wallet_existing_device_detect)
public class ExistingDeviceDetectPath extends StyledPath {

   public final String scId;

   public ExistingDeviceDetectPath(String scId) {
      this.scId = scId;
   }

   @Override
   public MasterDetailPath getMaster() {
      return this;
   }
}
