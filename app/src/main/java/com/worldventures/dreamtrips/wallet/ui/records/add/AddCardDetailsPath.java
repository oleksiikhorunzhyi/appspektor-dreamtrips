package com.worldventures.dreamtrips.wallet.ui.records.add;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.flow.path.MasterDetailPath;
import com.worldventures.dreamtrips.core.flow.path.StyledPath;
import com.worldventures.dreamtrips.core.flow.util.Layout;
import com.worldventures.dreamtrips.wallet.domain.entity.record.Record;

@Layout(R.layout.screen_wallet_wizard_add_card_details)
public class AddCardDetailsPath extends StyledPath {

   private final Record record;

   public AddCardDetailsPath(Record record) {
      this.record = record;
   }

   public Record getRecord() {
      return record;
   }

   @Override
   public MasterDetailPath getMaster() {
      return this;
   }
}
