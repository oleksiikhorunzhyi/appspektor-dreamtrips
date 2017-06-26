package com.worldventures.dreamtrips.wallet.service.picker;


import android.app.Activity;
import android.content.Intent;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import java.util.Collection;

import rx.Notification;
import rx.Observable;
import rx.subjects.PublishSubject;

public class WalletPickerFacebookServiceImpl implements WalletPickerFacebookService {

   private final Activity activity;
   private final LoginManager loginManager;
   private final CallbackManager callbackManager;
   private final PublishSubject<Notification<LoginResult>> resultPublishSubject = PublishSubject.create();

   public WalletPickerFacebookServiceImpl(Activity activity) {
      this.activity = activity;
      this.loginManager = LoginManager.getInstance();
      this.callbackManager = CallbackManager.Factory.create();
   }

   private void loginToFacebook(Collection<String> permissions) {
      loginManager.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
         @Override
         public void onSuccess(final LoginResult loginResult) {
            AccessToken.setCurrentAccessToken(loginResult.getAccessToken());
            resultPublishSubject.onNext(Notification.createOnNext(loginResult));
         }

         @Override
         public void onCancel() {
            resultPublishSubject.onNext(Notification.createOnCompleted());
         }

         @Override
         public void onError(final FacebookException exception) {
            resultPublishSubject.onNext(Notification.createOnError(exception));
         }
      });
      loginManager.logInWithReadPermissions(activity, permissions);
   }

   @Override
   public Observable<Notification<LoginResult>> checkFacebookLogin(Collection<String> permissions) {
      loginToFacebook(permissions);
      return resultPublishSubject.asObservable();
   }

   @Override
   public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
      return callbackManager.onActivityResult(requestCode, resultCode, data);
   }
}
