package com.worldventures.dreamtrips.view.presentation;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.core.navigation.State;

public class CreatePhotoActivityPM extends BasePresentation {
    public CreatePhotoActivityPM(IInformView view, Injector objectGraph) {
        super(view, objectGraph);
    }

    public void onCreate() {
        fragmentCompass.add(State.CREATE_PHOTO);
    }
}
