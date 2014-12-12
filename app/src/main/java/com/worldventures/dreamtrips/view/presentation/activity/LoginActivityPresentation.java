package com.worldventures.dreamtrips.view.presentation.activity;

import android.util.Log;

import com.worldventures.dreamtrips.BuildConfig;
import com.worldventures.dreamtrips.core.DataManager;

import org.robobinding.annotation.PresentationModel;
import org.robobinding.presentationmodel.HasPresentationModelChangeSupport;
import org.robobinding.presentationmodel.PresentationModelChangeSupport;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

@PresentationModel
public class LoginActivityPresentation implements HasPresentationModelChangeSupport {
    private final PresentationModelChangeSupport changeSupport;
    private String username;
    private String userPassword;
    private View view;
    private DataManager dataManager;

    public LoginActivityPresentation(View view, DataManager dataManager) {
        this.view = view;
        this.dataManager = dataManager;
        this.changeSupport = new PresentationModelChangeSupport(this);

    }

    public void fillDataAction() {
        if (BuildConfig.DEBUG) {
            setUsername("888888");
            setUserPassword("travel1ns1de");
            changeSupport.firePropertyChange("username");
            changeSupport.firePropertyChange("userPassword");
        }
    }

    public void loginAction() {
        dataManager.login(getUsername(), getUserPassword(), new Callback<Object>() {
            @Override
            public void success(Object o, Response response) {
                //view.openMainWindow();
                Log.d(LoginActivityPresentation.class.getSimpleName(), o.toString());
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e(LoginActivityPresentation.class.getSimpleName(), error.toString(), error);

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

    public static interface View {
        void openMainWindow();
    }
}
