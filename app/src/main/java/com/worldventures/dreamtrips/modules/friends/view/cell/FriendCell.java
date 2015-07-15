package com.worldventures.dreamtrips.modules.friends.view.cell;

import android.content.Context;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.techery.spares.annotations.Layout;
import com.techery.spares.ui.view.cell.AbstractCell;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.friends.events.UserClickedEvent;
import com.worldventures.dreamtrips.modules.friends.model.Friend;

import butterknife.InjectView;
import butterknife.OnClick;

@Layout(R.layout.adapter_item_friend)
public class FriendCell extends AbstractCell<Friend> {

    @InjectView(R.id.avatar)
    SimpleDraweeView userPhoto;
    @InjectView(R.id.tvName)
    TextView tvName;
    @InjectView(R.id.tvGroup)
    TextView tvGroup;
    @InjectView(R.id.tvMutual)
    TextView tvMutual;

    public FriendCell(View view) {
        super(view);
    }

    @Override
    protected void syncUIStateWithModel() {
        Friend user = getModelObject();
        userPhoto.setImageURI(Uri.parse(user.getAvatar().getThumb()));
        tvName.setText(user.getFullName());
        tvGroup.setText(user.getCircles());
        String mutual = itemView.getContext().getString(R.string.social_postfix_mutual_friends, getModelObject().getMutualFriends());
        if (getModelObject().getMutualFriends() == 0) {
            tvMutual.setVisibility(View.GONE);
        } else {
            tvMutual.setVisibility(View.VISIBLE);
            tvMutual.setText(mutual);
        }
    }

    @OnClick(R.id.avatar)
    void onUserClicked() {
        getEventBus().post(new UserClickedEvent(getModelObject()));
    }

    @Override
    public void prepareForReuse() {

    }

    public void onAction(View v) {
        Context c = v.getContext();
        AlertDialog.Builder builder = new AlertDialog.Builder(c);
        builder.setTitle(getModelObject().getFullName());
        builder.setIcon(userPhoto.getDrawable());
        builder.setNegativeButton(R.string.friend_cancel, (dialogInterface, i) ->
                dialogInterface.dismiss());
        builder.setItems(new String[]{c.getString(R.string.social_remove_friend_title)}, (dialogInterface, i) -> {
            //TODO
        });
        builder.show();
    }


}
