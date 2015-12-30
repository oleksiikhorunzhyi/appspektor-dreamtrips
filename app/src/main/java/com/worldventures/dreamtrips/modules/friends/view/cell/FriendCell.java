package com.worldventures.dreamtrips.modules.friends.view.cell;

import android.text.TextUtils;
import android.view.View;

import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.view.util.DrawableUtil;
import com.worldventures.dreamtrips.modules.profile.view.dialog.FriendActionDialogDelegate;

import butterknife.OnClick;

@Layout(R.layout.adapter_item_friend)
public class FriendCell extends BaseUserCell {

    FriendActionDialogDelegate dialog;

    private DrawableUtil drawableUtil;

    public FriendCell(View view) {
        super(view);
        drawableUtil = new DrawableUtil(view.getContext());
    }

    @Override
    protected void syncUIStateWithModel() {
        super.syncUIStateWithModel();

        String circleName = getModelObject().getCirclesString();
        tvGroup.setVisibility(TextUtils.isEmpty(circleName) ? View.GONE : View.VISIBLE);
        tvGroup.setText(circleName);
    }

    @Override
    public void afterInject() {
        super.afterInject();
        if (dialog == null) {
            dialog = new FriendActionDialogDelegate(itemView.getContext(), getEventBus());
        }
    }

    @OnClick(R.id.tv_actions)
    public void onAction() {
        sdvAvatar.setDrawingCacheEnabled(true);
        dialog.showFriendDialog(getModelObject(), drawableUtil.copyIntoDrawable(sdvAvatar.getDrawingCache()));
    }
}
