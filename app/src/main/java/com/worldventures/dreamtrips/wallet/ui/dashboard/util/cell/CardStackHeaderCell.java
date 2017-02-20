package com.worldventures.dreamtrips.wallet.ui.dashboard.util.cell;

import android.view.View;

import com.techery.spares.annotations.Layout;
import com.techery.spares.ui.view.cell.AbstractDelegateCell;
import com.techery.spares.ui.view.cell.CellDelegate;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.wallet.domain.entity.FirmwareUpdateData;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCard;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardStatus;
import com.worldventures.dreamtrips.wallet.ui.dashboard.util.CardStackHeaderHolder;
import com.worldventures.dreamtrips.wallet.ui.widget.SmartCardWidget;

import butterknife.InjectView;

@Layout(R.layout.adapter_item_wallet_cardstack_header)
public class CardStackHeaderCell extends AbstractDelegateCell<CardStackHeaderHolder, CardStackHeaderCell.Delegate> {

   @InjectView(R.id.widget_dashboard_smart_card) SmartCardWidget smartCardWidget;

   public CardStackHeaderCell(View view) {
      super(view);
   }

   @Override
   protected void syncUIStateWithModel() {
      CardStackHeaderHolder model = getModelObject();
      SmartCard smartCard = model.smartCard();
      SmartCardStatus status = model.smartCardStatus();

      if (smartCard != null && status != null) {
         FirmwareUpdateData firmwareUpdateData = model.firmware();
         smartCardWidget.bindCard(smartCard, status, firmwareUpdateData != null && firmwareUpdateData.updateAvailable());
      }
      smartCardWidget.bindCount(model.cardCount());
      smartCardWidget.setOnSettingsClickListener(v -> cellDelegate.onSettingsChosen());
   }

   @Override
   public void prepareForReuse() {

   }

   public interface Delegate extends CellDelegate<CardStackHeaderHolder> {

      void onSettingsChosen();
   }
}
