package com.worldventures.dreamtrips.modules.bucketlist.view.adapter;

import android.content.Context;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketItem;
import com.worldventures.dreamtrips.modules.common.view.adapter.DraggableArrayListAdapter;

import javax.inject.Provider;

public class BucketItemAdapter extends DraggableArrayListAdapter<BucketItem> {

    public BucketItemAdapter(Context context, Provider<Injector> injector) {
        super(context, injector);
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).getUid().hashCode();
    }
}
