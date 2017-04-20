package com.worldventures.dreamtrips.wallet.ui.records.detail;


import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.flow.path.MasterDetailPath;
import com.worldventures.dreamtrips.core.flow.path.StyledPath;
import com.worldventures.dreamtrips.core.flow.util.Layout;
import com.worldventures.dreamtrips.wallet.domain.entity.record.Record;
import com.worldventures.dreamtrips.wallet.ui.dashboard.util.model.TransitionModel;

@Layout(R.layout.screen_wallet_wizard_view_card_details)
public class CardDetailsPath extends StyledPath {

   private final Record record;
   private TransitionModel transitionModel;

   public CardDetailsPath(Record record) {
      this.record = record;
   }

   public CardDetailsPath(Record record, TransitionModel transitionModel) {
      this.record = record;
      this.transitionModel = transitionModel;
   }

   public TransitionModel getTransitionModel() {
      return this.transitionModel;
   }

   public Record getRecord() {
      return record;
   }

   @Override
   public MasterDetailPath getMaster() {
      return this;
   }
}
