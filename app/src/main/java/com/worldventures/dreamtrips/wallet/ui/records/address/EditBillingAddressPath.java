package com.worldventures.dreamtrips.wallet.ui.records.address;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.flow.path.MasterDetailPath;
import com.worldventures.dreamtrips.core.flow.path.StyledPath;
import com.worldventures.dreamtrips.core.flow.util.Layout;
import com.worldventures.dreamtrips.wallet.domain.entity.record.Record;

@Layout(R.layout.screen_wallet_wizard_edit_card_details)
public class EditBillingAddressPath extends StyledPath {
   private final Record record;

   public EditBillingAddressPath(Record record) {
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
