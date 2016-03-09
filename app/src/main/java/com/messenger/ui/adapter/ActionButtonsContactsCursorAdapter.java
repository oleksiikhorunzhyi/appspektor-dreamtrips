package com.messenger.ui.adapter;

import android.content.Context;
import android.view.ViewGroup;

import com.messenger.entities.DataUser;
import com.messenger.ui.adapter.cell.SwipeableUserCell;
import com.messenger.ui.adapter.swipe.SwipeLayoutContainer;
import com.techery.spares.adapter.BaseDelegateAdapter;
import com.techery.spares.module.Injector;
import com.techery.spares.ui.view.cell.AbstractCell;
import com.worldventures.dreamtrips.R;

import java.util.List;

public class ActionButtonsContactsCursorAdapter
        extends BaseDelegateAdapter<DataUser> implements SwipeLayoutContainer {

    private final boolean owner;
    private DataUser admin;

    public ActionButtonsContactsCursorAdapter(Context context, Injector injector, boolean owner) {
        super(context, injector);
        this.owner = owner;
    }

    public void setItems(DataUser admin, List items) {
        this.admin = admin;
        super.setItems(items);
    }

    @Override
    public AbstractCell onCreateViewHolder(ViewGroup parent, int viewType) {
        AbstractCell cell = super.onCreateViewHolder(parent, viewType);
        if (cell instanceof SwipeableUserCell) {
            SwipeableUserCell swipeableUserCell = (SwipeableUserCell) cell;
            swipeableUserCell.setOwner(owner);
        }
        return cell;
    }

    @Override
    public void onBindViewHolder(AbstractCell cell, int position) {
        super.onBindViewHolder(cell, position);
        if (cell instanceof SwipeableUserCell) {
            SwipeableUserCell userCell = (SwipeableUserCell) cell;
            userCell.setAdmin(admin);
        }
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        if (getItemViewType(position) == viewTypes.indexOf(SwipeableUserCell.class)) {
            return R.id.swipe;
        }
        return 0;
    }
}
