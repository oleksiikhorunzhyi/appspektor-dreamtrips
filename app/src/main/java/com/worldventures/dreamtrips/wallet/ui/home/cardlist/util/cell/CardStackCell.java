package com.worldventures.dreamtrips.wallet.ui.home.cardlist.util.cell;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.techery.spares.adapter.BaseDelegateAdapter;
import com.techery.spares.annotations.Layout;
import com.techery.spares.module.Injector;
import com.techery.spares.module.qualifier.ForActivity;
import com.techery.spares.ui.view.cell.AbstractDelegateCell;
import com.techery.spares.ui.view.cell.CellDelegate;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.wallet.domain.entity.card.BankCard;
import com.worldventures.dreamtrips.wallet.domain.entity.card.ImmutableBankCard;
import com.worldventures.dreamtrips.wallet.ui.home.cardlist.util.CardStackViewModel;
import com.worldventures.dreamtrips.wallet.ui.home.cardlist.util.OverlapDecoration;

import javax.inject.Inject;

import butterknife.InjectView;

@Layout(R.layout.adapter_item_wallet_cardstack)
public class CardStackCell extends AbstractDelegateCell<CardStackViewModel, CardStackCell.Delegate> {

    private static final double VISIBLE_SCALE = 0.75;

    @InjectView(R.id.cardStack) RecyclerView cardStack;
    @Inject
    @ForActivity
    Injector injector;

    public CardStackCell(View view) {
        super(view);
    }

    @Override protected void syncUIStateWithModel() {
        BankCardCell.Delegate childCellDelegate = model -> cellDelegate.onCardClicked(model);

        BaseDelegateAdapter adapter = new BaseDelegateAdapter(itemView.getContext(), injector);
        adapter.registerCell(BankCard.class, BankCardCell.class);
        adapter.registerCell(ImmutableBankCard.class, BankCardCell.class);
        adapter.registerDelegate(BankCard.class, childCellDelegate);
        adapter.registerDelegate(ImmutableBankCard.class, childCellDelegate);

        adapter.setHasStableIds(true);
        adapter.addItems(getModelObject().getBankCards());

        cardStack.setAdapter(adapter);
        int dimension = itemView.getResources().getDimensionPixelSize(R.dimen.wallet_bank_card_default_height);
        cardStack.addItemDecoration(new OverlapDecoration((int) (dimension * VISIBLE_SCALE * -1)));
        cardStack.setLayoutManager(new LinearLayoutManager(itemView.getContext()));
        cardStack.setNestedScrollingEnabled(false);
    }

    @Override public void prepareForReuse() {

    }

    public interface Delegate extends CellDelegate<CardStackViewModel> {
        void onCardClicked(BankCard bankCard);
    }
}

