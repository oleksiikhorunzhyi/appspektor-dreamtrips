package com.worldventures.dreamtrips.wallet.ui.wizard.card_alias;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletFrameLayout;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.OperationScreen;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.delegate.DialogOperationScreen;

import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnEditorAction;

public class WizardCardNameScreen extends WalletFrameLayout<WizardCardNamePresenter.Screen, WizardCardNamePresenter, WizardCardNamePath>
        implements WizardCardNamePresenter.Screen {

    @InjectView(R.id.toolbar)
    Toolbar toolbar;

    @InjectView(R.id.card_name_field)
    TextView cardName;

    public WizardCardNameScreen(Context context) {
        super(context);
    }

    public WizardCardNameScreen(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @NonNull
    @Override
    public WizardCardNamePresenter createPresenter() {
        return new WizardCardNamePresenter(getContext(), getInjector(), getPath().getSmartCardId());
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        toolbar.setNavigationOnClickListener(v -> navigateButtonClick());
    }

    protected void navigateButtonClick() {
        presenter.goToBack();
    }

    @OnClick(R.id.next_button)
    public void nextClick() {
        presenter.setupCardName();
    }

    @OnEditorAction(R.id.card_name_field)
    public boolean actionNext(int action) {
        if (action != EditorInfo.IME_ACTION_NEXT) return false;
        presenter.setupCardName();
        return true;
    }

    @NonNull
    @Override
    public String getCardName() {
        return cardName.getText().toString();
    }

    @Override
    public OperationScreen provideOperationDelegate() {
        return new DialogOperationScreen(this);
    }
}
