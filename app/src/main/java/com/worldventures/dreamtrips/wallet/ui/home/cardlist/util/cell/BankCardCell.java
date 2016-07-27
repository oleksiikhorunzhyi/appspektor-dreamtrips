package com.worldventures.dreamtrips.wallet.ui.home.cardlist.util.cell;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.techery.spares.annotations.Layout;
import com.techery.spares.ui.view.cell.AbstractDelegateCell;
import com.techery.spares.ui.view.cell.CellDelegate;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.wallet.domain.entity.card.BankCard;

import butterknife.InjectView;

@Layout(R.layout.adapter_item_wallet_bankcard)
public class BankCardCell extends AbstractDelegateCell<BankCard, BankCardCell.Delegate> {

    @InjectView(R.id.bankLabel) TextView bankName;
    @InjectView(R.id.cardNumber) TextView cardNumber;
    @InjectView(R.id.someStrangeInfo) TextView someStrangeInfo;
    @InjectView(R.id.typeIcon) ImageView typeIcon;
    @InjectView(R.id.expireDate) TextView expireDate;

    public BankCardCell(View view) {
        super(view);
    }

    @Override protected void syncUIStateWithModel() {
        itemView.setOnClickListener(v -> cellDelegate.onCellClicked(getModelObject()));
    }

    @Override public void prepareForReuse() {

    }

    public interface Delegate extends CellDelegate<BankCard> {

    }
}
