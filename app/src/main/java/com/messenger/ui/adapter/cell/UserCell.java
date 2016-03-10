package com.messenger.ui.adapter.cell;

import android.net.Uri;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.messenger.entities.DataUser;
import com.messenger.ui.widget.AvatarView;
import com.techery.spares.ui.view.cell.AbstractDelegateCell;
import com.techery.spares.ui.view.cell.CellDelegate;
import com.worldventures.dreamtrips.R;

import butterknife.InjectView;

public abstract class UserCell<D extends CellDelegate<DataUser>> extends AbstractDelegateCell<DataUser, D> {

    @InjectView(R.id.contact_icon)
    AvatarView avatarView;
    @InjectView(R.id.contact_name_textview)
    TextView nameTextView;
    @InjectView(R.id.contact_chat_tick_image_view)
    ImageView tickImageView;

    public UserCell(View view) {
        super(view);
    }

    public ImageView getTickImageView() {
        return tickImageView;
    }

    @Override
    protected void syncUIStateWithModel() {
        DataUser user = getModelObject();
        nameTextView.setText(user.getName());
        avatarView.setOnline(user.isOnline());
        avatarView.setImageURI(Uri.parse(user.getAvatarUrl()));
    }

    @Override
    public void prepareForReuse() {

    }

    @Override
    public boolean shouldInject() {
        return false;
    }
}
