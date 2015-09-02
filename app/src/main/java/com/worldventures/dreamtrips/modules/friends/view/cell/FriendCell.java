package com.worldventures.dreamtrips.modules.friends.view.cell;

import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.techery.spares.annotations.Layout;
import com.techery.spares.ui.view.cell.AbstractCell;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.friends.events.UserClickedEvent;
import com.worldventures.dreamtrips.modules.profile.view.dialog.FriendActionDialogDelegate;

import butterknife.InjectView;
import butterknife.OnClick;

@Layout(R.layout.adapter_item_friend)
public class FriendCell extends AbstractCell<User> {

    @InjectView(R.id.avatar)
    SimpleDraweeView userPhoto;
    @InjectView(R.id.tvName)
    TextView tvName;
    @InjectView(R.id.tvGroup)
    TextView tvGroup;
    @InjectView(R.id.tvMutual)
    TextView tvMutual;
    @InjectView(R.id.company)
    TextView companyName;

    FriendActionDialogDelegate dialog;

    public FriendCell(View view) {
        super(view);
    }

    @Override
    protected void syncUIStateWithModel() {
        User user = getModelObject();
        userPhoto.setImageURI(Uri.parse(user.getAvatar().getThumb()));
        tvName.setText(user.getFullName());
        if (!TextUtils.isEmpty(getModelObject().getCompany())) {
            companyName.setText(getModelObject().getCompany());
            companyName.setVisibility(View.VISIBLE);
        } else {
            companyName.setVisibility(View.GONE);
        }

        tvGroup.setText(user.getCircles());
        String mutual = itemView.getContext().getString(R.string.social_postfix_mutual_friends, getModelObject().getMutualFriends());
        if (getModelObject().getMutualFriends() == 0) {
            tvMutual.setVisibility(View.GONE);
        } else {
            tvMutual.setVisibility(View.VISIBLE);
            tvMutual.setText(mutual);
        }
    }

    @Override
    public void afterInject() {
        super.afterInject();
        if (dialog == null) {
            dialog = new FriendActionDialogDelegate(itemView.getContext(), getEventBus());
        }
    }

    @OnClick(R.id.avatar)
    void onUserClicked() {
        getEventBus().post(new UserClickedEvent(getModelObject()));
    }

    @Override
    public void prepareForReuse() {

    }

    @OnClick(R.id.actions)
    public void onAction(View v) {
        dialog.showFriendDialog(getModelObject(), userPhoto.getDrawable());
    }


}
