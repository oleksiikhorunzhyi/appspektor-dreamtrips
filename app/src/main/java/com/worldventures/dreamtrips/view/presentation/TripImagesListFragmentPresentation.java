package com.worldventures.dreamtrips.view.presentation;

import com.worldventures.dreamtrips.view.activity.Injector;

import org.robobinding.annotation.PresentationModel;

@PresentationModel
public class TripImagesListFragmentPresentation extends BasePresentation {
    private View view;

    public TripImagesListFragmentPresentation(View view, Injector injector) {
        super(view, injector);
        this.view = view;
    }

    public static interface View extends IInformView {

    }
}
