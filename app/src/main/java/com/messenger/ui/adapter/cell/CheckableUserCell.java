package com.messenger.ui.adapter.cell;

import android.view.View;

import com.messenger.entities.DataUser;
import com.techery.spares.annotations.Layout;
import com.techery.spares.ui.view.cell.CellDelegate;
import com.worldventures.dreamtrips.R;

import butterknife.OnClick;

@Layout(R.layout.list_item_contact)
public class CheckableUserCell extends UserCell<DataUser, CellDelegate<DataUser>> {

    public CheckableUserCell(View view) {
        super(view);
    }

    @Override
    protected void syncUIStateWithModel() {
        super.syncUIStateWithModel();
        tickImageView.setVisibility(View.VISIBLE);
    }

    @Override
    protected DataUser getDataUser() {
        return getModelObject();
    }

    @OnClick(R.id.contact_icon)
    void onAvatarClick() {
        cellDelegate.onCellClicked(getModelObject());
    }
}
