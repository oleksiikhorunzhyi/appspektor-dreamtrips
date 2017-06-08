package com.worldventures.dreamtrips.wallet.service.picker;


import android.content.Intent;

import com.facebook.login.LoginResult;

import java.util.Collection;

import rx.Notification;
import rx.Observable;

public interface WalletPickerFacebookService {

   Observable<Notification<LoginResult>> checkFacebookLogin(Collection<String> permissions);

   boolean onActivityResult(int requestCode, int resultCode, Intent data);

}
