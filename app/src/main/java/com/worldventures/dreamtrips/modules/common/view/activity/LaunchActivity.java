package com.worldventures.dreamtrips.modules.common.view.activity;

import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;

import com.afollestad.materialdialogs.MaterialDialog;
import com.techery.spares.annotations.Layout;
import com.techery.spares.utils.ui.SoftInputUtil;
import com.worldventures.dreamtrips.BuildConfig;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.api.error.ErrorResponse;
import com.worldventures.dreamtrips.core.navigation.ActivityRouter;
import com.worldventures.dreamtrips.modules.common.presenter.LaunchActivityPresenter;
import com.worldventures.dreamtrips.modules.common.view.custom.DTEditText;

import javax.inject.Inject;

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
    @InjectView(R.id.splash_progress) View splashProgress;

    @Inject ActivityRouter activityRouter;

    private Snackbar snackbar;

    @Override
    protected LaunchActivityPresenter createPresentationModel(Bundle savedInstanceState) {
        return new LaunchActivityPresenter();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        detectMode();
        if (savedInstanceState == null) getPresentationModel().initDtl();
    }

    private void detectMode() {
        Bundle bundle = getIntent().getExtras();
        String type;
        if (bundle != null && !TextUtils.isEmpty(type = bundle.getString(EXTRA_TYPE))) {
            switch (type) {
                case ActivityRouter.LAUNCH_LOGIN:
                    loginMode();
                    break;
                case ActivityRouter.LAUNCH_SPLASH:
                    splashMode();
                    break;
            }
        } else {
            loginMode();
        }
    }

    private void loginMode() {
        splashModeHolder.setVisibility(View.GONE);
        loginModeHolder.setVisibility(View.VISIBLE);
        getPresentationModel().splashModeEnd();
    }

    private void splashMode() {
        splashModeHolder.setVisibility(View.VISIBLE);
        loginModeHolder.setVisibility(View.GONE);
        getPresentationModel().splashModeStart();
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
    public void configurationFailed() {
        splashProgress.setVisibility(View.GONE);
        snackbar = Snackbar.make(findViewById(R.id.rootView),
                R.string.configuration_failed,
                Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.configuration_acitve_button, v -> getPresentationModel().startPreloadChain());
        snackbar.show();
    }

    @Override
    public void configurationStarted() {
        splashProgress.setVisibility(View.VISIBLE);
        if (snackbar != null) {
            snackbar.dismiss();
        }
    }

    @Override
    public void openLogin() {
        loginMode();
    }

    @Override
    public void openSplash() {
        splashMode();
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
