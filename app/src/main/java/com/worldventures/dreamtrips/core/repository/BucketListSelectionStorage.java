package com.worldventures.dreamtrips.core.repository;

import com.techery.spares.storage.complex_objects.ComplexObjectStorage;
import com.techery.spares.storage.preferences.SimpleKeyValueStorage;

import java.util.ArrayList;
import java.util.List;

public class BucketListSelectionStorage extends ComplexObjectStorage<BucketListSelectionStorage.BucketListSelection> {

    public static class BucketListSelection {
        public boolean isFilterEnabled;
        public List<String> favoriteTrips = new ArrayList<>();
    }

    public BucketListSelectionStorage(SimpleKeyValueStorage storage, String userId) {
        super(storage, "bucket-selection-" + userId, BucketListSelection.class);

        if (!get().isPresent()) {
            put(new BucketListSelection());
        }
    }

    public BucketListSelection getSelection() {
        return get().get();
    }

    public void save() {
        put(get().get());
    }
}
