package com.worldventures.dreamtrips.presentation;

import com.worldventures.dreamtrips.core.navigation.State;

import org.robobinding.annotation.PresentationModel;

@PresentationModel
public class LoginActivityPresentation extends BasePresentation<BasePresentation.View> {

    public LoginActivityPresentation(View view) {
        super(view);
    }

    public void onCreate() {
        fragmentCompass.add(State.LOGIN);
    }
}
