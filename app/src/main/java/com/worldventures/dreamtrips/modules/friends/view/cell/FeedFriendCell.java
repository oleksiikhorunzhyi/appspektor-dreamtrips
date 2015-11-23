package com.worldventures.dreamtrips.modules.friends.view.cell;

import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.techery.spares.annotations.Layout;
import com.techery.spares.ui.view.cell.AbstractCell;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.module.RouteCreatorModule;
import com.worldventures.dreamtrips.core.navigation.ActivityRouter;
import com.worldventures.dreamtrips.core.navigation.NavigationBuilder;
import com.worldventures.dreamtrips.core.navigation.ToolbarConfig;
import com.worldventures.dreamtrips.core.navigation.creator.RouteCreator;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.friends.view.util.MutualStringUtil;
import com.worldventures.dreamtrips.modules.profile.bundle.UserBundle;

import javax.inject.Inject;
import javax.inject.Named;

import butterknife.InjectView;
import butterknife.OnClick;

@Layout(R.layout.adapter_item_feed_friend)
public class FeedFriendCell extends AbstractCell<User> {

    @InjectView(R.id.sdv_avatar)
    SimpleDraweeView sdvUserAvatar;
    @InjectView(R.id.tv_name)
    TextView tvName;
    @InjectView(R.id.tv_mutual)
    TextView tvMutual;
    @Inject
    ActivityRouter activityRouter;
    @Inject
    @Named(RouteCreatorModule.PROFILE)
    RouteCreator<Integer> routeCreator;

    private MutualStringUtil mutualStringUtil;

    public FeedFriendCell(View view) {
        super(view);
        mutualStringUtil = new MutualStringUtil(view.getContext());
    }

    @Override
    protected void syncUIStateWithModel() {
        User user = getModelObject();
        sdvUserAvatar.setImageURI(Uri.parse(user.getAvatar().getThumb()));
        sdvUserAvatar.invalidate(); // workaround for samsung devices

        tvName.setText(user.getFullName());

        String mutual = mutualStringUtil.createCircleAndMutualString(user);
        tvMutual.setVisibility(TextUtils.isEmpty(mutual) ? View.GONE : View.VISIBLE);
        tvMutual.setText(mutual);
    }

    @Override
    public void prepareForReuse() {

    }

    @OnClick({R.id.sdv_avatar, R.id.tv_name})
    public void onUserClicked() {
        NavigationBuilder.create().with(activityRouter)
                .data(new UserBundle(getModelObject()))
                .toolbarConfig(ToolbarConfig.Builder.create().visible(false).build())
                .move(routeCreator.createRoute(getModelObject().getId()));
    }
}
