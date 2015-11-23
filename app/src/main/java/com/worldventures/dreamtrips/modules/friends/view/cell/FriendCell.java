package com.worldventures.dreamtrips.modules.friends.view.cell;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
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
import com.worldventures.dreamtrips.modules.friends.view.util.MutualStringUtil;
import com.worldventures.dreamtrips.modules.profile.view.dialog.FriendActionDialogDelegate;

import butterknife.InjectView;
import butterknife.OnClick;

@Layout(R.layout.adapter_item_friend)
public class FriendCell extends AbstractCell<User> {

    @InjectView(R.id.sdv_avatar)
    SimpleDraweeView sdvUserPhoto;
    @InjectView(R.id.tv_name)
    TextView tvName;
    @InjectView(R.id.tv_group)
    TextView tvGroup;
    @InjectView(R.id.tv_mutual)
    TextView tvMutual;
    @InjectView(R.id.tv_company)
    TextView tvCompany;

    FriendActionDialogDelegate dialog;

    private MutualStringUtil mutualStringUtil;

    public FriendCell(View view) {
        super(view);
        mutualStringUtil = new MutualStringUtil(view.getContext());
    }

    @Override
    protected void syncUIStateWithModel() {
        User user = getModelObject();
        sdvUserPhoto.setImageURI(Uri.parse(user.getAvatar().getThumb()));
        sdvUserPhoto.invalidate(); // workaround for samsung devices
        tvName.setText(user.getFullName());

        String companyName = getModelObject().getCompany();
        tvCompany.setVisibility(TextUtils.isEmpty(companyName) ? View.GONE : View.VISIBLE);
        tvCompany.setText(getModelObject().getCompany());

        String circleName = user.getCircles();
        tvGroup.setVisibility(TextUtils.isEmpty(circleName) ? View.GONE : View.VISIBLE);
        tvGroup.setText(circleName);

        String mutual = mutualStringUtil.createMutualString(getModelObject().getMutualFriends());
        tvMutual.setVisibility(TextUtils.isEmpty(mutual) ? View.GONE : View.VISIBLE);
        tvMutual.setText(mutual);
    }

    @Override
    public void afterInject() {
        super.afterInject();
        if (dialog == null) {
            dialog = new FriendActionDialogDelegate(itemView.getContext(), getEventBus());
        }
    }

    @OnClick(R.id.sdv_avatar)
    void onUserClicked() {
        getEventBus().post(new UserClickedEvent(getModelObject()));
    }

    @Override
    public void prepareForReuse() {

    }

    @OnClick(R.id.tv_actions)
    public void onAction(View v) {
        sdvUserPhoto.buildDrawingCache();
        Drawable profileIcon = new BitmapDrawable(v.getResources(), Bitmap.createBitmap(sdvUserPhoto.getDrawingCache()));
        sdvUserPhoto.destroyDrawingCache();
        dialog.showFriendDialog(getModelObject(), profileIcon);
    }


}
