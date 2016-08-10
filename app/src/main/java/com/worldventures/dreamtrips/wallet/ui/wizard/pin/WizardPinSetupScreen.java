package com.worldventures.dreamtrips.wallet.ui.wizard.pin;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.AttributeSet;

import com.facebook.drawee.view.SimpleDraweeView;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletFrameLayout;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.OperationScreen;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.delegate.DialogOperationScreen;

import butterknife.InjectView;
import butterknife.OnClick;

public class WizardPinSetupScreen extends WalletFrameLayout<WizardPinSetupPresenter.Screen, WizardPinSetupPresenter, WizardPinSetupPath>
        implements WizardPinSetupPresenter.Screen {

    @InjectView(R.id.toolbar)
    Toolbar toolbar;

    @InjectView(R.id.profile_proto)
    SimpleDraweeView profilePhotoView;

    public WizardPinSetupScreen(Context context) {
        super(context);
    }

    public WizardPinSetupScreen(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        toolbar.setNavigationOnClickListener(v -> onBackClick());
    }

    private void onBackClick() {
        presenter.goToBack();
    }

    @NonNull
    @Override
    public WizardPinSetupPresenter createPresenter() {
        return new WizardPinSetupPresenter(getContext(), getInjector(), getPath().getSmartCardId());
    }

    @OnClick(R.id.button_next)
    public void nextClick() {
        presenter.setupPIN();
    }

    @Override
    public void setUserAvatar(@Nullable String fileUri) {
        if (TextUtils.isEmpty(fileUri)) return;
        profilePhotoView.setImageURI(Uri.parse(fileUri));
    }

    @Override
    public OperationScreen provideOperationDelegate() {
        return new DialogOperationScreen(this);
    }
}
