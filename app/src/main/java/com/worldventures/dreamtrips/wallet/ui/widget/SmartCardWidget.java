package com.worldventures.dreamtrips.wallet.ui.widget;

import android.content.Context;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.innahema.collections.query.queriables.Queryable;
import com.jakewharton.rxbinding.widget.RxCompoundButton;
import com.raizlabs.android.dbflow.annotation.NotNull;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.utils.QuantityHelper;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCard;
import com.worldventures.dreamtrips.wallet.ui.dashboard.list.util.CardStackViewModel;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import rx.Observable;

public class SmartCardWidget extends FrameLayout {

    @InjectView(R.id.cardListSCAvatar) SimpleDraweeView scAvatar;
    @InjectView(R.id.bankLabel) TextView bankLabel;
    @InjectView(R.id.connectedCardsCount) TextView connectedCardsCount;
    @InjectView(R.id.cbLock) CheckBox cbLock;

    public SmartCardWidget(Context context) {
        this(context, null);
    }

    public SmartCardWidget(Context context, AttributeSet attrs) {
        super(context, attrs);
        setup();
    }

    private void setup() {
        LayoutInflater.from(getContext()).inflate(R.layout.adapter_item_wallet_smartcard, this);
        ButterKnife.inject(this);
    }

    public void bindCard(SmartCard smartCard) {
        String url = smartCard.userPhoto();
        bankLabel.setText(smartCard.cardName());
        if (url != null) scAvatar.setImageURI(Uri.parse(url));
        cbLock.setChecked(smartCard.lock());

    }

    public void bindCount(List<CardStackViewModel> items) {
        int cardCount = stacksToItemsCount(items);
        if (cardCount > 0) {
            int resId = QuantityHelper.chooseResource(cardCount, R.string.wallet_card_list_record_connected, R.string.wallet_card_list_records_connected);
            connectedCardsCount.setText(getResources().getString(resId, cardCount));
            connectedCardsCount.setVisibility(VISIBLE);
        } else {
            connectedCardsCount.setVisibility(INVISIBLE);
        }
    }

    private int stacksToItemsCount(@NotNull List<CardStackViewModel> items) {
        if (items == null) return 0;
        Integer sum = Queryable.from(items)
                .sum(stack -> stack.getBankCards() != null ? stack.getBankCards().size() : 0);
        return sum != null ? sum : 0;
    }

    public void setLockBtnEnabled(boolean isEnabled) {
        cbLock.setEnabled(isEnabled);
    }

    public Observable<Boolean> lockStatus() {
        return RxCompoundButton.checkedChanges(cbLock);
    }
}
