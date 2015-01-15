package com.worldventures.dreamtrips.view.presentation;


import com.techery.spares.module.Injector;

public class BaseActivityPresentation extends BasePresentation {

    public BaseActivityPresentation(IInformView view, Injector injector) {
        super(view, injector);
    }

    public void pop() {
        fragmentCompass.pop();
    }
}
