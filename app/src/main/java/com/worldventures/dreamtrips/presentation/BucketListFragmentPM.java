package com.worldventures.dreamtrips.presentation;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.techery.spares.loader.CollectionController;
import com.techery.spares.loader.LoaderFactory;
import com.techery.spares.storage.preferences.SimpleKeyValueStorage;
import com.worldventures.dreamtrips.core.model.BucketItem;
import com.worldventures.dreamtrips.core.model.response.BucketListResponse;
import com.worldventures.dreamtrips.view.cell.BucketItemCell;
import com.worldventures.dreamtrips.view.fragment.BucketTabsFragment;

import org.robobinding.annotation.PresentationModel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

@PresentationModel
public class BucketListFragmentPM extends BasePresentation {
    private CollectionController<Object> adapterController;
    private BucketTabsFragment.Type type;
    private boolean filterEnabled;

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
    SimpleKeyValueStorage storage;

    @Override
    public void init() {
        super.init();
        this.adapterController = loaderFactory.create(type.ordinal(), (context, params) -> {
            ArrayList<Object> result = new ArrayList<>();
            String stringFromAsset = getStringFromAsset(type.getFileName());
            gson.fromJson(stringFromAsset, BucketItem.class);
            BucketListResponse response = gson.fromJson(stringFromAsset, BucketListResponse.class);
            List<BucketItem> data = response.getData();
            if (filterEnabled) {
                List<BucketItem> filteredData = new ArrayList<BucketItem>();
                for (BucketItem bucketItem : data) {
                    if (storage.get(BucketItemCell.PREFIX + bucketItem.getId()) != null) {
                        filteredData.add(bucketItem);
                    }
                }
                result.addAll(filteredData);
            } else {
                result.addAll(data);
            }
            return result;
        });
    }

    private String getStringFromAsset(String fileName) {
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
        return "";
    }

    public CollectionController<Object> getAdapterController() {
        return adapterController;
    }

    public void filterEnabled(boolean onlyFavorites) {
        this.filterEnabled = onlyFavorites;
    }
}
