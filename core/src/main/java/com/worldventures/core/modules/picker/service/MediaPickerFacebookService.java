package com.worldventures.core.modules.picker.service;


import android.content.Intent;

import com.facebook.login.LoginResult;

import java.util.Collection;

import rx.Notification;
import rx.Observable;

public interface MediaPickerFacebookService {

   Observable<Notification<LoginResult>> checkFacebookLogin(Collection<String> permissions);

   boolean onActivityResult(int requestCode, int resultCode, Intent data);

}
