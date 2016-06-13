package com.messenger.ui.adapter.cell;

import android.view.View;

import com.daimajia.swipe.SwipeLayout;
import com.messenger.entities.DataUser;
import com.messenger.ui.model.SwipeDataUser;
import com.messenger.util.SwipeClickListener;
import com.techery.spares.annotations.Layout;
import com.techery.spares.ui.view.cell.CellDelegate;
import com.worldventures.dreamtrips.R;

import butterknife.InjectView;

@Layout(R.layout.list_item_contact_swipe)
public class SwipeableUserCell extends UserCell<SwipeDataUser, SwipeableUserCell.Delegate> {
    @InjectView(R.id.swipe)
    SwipeLayout swipeLayout;
    @InjectView(R.id.swipe_layout_button_delete)
    View deleteButton;

    public SwipeableUserCell(View view) {
        super(view);
    }

    @Override
    protected DataUser getDataUser() {
        return getModelObject().user;
    }

    @Override
    protected void syncUIStateWithModel() {
        super.syncUIStateWithModel();
        SwipeDataUser swipeDataUser = getModelObject();
        swipeLayout.setSwipeEnabled(swipeDataUser.swipeAvailable);
        tickImageView.setVisibility(View.GONE);
        View.OnClickListener clickListener = view -> {
            if (cellDelegate != null) cellDelegate.onCellClicked(getModelObject());
        };
        swipeLayout.addSwipeListener(new SwipeClickListener(itemView, clickListener));
        itemView.setOnClickListener(clickListener);
        deleteButton.setOnClickListener(view -> {
            if (cellDelegate != null) cellDelegate.onDeleteUserRequired(getModelObject());
        });
    }

    @Override
    protected void setUserOnline(DataUser user) {
        if (getModelObject().onlineStatusAvailable) {
            super.setUserOnline(user);
        }
    }

    public interface Delegate extends CellDelegate<SwipeDataUser> {
        void onDeleteUserRequired(SwipeDataUser user);
    }
}
