package com.worldventures.dreamtrips.wallet.ui.dashboard.detail;

import android.content.Context;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.widget.TextView;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.wallet.domain.entity.AddressInfoWithLocale;
import com.worldventures.dreamtrips.wallet.domain.entity.card.BankCard;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletFrameLayout;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.OperationScreen;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.delegate.DialogOperationScreen;
import com.worldventures.dreamtrips.wallet.ui.widget.BankCardWidget;
import com.worldventures.dreamtrips.wallet.util.AddressUtil;

import butterknife.InjectView;
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
        return new CardDetailsPresenter(getContext(), getInjector(), getPath().getBankCard());
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        toolbar.setNavigationOnClickListener(v -> navigateButtonClick());
    }

    @OnClick(R.id.delete_button)
    public void onDeleteCardClicked() {
        getPresenter().onDeleteCardRequired();
    }

    @Override
    public OperationScreen provideOperationDelegate() {
        return new DialogOperationScreen(this);
    }

    @Override
    public void setTitle(String title) {
        toolbar.setTitle(title);
    }

    @Override
    public void showCardBankInfo(BankCard bankCard) {
        bankCardWidget.setBankCardInfo(bankCard);
    }

    @Override
    public void showDefaultAddress(AddressInfoWithLocale addressInfoWithLocale) {
        addressText.setText(AddressUtil.obtainAddressLabel(addressInfoWithLocale));
    }

    protected void navigateButtonClick() {
        presenter.goBack();
    }
}
