package com.worldventures.dreamtrips.presentation;

import com.techery.spares.storage.preferences.SimpleKeyValueStorage;

import org.robobinding.annotation.PresentationModel;

import javax.inject.Inject;

@PresentationModel
public class BucketTabsFragmentPM extends BasePresentation {
    public static final String BUCKET_FILTER_ENABLED = "BUCKET_FILTER_ENABLED";

    @Inject
    SimpleKeyValueStorage storage;


    public BucketTabsFragmentPM(View view) {
        super(view);
    }

    public void filterEnabled(boolean isEnabled) {
        storage.put(BUCKET_FILTER_ENABLED, String.valueOf(isEnabled));
    }

    public boolean isFilterEnabled() {
        return Boolean.valueOf(storage.get(BucketTabsFragmentPM.BUCKET_FILTER_ENABLED));
    }
}
