package com.worldventures.dreamtrips.wallet.ui.dashboard.list.util.cell;

import android.view.View;

import com.techery.spares.annotations.Layout;
import com.techery.spares.ui.view.cell.AbstractDelegateCell;
import com.techery.spares.ui.view.cell.CellDelegate;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.wallet.ui.dashboard.list.util.BankCardViewModel;
import com.worldventures.dreamtrips.wallet.ui.widget.BankCardWidget;
import com.worldventures.dreamtrips.wallet.util.BankCardHelper;

import javax.inject.Inject;

import butterknife.InjectView;

@Layout(R.layout.adapter_item_wallet_bankcard)
public class BankCardCell extends AbstractDelegateCell<BankCardViewModel, BankCardCell.Delegate> {

   @InjectView(R.id.bank_card) BankCardWidget bankCardWidget;
   @Inject BankCardHelper bankCardHelper;

   public BankCardCell(View view) {
      super(view);
   }

   @Override
   protected void syncUIStateWithModel() {
      bankCardWidget.setBankCardInfoForList(bankCardHelper, getModelObject().bankCard);
      bankCardWidget.setAsDefault(getModelObject().defaultCard);
      bankCardWidget.setBankCardHolder(getAdapterPosition() % 2 == 0? R.drawable.creditcard_blue : R.drawable.creditcard_darkblue);
      itemView.setOnClickListener(v -> cellDelegate.onCellClicked(getModelObject()));
   }

   @Override
   public void prepareForReuse() {

   }

   public interface Delegate extends CellDelegate<BankCardViewModel> {

   }
}
