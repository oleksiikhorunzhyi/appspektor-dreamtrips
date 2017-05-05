package com.worldventures.dreamtrips.wallet.ui.wizard.records.sync;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.flow.path.MasterDetailPath;
import com.worldventures.dreamtrips.core.flow.path.StyledPath;
import com.worldventures.dreamtrips.core.flow.util.Layout;
import com.worldventures.dreamtrips.wallet.ui.wizard.records.SyncAction;

@Layout(R.layout.screen_wallet_wizard_sync_records)
public class SyncRecordsPath extends StyledPath {

   final SyncAction syncAction;

   public SyncRecordsPath(SyncAction syncAction) {
      this.syncAction = syncAction;
   }

   @Override
   public MasterDetailPath getMaster() {
      return this;
   }
}
