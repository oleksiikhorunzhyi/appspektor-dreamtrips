package com.worldventures.dreamtrips.modules.feed.view.fragment;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.module.RouteCreatorModule;
import com.worldventures.dreamtrips.core.navigation.NavigationBuilder;
import com.worldventures.dreamtrips.core.navigation.ToolbarConfig;
import com.worldventures.dreamtrips.core.navigation.creator.RouteCreator;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragmentWithArgs;
import com.worldventures.dreamtrips.modules.feed.bundle.FeedAdditionalInfoBundle;
import com.worldventures.dreamtrips.modules.feed.presenter.FeedItemAdditionalInfoPresenter;
import com.worldventures.dreamtrips.modules.profile.bundle.UserBundle;

import java.text.DecimalFormat;

import javax.inject.Inject;
import javax.inject.Named;

import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.Optional;

@Layout(R.layout.fragment_feed_item_additional_info)
public class FeedItemAdditionalInfoFragment<P extends FeedItemAdditionalInfoPresenter> extends BaseFragmentWithArgs<P, FeedAdditionalInfoBundle> implements FeedItemAdditionalInfoPresenter.View {

    @Inject
    @Named(RouteCreatorModule.PROFILE)
    RouteCreator<Integer> routeCreator;

    DecimalFormat df = new DecimalFormat("#0.00");

    @InjectView(R.id.user_cover)
    SimpleDraweeView userCover;
    @InjectView(R.id.user_photo)
    SimpleDraweeView userPhoto;
    @InjectView(R.id.user_name)
    TextView userName;
    @InjectView(R.id.company_name)
    TextView companyName;
    @InjectView(R.id.view_profile)
    TextView viewProfile;

    @Override
    protected P createPresenter(Bundle savedInstanceState) {
        return (P) new FeedItemAdditionalInfoPresenter();
    }

    @Override
    public void afterCreateView(View rootView) {
        super.afterCreateView(rootView);
        setUser(getArgs().getUser());
    }

    protected void setUser(User user) {
        if (user != null) {
            userPhoto.setImageURI(Uri.parse(user.getAvatar().getThumb()));
            userCover.setImageURI(Uri.parse(user.getBackgroundPhotoUrl()));
            userName.setText(user.getFullName());
            companyName.setText(user.getCompany());
            viewProfile.setVisibility(View.VISIBLE);
        }
    }

    @Optional
    @OnClick({R.id.user_cover, R.id.view_profile})
    protected void onUserClick() {
        NavigationBuilder.create().with(activityRouter)
                .data(new UserBundle(getArgs().getUser()))
                .toolbarConfig(ToolbarConfig.Builder.create().visible(false).build())
                .move(routeCreator.createRoute(getArgs().getUser().getId()));
    }

}
