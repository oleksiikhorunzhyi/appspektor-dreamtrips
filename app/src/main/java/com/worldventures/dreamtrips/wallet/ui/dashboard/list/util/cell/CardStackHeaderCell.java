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
      smartCardWidget.setOnLockChangedListener(null);
      if (getModelObject().smartCard() != null) {
         smartCardWidget.bindCard(getModelObject().smartCard());
      }
      smartCardWidget.setOnLockChangedListener((v, b) -> cellDelegate.onLockChanged(b));
      smartCardWidget.bindCount(getModelObject().cardCount());
   }

   private void setupToolbar() {
      toolbar.setTitle(R.string.wallet);
      toolbar.setNavigationOnClickListener(it -> cellDelegate.onNavigateButtonClick());
      toolbar.getMenu().clear();
      toolbar.inflateMenu(R.menu.menu_wallet_dashboard);
      toolbar.setOnMenuItemClickListener(this::onMenuItemClick);
   }

   private boolean onMenuItemClick(MenuItem item) {
      switch (item.getItemId()) {
         case R.id.action_card_settings:
            cellDelegate.onSettingsChosen();
            return true;
         default:
            return false;
      }
   }

   @Override
   public void prepareForReuse() {

   }

   public interface Delegate extends CellDelegate<CardStackHeaderHolder> {

      void onSettingsChosen();

      void onNavigateButtonClick();

      void onLockChanged(boolean isLocked);
   }
}
