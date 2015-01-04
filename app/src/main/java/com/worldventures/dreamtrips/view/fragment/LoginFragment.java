package com.worldventures.dreamtrips.view.fragment;


import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.dd.CircularProgressButton;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.utils.ViewIUtils;
import com.worldventures.dreamtrips.view.activity.BaseActivity;
import com.worldventures.dreamtrips.view.custom.DTEditText;
import com.worldventures.dreamtrips.view.presentation.LoginFragmentPresentation;

import org.robobinding.ViewBinder;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class LoginFragment extends BaseFragment<BaseActivity> implements LoginFragmentPresentation.View {

    @InjectView(R.id.btn_login)
    CircularProgressButton btnLogin;
    @InjectView(R.id.et_username)
    DTEditText etUsername;
    @InjectView(R.id.et_password)
    DTEditText etPassword;
    @InjectView(R.id.iv_bg)
    ImageView ivBg;

    public LoginFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LoginFragmentPresentation presentationModel = new LoginFragmentPresentation(this, getAbsActivity());
        ViewBinder viewBinder = getAbsActivity().createViewBinder();
        return viewBinder.inflateAndBindWithoutAttachingToRoot(R.layout.fragment_login, presentationModel, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        ButterKnife.inject(this, view);
        int screenHeight = ViewIUtils.getScreenHeight(getAbsActivity());
        int statusBarHeight = ViewIUtils.getStatusBarHeight(getAbsActivity());
        ivBg.getLayoutParams().height= screenHeight- statusBarHeight;
    }

    @Override
    public void showProgressDialog() {
        btnLogin.setIndeterminateProgressMode(true);
        btnLogin.setProgress(50);
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
            btnLogin.setProgress(0);
            btnLogin.setClickable(true);
        }, 50);
    }


    public void showLocalErrors(String userNameError, String passwordError) {
        etUsername.setError(userNameError);
        etPassword.setError(passwordError);
    }
}
