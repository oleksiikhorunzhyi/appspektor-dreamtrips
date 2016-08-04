package com.worldventures.dreamtrips.wallet.ui.settings.card_details;

import android.content.Context;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.wallet.model.AddressInfo;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletFrameLayout;
import com.worldventures.dreamtrips.wallet.ui.widget.BankCardWidget;
import com.worldventures.dreamtrips.wallet.util.AddressUtil;

import butterknife.InjectView;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

public class CardDetailsScreen extends WalletFrameLayout<CardDetailsPresenter.Screen, CardDetailsPresenter, CardDetailsPath>
        implements CardDetailsPresenter.Screen {

    @InjectView(R.id.toolbar)
    Toolbar toolbar;

    @InjectView(R.id.address_textview)
    TextView addressText;
    @InjectView(R.id.card)
    BankCardWidget bankCardWidget;

    public CardDetailsScreen(Context context) {
        super(context);
    }

    public CardDetailsScreen(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public CardDetailsPresenter createPresenter() {
        return new CardDetailsPresenter(getContext(), getInjector());
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        toolbar.setNavigationOnClickListener(v -> navigateButtonClick());
        bankCardWidget.setBankCardInfo(getPath().getBankCard());
    }

    @OnCheckedChanged(R.id.default_payment_card_checkbox)
    public void onUsageDefaultAddressChanged(boolean useAsDefaultAddress) {
        getPresenter().useDefaultAddressRequired(useAsDefaultAddress);
    }

    @OnClick(R.id.delete_button)
    public void onDeleteCardClicked() {
        getPresenter().onDeleteCardRequired();
    }

    @Override
    public void showDefaultAddress(AddressInfo addressInfo, String country) {
        addressText.setText(AddressUtil.obtainAddressLabel(addressInfo, country));
    }

    @Override
    public void showCardInfo(String cardNumber) {
        toolbar.setTitle(cardNumber);
    }

    protected void navigateButtonClick() {
        presenter.goToBack();
    }

}
