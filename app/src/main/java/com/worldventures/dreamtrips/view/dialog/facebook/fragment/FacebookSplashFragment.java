/**
 * Copyright 2010-present Facebook.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.worldventures.dreamtrips.view.dialog.facebook.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.Session;
import com.facebook.widget.LoginButton;
import com.worldventures.dreamtrips.R;

public class FacebookSplashFragment extends BaseFacebookDialogFragment {

    private Session.StatusCallback callback = (session, state, exception) -> {
        if (session != null && session.isOpened()) {
            FacebookAlbumFragment facebookAlbumFragment = new FacebookAlbumFragment();
            facebookAlbumFragment.show(getFragmentManager(),injector,imagePickCallback);
            dismissAllowingStateLoss();
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_facebook_splash, container, false);

        LoginButton loginButton = (LoginButton) view.findViewById(R.id.login_button);
        loginButton.setReadPermissions("user_photos");
        loginButton.setSessionStatusCallback(callback);
        return view;
    }

}

