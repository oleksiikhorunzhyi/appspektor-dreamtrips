package com.worldventures.dreamtrips.view.fragment;


import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.utils.ViewIUtils;
import com.worldventures.dreamtrips.view.activity.BaseActivity;
import com.worldventures.dreamtrips.view.custom.DTEditText;
import com.worldventures.dreamtrips.presentation.LoginFragmentPresentation;

import org.robobinding.ViewBinder;

import butterknife.ButterKnife;
import butterknife.InjectView;

@Layout(R.layout.fragment_login)
public class LoginFragment extends BaseFragment<LoginFragmentPresentation> implements LoginFragmentPresentation.View {

    @InjectView(R.id.btn_login)
    Button btnLogin;
    @InjectView(R.id.et_username)
    DTEditText etUsername;
    @InjectView(R.id.et_password)
    DTEditText etPassword;
    @InjectView(R.id.iv_bg)
    ImageView ivBg;

    public LoginFragment() {

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        int screenHeight = ViewIUtils.getScreenHeight(getActivity());
        int statusBarHeight = ViewIUtils.getStatusBarHeight(getActivity());
        ivBg.getLayoutParams().height= screenHeight- statusBarHeight;
    }

    @Override
    public void showProgressDialog() {
        btnLogin.setVisibility(View.GONE);
        btnLogin.setClickable(false);
    }

    @Override
    public void showLoginSuccess() {
        dismissProgressDialog();
    }

    @Override
    public void showLoginErrorMessage() {
        dismissProgressDialog();
        informUser("Invalid username or password");
    }

    private void dismissProgressDialog() {
        //Handler for better visual effect
        new Handler().postDelayed(() -> {
            btnLogin.setVisibility(View.VISIBLE);
            btnLogin.setClickable(true);
        }, 50);
    }

    public void showLocalErrors(String userNameError, String passwordError) {
        etUsername.setError(userNameError);
        etPassword.setError(passwordError);
    }

    @Override
    protected LoginFragmentPresentation createPresentationModel(Bundle savedInstanceState) {
        return new LoginFragmentPresentation(this);
    }
}
