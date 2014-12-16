package com.worldventures.dreamtrips.view.presentation;

import com.worldventures.dreamtrips.BuildConfig;
import com.worldventures.dreamtrips.core.DataManager;
import com.worldventures.dreamtrips.utils.Logs;
import com.worldventures.dreamtrips.view.activity.Injector;

import org.robobinding.annotation.PresentationModel;
import org.robobinding.presentationmodel.HasPresentationModelChangeSupport;
import org.robobinding.presentationmodel.PresentationModelChangeSupport;

@PresentationModel
public class LoginFragmentPresentation extends BasePresentation implements HasPresentationModelChangeSupport {
    public static final int REQUEST_COUNT = 2;
    private final PresentationModelChangeSupport changeSupport;
    private String username;
    private String userPassword;
    private View view;

    public LoginFragmentPresentation(View view, Injector graf) {
        super(graf);
        this.view = view;
        this.changeSupport = new PresentationModelChangeSupport(this);

    }

    public void loginAction() {
        dataManager.login(getUsername(), getUserPassword(), new DataManager.Result<Object>() {
            int successCount;

            @Override
            public void response(Object o, Exception e) {
                if (o != null) {
                    successCount++;
                    Logs.i(LoginFragmentPresentation.class.getSimpleName(), o.toString());
                } else if (e != null) {
                    view.informUser(e.getMessage());
                }
                if (successCount == REQUEST_COUNT) {
                    activityCompass.openMain();
                }
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
    }
}
