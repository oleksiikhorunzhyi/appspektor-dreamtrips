package com.worldventures.dreamtrips.presentation;

import com.techery.spares.storage.preferences.SimpleKeyValueStorage;
import com.worldventures.dreamtrips.core.repository.BucketListSelectionStorage;

import org.robobinding.annotation.PresentationModel;

import javax.inject.Inject;

@PresentationModel
public class BucketTabsFragmentPM extends BasePresentation {

    @Inject
    BucketListSelectionStorage bucketListSelectionStorage;

    public BucketTabsFragmentPM(View view) {
        super(view);
    }

    public void filterEnabled(boolean isEnabled) {
        bucketListSelectionStorage.getSelection().isFilterEnabled = isEnabled;
        bucketListSelectionStorage.save();
    }

    public boolean isFilterEnabled() {
        return bucketListSelectionStorage.getSelection().isFilterEnabled;
    }
}
