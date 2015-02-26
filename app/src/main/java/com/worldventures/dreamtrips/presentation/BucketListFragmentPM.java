package com.worldventures.dreamtrips.presentation;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.techery.spares.loader.CollectionController;
import com.techery.spares.loader.LoaderFactory;
import com.techery.spares.storage.preferences.SimpleKeyValueStorage;
import com.worldventures.dreamtrips.core.model.BucketItem;
import com.worldventures.dreamtrips.core.model.response.BucketListResponse;
import com.worldventures.dreamtrips.core.repository.BucketListSelectionStorage;
import com.worldventures.dreamtrips.view.fragment.BucketTabsFragment;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class BucketListFragmentPM extends BasePresentation {
    private CollectionController<Object> adapterController;
    private BucketTabsFragment.Type type;

    public BucketListFragmentPM(View view, BucketTabsFragment.Type type) {
        super(view);
        this.type = type;
    }

    @Inject
    LoaderFactory loaderFactory;

    @Inject
    Context context;

    @Inject
    Gson gson;

    @Inject
    BucketListSelectionStorage bucketListSelectionStorage;

    List<BucketItem> cachedBucketListItems;

    @Override
    public void init() {
        super.init();
        this.adapterController = loaderFactory.create(type.ordinal(), (context, params) -> {
            ArrayList<Object> result = new ArrayList<>();

           /* if (cachedBucketListItems == null) {
                String stringFromAsset = getStringFromAsset(type.getFileName());

                BucketListResponse response = gson.fromJson(stringFromAsset, BucketListResponse.class);

                cachedBucketListItems = response.getData();
            }

            final BucketListSelectionStorage.BucketListSelection selection = bucketListSelectionStorage.getSelection();
            if (selection.isFilterEnabled) {
                List<BucketItem> filteredData = new ArrayList<BucketItem>();

                final List<String> favoriteTrips = selection.favoriteTrips;

                for (BucketItem bucketItem : cachedBucketListItems) {

                    if (favoriteTrips.contains(bucketItem.getSPName())) {
                        filteredData.add(bucketItem);
                    }
                }
                result.addAll(filteredData);
            } else {
                result.addAll(cachedBucketListItems);
            }*/

            return result;
        });
    }

    private String getStringFromAsset(String fileName) {
/*
        try {
            String str;
            StringBuilder buf = new StringBuilder();
            InputStream json = context.getAssets().open(fileName);
            BufferedReader in = new BufferedReader(new InputStreamReader(json, "UTF-8"));
            while ((str = in.readLine()) != null) {
                buf.append(str);
            }
            in.close();
            return buf.toString();
        } catch (IOException e) {
            Log.e(this.getClass().getSimpleName(), "", e);
        }
*/
        return "";
    }

    public CollectionController<Object> getAdapterController() {
        return adapterController;
    }
}
