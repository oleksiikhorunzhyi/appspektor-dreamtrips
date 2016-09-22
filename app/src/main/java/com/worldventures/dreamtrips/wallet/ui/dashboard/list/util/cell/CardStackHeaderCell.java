package com.worldventures.dreamtrips.wallet.ui.dashboard.list.util.cell;

import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.techery.spares.annotations.Layout;
import com.techery.spares.ui.view.cell.AbstractDelegateCell;
import com.techery.spares.ui.view.cell.CellDelegate;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.wallet.ui.dashboard.list.util.CardStackHeaderHolder;
import com.worldventures.dreamtrips.wallet.ui.widget.SmartCardWidget;

import butterknife.InjectView;

@Layout(R.layout.adapter_item_wallet_cardstack_header)
public class CardStackHeaderCell extends AbstractDelegateCell<CardStackHeaderHolder, CardStackHeaderCell.Delegate> {

   @InjectView(R.id.widget_dashboard_smart_card) SmartCardWidget smartCardWidget;
   @InjectView(R.id.toolbar) Toolbar toolbar;

   public CardStackHeaderCell(View view) {
      super(view);
   }

   @Override
   protected void syncUIStateWithModel() {
      setupToolbar();
      if (getModelObject().smartCard() != null) {
         smartCardWidget.bindCard(getModelObject().smartCard());
      }
      smartCardWidget.bindCount(getModelObject().cardCount());
      smartCardWidget.setOnSettingsClickListener(v -> cellDelegate.onSettingsChosen());
   }

   private void setupToolbar() {
      toolbar.setNavigationOnClickListener(it -> cellDelegate.onNavigateButtonClick());
   }

   @Override
   public void prepareForReuse() {

   }

   public interface Delegate extends CellDelegate<CardStackHeaderHolder> {

      void onSettingsChosen();

      void onNavigateButtonClick();
   }
}
