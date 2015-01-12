package com.worldventures.dreamtrips.view.presentation;

import com.worldventures.dreamtrips.BuildConfig;
import com.worldventures.dreamtrips.core.SessionManager;
import com.worldventures.dreamtrips.core.model.User;
import com.worldventures.dreamtrips.utils.ValidationUtils;
import com.worldventures.dreamtrips.view.activity.Injector;

import org.robobinding.annotation.PresentationModel;
import org.robobinding.presentationmodel.HasPresentationModelChangeSupport;
import org.robobinding.presentationmodel.PresentationModelChangeSupport;

import javax.inject.Inject;

@PresentationModel
public class LoginFragmentPresentation extends BasePresentation implements HasPresentationModelChangeSupport {
    private final PresentationModelChangeSupport changeSupport;
    private final View view;

    @Inject
    protected SessionManager sessionManager;

    private String username;
    private String userPassword;

    public LoginFragmentPresentation(View view, Injector injector) {
        super(view, injector);
        this.view = view;
        this.changeSupport = new PresentationModelChangeSupport(this);
    }

    public void loginAction() {


        String username = getUsername();
        String userPassword = getUserPassword();

        ValidationUtils.VResult usernameValid = ValidationUtils.isUsernameValid(username);
        ValidationUtils.VResult passwordValid = ValidationUtils.isPasswordValid(userPassword);
        if (!usernameValid.isValid() || !passwordValid.isValid()) {
            view.showLocalErrors(usernameValid.getMessage(), passwordValid.getMessage());
            return;
        }
        this.view.showProgressDialog();
        dataManager.getSession(username, userPassword, (o, e) -> {
            if (o != null) {
                String sessionToken = o.getToken();
                User sessionUser = o.getUser();
                if (sessionUser == null || sessionToken == null) {
                    this.view.showLoginErrorMessage();
                    return;
                }
                sessionManager.createUserLoginSession(sessionToken);
                sessionManager.saveCurrentUser(sessionUser);
                dataManager.getToken(username, userPassword, (oi, ei) -> {
                    if (oi != null) {
                        String token = oi.get("result").getAsString();

                        sessionManager.createDreamToken(token);
                        activityRouter.openMain();
                        activityRouter.finish();
                        this.view.showLoginSuccess();
                    } else {
                        this.view.showLoginErrorMessage();
                    }
                });
            } else {
                this.view.showLoginErrorMessage();
            }
        });
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    @Override
    public PresentationModelChangeSupport getPresentationModelChangeSupport() {
        return changeSupport;
    }

    public void fillDataAction() {
        if (BuildConfig.DEBUG) {
            setUsername("888888");
            setUserPassword("travel1ns1de");
            changeSupport.firePropertyChange("username");
            changeSupport.firePropertyChange("userPassword");
        }
    }

    public static interface View extends IInformView {
        void showProgressDialog();

        void showLoginSuccess();

        void showLoginErrorMessage();

        public void showLocalErrors(String userNameError, String passwordError);
    }
}
