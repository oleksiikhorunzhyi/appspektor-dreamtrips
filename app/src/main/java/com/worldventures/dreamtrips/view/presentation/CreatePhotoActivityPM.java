package com.worldventures.dreamtrips.view.presentation;

import com.worldventures.dreamtrips.core.navigation.State;
import com.worldventures.dreamtrips.view.activity.Injector;

public class CreatePhotoActivityPM extends BasePresentation {
    public CreatePhotoActivityPM(IInformView view, Injector objectGraph) {
        super(view, objectGraph);
    }

    public void onCreate() {
        fragmentCompass.add(State.CREATE_PHOTO);
    }
}
