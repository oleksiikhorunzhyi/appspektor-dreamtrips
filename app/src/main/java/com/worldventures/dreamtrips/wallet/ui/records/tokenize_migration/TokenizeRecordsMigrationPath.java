package com.worldventures.dreamtrips.wallet.ui.records.tokenize_migration;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.flow.path.MasterDetailPath;
import com.worldventures.dreamtrips.core.flow.path.StyledPath;
import com.worldventures.dreamtrips.core.flow.util.Layout;

@Layout(R.layout.screen_tokenize_records_migration)
public class TokenizeRecordsMigrationPath extends StyledPath {

   @Override
   public MasterDetailPath getMaster() {
      return this;
   }

}