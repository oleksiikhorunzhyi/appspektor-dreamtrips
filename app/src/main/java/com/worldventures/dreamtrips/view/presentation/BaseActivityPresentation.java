package com.worldventures.dreamtrips.view.presentation;

import com.worldventures.dreamtrips.view.activity.Injector;

public class BaseActivityPresentation extends BasePresentation {
    public BaseActivityPresentation(Injector objectGraph) {
        super(objectGraph);
    }


    public void pop() {
        fragmentCompass.pop();
    }
}
