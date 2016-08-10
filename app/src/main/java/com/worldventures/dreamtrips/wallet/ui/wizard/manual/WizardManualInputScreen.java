package com.worldventures.dreamtrips.wallet.ui.wizard.manual;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletFrameLayout;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.OperationScreen;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.delegate.DialogOperationScreen;

import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnEditorAction;

public class WizardManualInputScreen extends WalletFrameLayout<WizardManualInputPresenter.Screen, WizardManualInputPresenter, WizardManualInputPath>
        implements WizardManualInputPresenter.Screen {
    @InjectView(R.id.toolbar)
    Toolbar toolbar;

    @InjectView(R.id.wallet_wizard_manual_input_scid)
    EditText scidNumberInput;

    public WizardManualInputScreen(Context context) {
        super(context);
    }

    public WizardManualInputScreen(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        toolbar.setNavigationOnClickListener(v -> getPresenter().goBack());
    }

    @NonNull
    @Override
    public WizardManualInputPresenter createPresenter() {
        return new WizardManualInputPresenter(getContext(), getInjector());
    }

    @OnEditorAction(R.id.wallet_wizard_manual_input_scid)
    boolean actionNext(int action) {
        if (action == EditorInfo.IME_ACTION_NEXT) {
            getPresenter().checkBarcode(scidNumberInput.getText().toString());
            return true;
        }
        return false;
    }

    @Override
    public OperationScreen provideOperationDelegate() {
        return new DialogOperationScreen(this);
    }

    @OnClick(R.id.wallet_wizard_manual_input_next_btn)
    void onNextClicked() {
        getPresenter().checkBarcode(scidNumberInput.getText().toString());
    }
}