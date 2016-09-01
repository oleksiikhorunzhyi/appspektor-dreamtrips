package com.worldventures.dreamtrips.wallet.ui.dashboard.list.util.cell;

import android.view.View;

import com.techery.spares.annotations.Layout;
import com.techery.spares.ui.view.cell.AbstractDelegateCell;
import com.techery.spares.ui.view.cell.CellDelegate;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.wallet.ui.dashboard.list.util.BankCardViewModel;
import com.worldventures.dreamtrips.wallet.ui.widget.BankCardWidget;

import butterknife.InjectView;

@Layout(R.layout.adapter_item_wallet_bankcard)
public class BankCardCell extends AbstractDelegateCell<BankCardViewModel, BankCardCell.Delegate> {

   @InjectView(R.id.bank_card) BankCardWidget bankCardWidget;

   public BankCardCell(View view) {
      super(view);
   }

   @Override
   protected void syncUIStateWithModel() {
      bankCardWidget.setBankCardInfo(getModelObject().bankCard);
      bankCardWidget.setAsDefault(getModelObject().defaultCard);
      itemView.setOnClickListener(v -> cellDelegate.onCellClicked(getModelObject()));
   }

   @Override
   public void prepareForReuse() {

   }

   public interface Delegate extends CellDelegate<BankCardViewModel> {

   }
}
