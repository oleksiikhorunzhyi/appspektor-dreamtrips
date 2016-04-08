package com.messenger.ui.adapter;

import android.content.Context;

import com.messenger.ui.adapter.cell.SwipeableUserCell;
import com.messenger.ui.adapter.swipe.SwipeLayoutContainer;
import com.techery.spares.adapter.BaseDelegateAdapter;
import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.R;

public class SwipeableContactsAdapter<T>
        extends BaseDelegateAdapter<T> implements SwipeLayoutContainer {

    public SwipeableContactsAdapter(Context context, Injector injector) {
        super(context, injector);
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        if (getItemViewType(position) == viewTypes.indexOf(SwipeableUserCell.class)) {
            return R.id.swipe;
        }
        return 0;
    }
}
