package com.messenger.ui.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.daimajia.swipe.SwipeLayout;
import com.messenger.entities.DataUser;
import com.messenger.ui.adapter.holder.BaseViewHolder;
import com.messenger.ui.adapter.holder.ContactViewHolder;
import com.messenger.ui.adapter.swipe.SwipeLayoutContainer;
import com.messenger.ui.adapter.swipe.SwipeableAdapterManager;
import com.messenger.util.SwipeClickListener;
import com.worldventures.dreamtrips.R;

import butterknife.InjectView;

public class ActionButtonsContactsCursorAdapter
        extends ContactCursorAdapter
        implements SwipeLayoutContainer {

    public final String userId;
    private final boolean owner;

    private SwipeableAdapterManager swipeableAdapterManager;

    private DeleteRequestListener deleteRequestListener;
    private UserClickListener userClickListener;

    public ActionButtonsContactsCursorAdapter(Context context, DataUser user, boolean owner,
                                              SwipeableAdapterManager swipeableAdapterManager) {
        super(context, null);
        this.userId = user.getId();
        this.owner = owner;
        this.swipeableAdapterManager = swipeableAdapterManager;
        setAdminSectionEnabled(true);
    }

    @Override
    public BaseViewHolder createContactViewHolder(ViewGroup parent, int viewType) {
        View itemRow = LayoutInflater
                .from(parent.getContext()).inflate(R.layout.list_item_contact_swipe, parent, false);
        return new ActionButtonsViewHolder(itemRow);
    }

    protected void onBindUserHolder(ContactViewHolder h, Cursor cursor, final DataUser user) {
        super.onBindUserHolder(h, cursor, user);
        ActionButtonsViewHolder holder = (ActionButtonsViewHolder)h;

        holder.swipeLayout.setSwipeEnabled(owner && !user.equals(admin));

        holder.getTickImageView().setVisibility(View.GONE);
        View.OnClickListener clickListener = view -> {
            if (userClickListener != null) {
                userClickListener.onUserClicked(user);
            }
        };
        holder.swipeLayout.addSwipeListener(new SwipeClickListener(holder.itemView, clickListener));
        holder.itemView.setOnClickListener(clickListener);

        holder.deleteButton.setOnClickListener(view -> {
            if (deleteRequestListener != null) {
                deleteRequestListener.onDeleteUserRequired(user);
            }
        });
    }

    public void setDeleteRequestListener(DeleteRequestListener deleteRequestListener) {
        this.deleteRequestListener = deleteRequestListener;
    }

    public void setUserClickListener(UserClickListener userClickListener) {
        this.userClickListener = userClickListener;
    }

    @Override
    public void changeCursor(Cursor cursor, String filter, String column) {
        if (swipeableAdapterManager != null) swipeableAdapterManager.closeAllItems(); // cause sometime there is magic NullPointerException
        super.changeCursor(cursor, filter, column);
    }

    @Override
    public void changeCursor(Cursor cursor) {
        if (swipeableAdapterManager != null) swipeableAdapterManager.closeAllItems(); // cause sometime there is magic NullPointerException
        super.changeCursor(cursor);
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        if (getItemViewType(position) == VIEW_TYPE_CONTACT) {
            return R.id.swipe;
        }
        return 0;
    }

    public interface DeleteRequestListener {
        void onDeleteUserRequired(DataUser user);
    }

    public interface UserClickListener {
        void onUserClicked(DataUser user);
    }

    public static class ActionButtonsViewHolder extends ContactViewHolder {
        @InjectView(R.id.swipe)
        SwipeLayout swipeLayout;
        @InjectView(R.id.swipe_layout_button_delete)
        View deleteButton;

        public ActionButtonsViewHolder(View itemView) {
            super(itemView);
        }
    }
}
