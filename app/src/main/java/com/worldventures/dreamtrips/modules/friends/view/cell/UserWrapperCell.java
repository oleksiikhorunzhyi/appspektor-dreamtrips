package com.worldventures.dreamtrips.modules.friends.view.cell;

import android.content.Context;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.techery.spares.annotations.Layout;
import com.techery.spares.ui.view.cell.AbstractCell;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.friends.model.Friend;

import butterknife.InjectView;
import butterknife.OnClick;

@Layout(R.layout.adapter_item_user_wrapper)
public class UserWrapperCell extends AbstractCell<Friend> {

    @InjectView(R.id.user_photo)
    SimpleDraweeView userPhoto;
    @InjectView(R.id.tvName)
    TextView tvName;
    @InjectView(R.id.tvGroup)
    TextView tvGroup;
    @InjectView(R.id.tvMutual)
    TextView tvMutual;
    @InjectView(R.id.action)
    Button action;

    public UserWrapperCell(View view) {
        super(view);
    }

    @Override
    protected void syncUIStateWithModel() {
        User user = getModelObject();
        userPhoto.setImageURI(Uri.parse(user.getAvatar().getThumb()));
        tvName.setText(user.getFullName());
        tvGroup.setText(convertGroups(getModelObject().getGroups()));
        String postfix = itemView.getContext().getString(R.string.social_postfix_mutual_friends);
        String mutual = String.format(postfix, getModelObject().getMutualFriends());
        if (getModelObject().getMutualFriends() == 0) {
            tvMutual.setVisibility(View.GONE);
        } else {
            tvMutual.setVisibility(View.VISIBLE);
            tvMutual.setText(mutual);
        }
    }

    @Override
    public void prepareForReuse() {

    }

    @OnClick(R.id.action)
    public void onAction(View v) {
        Context c = v.getContext();
        AlertDialog.Builder builder = new AlertDialog.Builder(c);
        builder.setTitle(getModelObject().getFullName());
        builder.setIcon(userPhoto.getDrawable());
        builder.setItems(new String[]{c.getString(R.string.social_remove_friend_title)}, (dialogInterface, i) -> {
            //TODO
        });
        builder.show();
    }

    private String convertGroups(String[] groups) {
        String result = "";
        for (String group : groups) {
            result = result + ", " + getFirstUppercaseField(group);
        }
        return result.substring(result.indexOf(",") + 1);
    }

    private String getFirstUppercaseField(String text) {
        if (text.length() > 1) {
            text = text.substring(0, 1).toUpperCase() + text.substring(1).toLowerCase();
        }
        return text;
    }
}
