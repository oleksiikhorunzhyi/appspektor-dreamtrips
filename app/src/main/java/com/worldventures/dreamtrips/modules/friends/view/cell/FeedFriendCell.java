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

    public FeedFriendCell(View view) {
        super(view);
    }

    @Override
    protected void syncUIStateWithModel() {
        User user = getModelObject();
        sdvUserAvatar.setImageURI(Uri.parse(user.getAvatar().getThumb()));
        sdvUserAvatar.invalidate(); // workaround for samsung devices

        tvName.setText(user.getFullName());

        String circleAndMutualString = createCircleAndMutualString(user);
        if (TextUtils.isEmpty(circleAndMutualString)) {
            tvMutual.setVisibility(View.GONE);
        } else {
            tvMutual.setVisibility(View.VISIBLE);
            tvMutual.setText(circleAndMutualString);
        }
    }

    private String createCircleAndMutualString(User user) {
        StringBuilder sb = new StringBuilder();
        if (!TextUtils.isEmpty(user.getCircles())) {
            sb.append(user.getCircles());

            if (user.getMutualFriends() > 0) {
                sb.append(", ");
            }
        }

        if (user.getMutualFriends() > 0) {
            sb.append(itemView.getContext().getString(R.string.social_postfix_mutual_friends, user.getMutualFriends()));
        }

        return sb.toString();
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
