package com.worldventures.dreamtrips.presentation;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.snappydb.DB;
import com.techery.spares.loader.CollectionController;
import com.techery.spares.loader.LoaderFactory;
import com.techery.spares.module.Annotations.Global;
import com.techery.spares.storage.preferences.SimpleKeyValueStorage;
import com.worldventures.dreamtrips.core.model.BucketItem;
import com.worldventures.dreamtrips.core.model.ContentItem;
import com.worldventures.dreamtrips.core.model.response.BucketListResponse;
import com.worldventures.dreamtrips.core.repository.BucketListSelectionStorage;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.utils.busevents.DeleteBucketItemEvent;
import com.worldventures.dreamtrips.utils.busevents.MarkBucketItemDoneEvent;
import com.worldventures.dreamtrips.view.fragment.BucketTabsFragment;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;

public class BucketListFragmentPM extends BasePresentation {
    private CollectionController<BucketItem> adapterController;
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
    SnappyRepository db;

    @Global
    @Inject
    EventBus eventBus;

    @Override
    public void init() {
        super.init();
        this.adapterController = loaderFactory.create(type.ordinal(), (context, params) -> {
            ArrayList<BucketItem> result = new ArrayList<>();

            try {
                result.addAll(db.readBucketList(type.name()));
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            return result;
        });
        eventBus.register(this);
    }


    public void deleteItem(int position) {
        db.saveBucketList(adapterController.getResult(), type.name());
    }

    public void itemMoved() {
        db.saveBucketList(adapterController.getResult(), type.name());
    }

    public void onEvent(MarkBucketItemDoneEvent markBucketItemDoneEvent) {
        db.saveBucketList(adapterController.getResult(), type.name());
        adapterController.reload();
    }

    public CollectionController<BucketItem> getAdapterController() {
        return adapterController;
    }


}