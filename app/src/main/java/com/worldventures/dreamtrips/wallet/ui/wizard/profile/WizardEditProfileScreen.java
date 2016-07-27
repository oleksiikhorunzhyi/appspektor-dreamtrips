package com.worldventures.dreamtrips.wallet.ui.wizard.profile;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;
import android.view.inputmethod.EditorInfo;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.wallet.ui.common.base.BaseToolbarScreenLayout;

import butterknife.OnClick;
import butterknife.OnEditorAction;

public class WizardEditProfileScreen extends BaseToolbarScreenLayout<WizardEditProfilePresenter.Screen, WizardEditProfilePresenter, WizardEditProfilePath> implements WizardEditProfilePresenter.Screen {

    public WizardEditProfileScreen(Context context) {
        super(context);
    }

    public WizardEditProfileScreen(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @NonNull
    @Override
    public WizardEditProfilePresenter createPresenter() {
        return new WizardEditProfilePresenter(getContext(), getInjector());
    }

    @Override
    protected void onNavigateButtonClick(View view) {
        presenter.goToBack();
    }

    @OnClick(R.id.next_button)
    public void nextClick() {
        presenter.doOnNext();
    }

    @OnEditorAction(R.id.person_name)
    public boolean actionNext(int action) {
        if (action != EditorInfo.IME_ACTION_NEXT) return false;
        presenter.doOnNext();
        return true;
    }
}
