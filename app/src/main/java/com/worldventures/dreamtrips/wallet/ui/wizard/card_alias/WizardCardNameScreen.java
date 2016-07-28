package com.worldventures.dreamtrips.wallet.ui.wizard.card_alias;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.inputmethod.EditorInfo;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletFrameLayout;

import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnEditorAction;

public class WizardCardNameScreen extends WalletFrameLayout<WizardCardNamePresenter.Screen, WizardCardNamePresenter, WizardCardAliasPath>
        implements WizardCardNamePresenter.Screen {

    @InjectView(R.id.toolbar)
    Toolbar toolbar;

    public WizardCardNameScreen(Context context) {
        super(context);
    }

    public WizardCardNameScreen(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @NonNull
    @Override
    public WizardCardNamePresenter createPresenter() {
        return new WizardCardNamePresenter(getContext(), getInjector());
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
        presenter.goToNext();
    }

    @OnEditorAction(R.id.card_alias)
    public boolean actionNext(int action) {
        if (action != EditorInfo.IME_ACTION_NEXT) return false;
        presenter.goToNext();
        return true;
    }
}
