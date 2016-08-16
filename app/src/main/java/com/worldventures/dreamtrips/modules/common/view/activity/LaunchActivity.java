package com.worldventures.dreamtrips.modules.common.view.activity;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;

import com.afollestad.materialdialogs.MaterialDialog;
import com.techery.spares.annotations.Layout;
import com.techery.spares.utils.ui.SoftInputUtil;
import com.worldventures.dreamtrips.BuildConfig;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.api.error.ErrorResponse;
import com.worldventures.dreamtrips.modules.common.presenter.LaunchActivityPresenter;
import com.worldventures.dreamtrips.modules.common.view.custom.DTEditText;

import butterknife.InjectView;
import butterknife.OnClick;

@Layout(R.layout.activity_launch)
public class LaunchActivity extends ActivityWithPresenter<LaunchActivityPresenter> implements LaunchActivityPresenter.View {

    public static final String LOGIN = "login";
    public static final String SPLASH = "splash";
    public static final String EXTRA_TYPE = "type";

    @InjectView(R.id.splash_mode_holder) View splashModeHolder;
    @InjectView(R.id.et_username) DTEditText usernameEditText;
    @InjectView(R.id.et_password) DTEditText passwordEditText;
    @InjectView(R.id.btn_login) Button loginButton;
    @InjectView(R.id.login_progress) View loginProgress;

    @InjectView(R.id.login_mode_holder) View loginModeHolder;

    @Override
    protected LaunchActivityPresenter createPresentationModel(Bundle savedInstanceState) {
        return new LaunchActivityPresenter();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) getPresentationModel().initDtl();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        Bundle bundle = getIntent().getExtras();
        getPresentationModel().detectMode(bundle != null ? bundle.getString(EXTRA_TYPE) : null);
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
        SoftInputUtil.hideSoftInputMethod(this);
        getPresentationModel().loginAction();
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
    public void alert(String s) {
        runOnUiThread(() -> {
            MaterialDialog.Builder builder = new MaterialDialog.Builder(this);
            builder.title(R.string.alert)
                    .content(s)
                    .positiveText(R.string.OK)
                    .onPositive((dialog, which) -> finish())
                    .show();
        });
    }

    @Override
    public void openLogin() {
        splashModeHolder.setVisibility(View.GONE);
        loginModeHolder.setVisibility(View.VISIBLE);
    }

    @Override
    public void openSplash() {
        splashModeHolder.setVisibility(View.VISIBLE);
        loginModeHolder.setVisibility(View.GONE);
        dismissLoginProgress();
    }

    @Override
    public void openMain() {
        activityRouter.openMain();
        activityRouter.finish();
    }

    @Override
    public void showLoginProgress() {
        loginButton.setVisibility(View.GONE);
        loginButton.setClickable(false);
    }

    @Override
    public void alertLogin(String message) {
        super.alert(message);
        dismissLoginProgress();
    }

    private void dismissLoginProgress() {
        new Handler().postDelayed(() -> {
            if (loginButton != null) {
                loginButton.setVisibility(View.VISIBLE);
                loginButton.setClickable(true);
            }
        }, 50);
    }

    @Override
    public void showLocalErrors(int userNameError, int passwordError) {
        if (userNameError != 0) {
            usernameEditText.setError(getString(userNameError));
        }

        if (passwordError != 0) {
            passwordEditText.setError(getString(passwordError));
        }
    }

    @Override
    public boolean onApiError(ErrorResponse errorResponse) {
        return false;
    }

    @Override
    public void onApiCallFailed() {

    }
}
