package com.worldventures.dreamtrips.wallet.ui.home.cardlist.util;

import android.view.View;
import android.widget.TextView;

import com.techery.spares.annotations.Layout;
import com.techery.spares.ui.view.cell.AbstractDelegateCell;
import com.techery.spares.ui.view.cell.CellDelegate;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCard;

import butterknife.InjectView;

@Layout(R.layout.adapter_item_wallet_smartcard)
public class SmartCardCell extends AbstractDelegateCell<SmartCard, SmartCardCell.Delegate> {

    @InjectView(R.id.bankLabel) TextView cardLabel;
    @InjectView(R.id.connectedCardCount) TextView connectedCardCount;

    public SmartCardCell(View view) {
        super(view);
    }

    @Override protected void syncUIStateWithModel() {

    }

    @Override public void prepareForReuse() {

    }

    public interface Delegate extends CellDelegate<SmartCard> {

    }
}
