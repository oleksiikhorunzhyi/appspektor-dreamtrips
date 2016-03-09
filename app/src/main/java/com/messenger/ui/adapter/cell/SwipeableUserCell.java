package com.messenger.ui.adapter.cell;

import android.view.View;

import com.daimajia.swipe.SwipeLayout;
import com.messenger.entities.DataUser;
import com.messenger.util.SwipeClickListener;
import com.techery.spares.annotations.Layout;
import com.techery.spares.ui.view.cell.CellDelegate;
import com.worldventures.dreamtrips.R;

import butterknife.InjectView;

@Layout(R.layout.list_item_contact_swipe)
public class SwipeableUserCell extends UserCell<SwipeableUserCell.Delegate> {

    private boolean owner;
    private DataUser admin;

    @InjectView(R.id.swipe)
    SwipeLayout swipeLayout;
    @InjectView(R.id.swipe_layout_button_delete)
    View deleteButton;

    public SwipeableUserCell(View view) {
        super(view);
    }

    @Override
    protected void syncUIStateWithModel() {
        super.syncUIStateWithModel();
        DataUser user = getModelObject();
        swipeLayout.setSwipeEnabled(owner && !user.equals(admin));
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

    public void setOwner(boolean owner) {
        this.owner = owner;
    }

    public void setAdmin(DataUser admin) {
        this.admin = admin;
    }

    public interface Delegate extends CellDelegate<DataUser> {
        void onDeleteUserRequired(DataUser user);
    }
}
