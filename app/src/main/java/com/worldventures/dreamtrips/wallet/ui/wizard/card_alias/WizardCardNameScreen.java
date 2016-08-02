package com.worldventures.dreamtrips.wallet.ui.wizard.card_alias;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletFrameLayout;
import com.worldventures.dreamtrips.wallet.util.FormatException;

import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnEditorAction;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class WizardCardNameScreen extends WalletFrameLayout<WizardCardNamePresenter.Screen, WizardCardNamePresenter, WizardCardNamePath>
        implements WizardCardNamePresenter.Screen {

    @InjectView(R.id.toolbar)
    Toolbar toolbar;

    @InjectView(R.id.card_name_field)
    TextView cardName;

    @Nullable
    private SweetAlertDialog alertDialog;

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
    public void showSuccessWithDelay(Runnable action, long delay) {
        if (alertDialog == null) return;
        alertDialog.setTitleText(getResources().getString(R.string.wallet_wizard_card_alias_was_setup));
        alertDialog.changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
        postDelayed(() -> {
            alertDialog.dismiss();
            action.run();
        }, delay);
    }

    @Override
    public void notifyError(Throwable throwable) {
        if (alertDialog == null) return;
        if (throwable instanceof FormatException) {
            alertDialog.setTitleText(getResources().getString(R.string.wallet_input_data_is_incorrect));
            alertDialog.setContentText(getResources().getString(R.string.wallet_wizard_card_alias_format_error));
        } else {
            alertDialog.setTitleText(getResources().getString(R.string.error_something_went_wrong));
        }
        alertDialog.changeAlertType(SweetAlertDialog.ERROR_TYPE);
    }

    @Override
    public void showProgress() {
        alertDialog = new SweetAlertDialog(getContext(), SweetAlertDialog.PROGRESS_TYPE);
        alertDialog.setTitleText(getResources().getString(R.string.wallet_wizard_card_alias_setup));
        alertDialog.setCancelable(false);
        alertDialog.show();
    }

    @Override
    public void hideProgress() {
        if (alertDialog == null) return;
        alertDialog.dismiss();
        alertDialog = null;
    }
}
