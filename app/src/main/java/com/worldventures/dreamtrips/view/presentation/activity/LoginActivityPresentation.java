package com.worldventures.dreamtrips.view.presentation.activity;

import com.worldventures.dreamtrips.core.DataManager;

import org.robobinding.annotation.PresentationModel;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

@PresentationModel
public class LoginActivityPresentation {
    private String username;
    private String userPassword;
    private View view;
    private DataManager dataManager;

    public LoginActivityPresentation(View view, DataManager dataManager) {
        this.view = view;
        this.dataManager = dataManager;
    }

    public void loginAction() {
        dataManager.login(getUsername(), getUserPassword(), new Callback<Integer>() {
            @Override
            public void success(Integer integer, Response response) {
                view.openMainWindow();
            }

            @Override
            public void failure(RetrofitError error) {

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

    public static interface View {
        void openMainWindow();
    }
}
