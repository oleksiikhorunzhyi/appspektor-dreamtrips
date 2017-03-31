package com.worldventures.dreamtrips.wallet.ui.wizard.records;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.flow.path.MasterDetailPath;
import com.worldventures.dreamtrips.core.flow.path.StyledPath;
import com.worldventures.dreamtrips.core.flow.util.Layout;

@Layout(R.layout.screen_wallet_wizard_sync_records)
public class SyncRecordsPath extends StyledPath {

   private final SyncAction syncAction;

   public SyncRecordsPath(SyncAction syncAction) {
      this.syncAction = syncAction;
   }

   public SyncAction syncAction() {
      return syncAction;
   }

   @Override
   public MasterDetailPath getMaster() {
      return this;
   }
}
