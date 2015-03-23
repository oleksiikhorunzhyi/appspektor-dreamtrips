package com.worldventures.dreamtrips.modules.auth.view;


import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.BuildConfig;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.auth.presenter.LoginPresenter;
import com.worldventures.dreamtrips.modules.common.view.custom.DTEditText;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragment;

import butterknife.InjectView;
import butterknife.OnClick;

import static com.worldventures.dreamtrips.core.utils.ViewUtils.getMinSideSize;

@Layout(R.layout.fragment_login)
public class LoginFragment extends BaseFragment<LoginPresenter> implements LoginPresenter.View {

    @InjectView(R.id.btn_login)
    Button loginButton;
    @InjectView(R.id.et_username)
    DTEditText usernameEditText;
    @InjectView(R.id.et_password)
    DTEditText passwordEditText;
    @InjectView(R.id.iv_bg)
    ImageView ivBg;
    @InjectView(R.id.vg_content_container)
    ViewGroup vgContentContainer;

    public LoginFragment() {

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        vgContentContainer.getLayoutParams().width = getMinSideSize(getActivity());
    }

    @Override
    public void showProgressDialog() {
        loginButton.setVisibility(View.GONE);
        loginButton.setClickable(false);
    }

    @Override
    public void showLoginSuccess() {
        dismissProgressDialog();
    }

    @Override
    public void showLoginErrorMessage() {
        dismissProgressDialog();
        informUser(getString(R.string.error_log_in));
    }

    private void dismissProgressDialog() {
        new Handler().postDelayed(() -> {
            loginButton.setVisibility(View.VISIBLE);
            loginButton.setClickable(true);
        }, 50);
    }

    public void showLocalErrors(int userNameError, int passwordError) {
        if (userNameError != 0) {
            usernameEditText.setError(getString(userNameError));
        }

        if (passwordError != 0) {
            passwordEditText.setError(getString(passwordError));
        }
    }

    @Override
    public String getUsername() {
        return usernameEditText.getText().toString();
    }

    @Override
    public String getUserPassword() {
        return passwordEditText.getText().toString();
    }

    @Override
    protected LoginPresenter createPresenter(Bundle savedInstanceState) {
        return new LoginPresenter(this);
    }

    @OnClick(R.id.iv_title)
    public void onTitleClick() {
        if (BuildConfig.DEBUG && !BuildConfig.FLAVOR.equals("prod")) {
            usernameEditText.setText("888888");
            passwordEditText.setText("travel1ns1de");
        }
    }

    @OnClick(R.id.btn_login)
    public void onLoginClick() {
        getPresenter().loginAction();
    }
}
