package com.worldventures.dreamtrips.modules.auth.view;


import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.auth.presenter.LoginFragmentPresenter;
import com.worldventures.dreamtrips.modules.common.view.custom.DTEditText;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragment;

import butterknife.InjectView;
import butterknife.OnClick;

import static com.worldventures.dreamtrips.core.utils.ViewUtils.getMinSideSize;

@Layout(R.layout.fragment_login)
public class LoginFragment extends BaseFragment<LoginFragmentPresenter> implements LoginFragmentPresenter.View {

    @InjectView(R.id.btn_login)
    Button loginButton;
    @InjectView(R.id.et_username)
    DTEditText etUsername;
    @InjectView(R.id.et_password)
    DTEditText etPassword;
    @InjectView(R.id.iv_bg)
    ImageView ivBg;
    @InjectView(R.id.vg_content_container)
    ViewGroup vgContentContainer;

    public LoginFragment() {

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        layoutConfiguration();
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
        //Handler for better visual effect
        new Handler().postDelayed(() -> {
            loginButton.setVisibility(View.VISIBLE);
            loginButton.setClickable(true);
        }, 50);
    }

    public void showLocalErrors(int userNameError, int passwordError) {
        if (userNameError != 0) {
            etUsername.setError(getString(userNameError));
        }
        if (passwordError != 0) {
            etPassword.setError(getString(passwordError));
        }
    }

    @Override
    public String getUsername() {
        return etUsername.getText().toString();
    }

    @Override
    public void setUsername(String name) {
        etUsername.setText(name);
    }

    @Override
    public String getUserPassword() {
        return etPassword.getText().toString();
    }

    @Override
    public void setUserPassword(String pass) {
        etPassword.setText(pass);
    }

    @Override
    protected LoginFragmentPresenter createPresenter(Bundle savedInstanceState) {
        return new LoginFragmentPresenter(this);
    }

    private void layoutConfiguration() {
        int minSideSize = getMinSideSize(getActivity());
        vgContentContainer.getLayoutParams().width = minSideSize;
    }

    @OnClick(R.id.iv_title)
    public void onTitleClick() {
        getPresenter().fillDataAction();
    }

    @OnClick(R.id.btn_login)
    public void onLoginClick() {
        getPresenter().loginAction();
    }
}
