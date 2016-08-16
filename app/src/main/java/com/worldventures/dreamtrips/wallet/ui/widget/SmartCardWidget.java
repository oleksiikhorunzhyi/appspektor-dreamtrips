package com.worldventures.dreamtrips.wallet.ui.widget;

import android.content.Context;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.jakewharton.rxbinding.widget.RxCompoundButton;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.utils.QuantityHelper;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCard;

import butterknife.ButterKnife;
import butterknife.InjectView;
import rx.Observable;

public class SmartCardWidget extends FrameLayout {

    @InjectView(R.id.cardListSCAvatar) SimpleDraweeView scAvatar;
    @InjectView(R.id.bankLabel) TextView bankLabel;
    @InjectView(R.id.connectedCardsCount) TextView connectedCardsCount;
    @InjectView(R.id.cbLock) CheckBox cbLock;
    @InjectView(R.id.batteryView) BatteryView batteryView;

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
        batteryView.setLevel(smartCard.batteryLevel());
    }

    public void bindCount(int cardCount) {
        if (cardCount > 0) {
            int resId = QuantityHelper.chooseResource(cardCount, R.string.wallet_card_list_record_connected, R.string.wallet_card_list_records_connected);
            connectedCardsCount.setText(getResources().getString(resId, cardCount));
            connectedCardsCount.setVisibility(VISIBLE);
        } else {
            connectedCardsCount.setVisibility(INVISIBLE);
        }
    }

    public void setLockBtnEnabled(boolean isEnabled) {
        cbLock.setEnabled(isEnabled);
    }

    public Observable<Boolean> lockStatus() {
        return RxCompoundButton.checkedChanges(cbLock);
    }
}
