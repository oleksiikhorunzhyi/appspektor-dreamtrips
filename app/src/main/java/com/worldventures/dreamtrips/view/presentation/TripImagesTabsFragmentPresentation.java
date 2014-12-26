package com.worldventures.dreamtrips.view.presentation;

import com.worldventures.dreamtrips.view.activity.Injector;

import org.robobinding.annotation.PresentationModel;
import org.robobinding.presentationmodel.HasPresentationModelChangeSupport;
import org.robobinding.presentationmodel.PresentationModelChangeSupport;

@PresentationModel
public class TripImagesTabsFragmentPresentation extends BasePresentation implements HasPresentationModelChangeSupport {
    private final PresentationModelChangeSupport changeSupport;
    private final View view;

    public TripImagesTabsFragmentPresentation(View view, Injector injector) {
        super(view, injector);
        this.view = view;
        this.changeSupport = new PresentationModelChangeSupport(this);
    }

    @Override
    public PresentationModelChangeSupport getPresentationModelChangeSupport() {
        return changeSupport;
    }

    public static interface View extends IInformView {
    }
}
