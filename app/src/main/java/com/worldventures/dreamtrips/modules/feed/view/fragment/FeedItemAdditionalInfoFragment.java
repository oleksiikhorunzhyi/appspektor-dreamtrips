package com.worldventures.dreamtrips.modules.feed.view.fragment;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.techery.spares.annotations.Layout;
import com.techery.spares.module.Injector;
import com.techery.spares.module.qualifier.ForActivity;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.module.RouteCreatorModule;
import com.worldventures.dreamtrips.core.navigation.ToolbarConfig;
import com.worldventures.dreamtrips.core.navigation.creator.RouteCreator;
import com.worldventures.dreamtrips.core.rx.RxBaseFragmentWithArgs;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.common.view.custom.SmartAvatarView;
import com.worldventures.dreamtrips.modules.feed.bundle.FeedAdditionalInfoBundle;
import com.worldventures.dreamtrips.modules.feed.presenter.FeedItemAdditionalInfoPresenter;
import com.worldventures.dreamtrips.modules.profile.bundle.UserBundle;

import java.text.DecimalFormat;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;

import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.Optional;

import static com.worldventures.dreamtrips.core.navigation.router.NavigationConfigBuilder.forActivity;

@Layout(R.layout.fragment_feed_item_additional_info)
public class FeedItemAdditionalInfoFragment<P extends FeedItemAdditionalInfoPresenter> extends RxBaseFragmentWithArgs<P, FeedAdditionalInfoBundle> implements FeedItemAdditionalInfoPresenter.View {

    @InjectView(R.id.user_cover) SimpleDraweeView userCover;
    @InjectView(R.id.user_photo) SmartAvatarView userPhoto;
    @InjectView(R.id.user_name) TextView userName;
    @InjectView(R.id.company_name) TextView companyName;
    @InjectView(R.id.view_profile) TextView viewProfile;

    @Inject @Named(RouteCreatorModule.PROFILE) RouteCreator<Integer> routeCreator;
    @Inject @ForActivity Provider<Injector> injectorProvider;

    DecimalFormat df = new DecimalFormat("#0.00");

    @Override
    protected P createPresenter(Bundle savedInstanceState) {
        FeedItemAdditionalInfoPresenter presenter = new FeedItemAdditionalInfoPresenter(getArgs() != null ? getArgs().getUser() : null);
        return (P) presenter;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getPresenter().loadUser();
    }

    @Override
    public void setupView(User user) {
        userPhoto.setImageURI(Uri.parse(user.getAvatar().getThumb()));
        userPhoto.setup(user, injectorProvider.get());
        userCover.setImageURI(Uri.parse(user.getBackgroundPhotoUrl()));
        userName.setText(user.getFullName());
        companyName.setText(user.getCompany());
        viewProfile.setVisibility(View.VISIBLE);
    }

    @Optional
    @OnClick({R.id.user_cover, R.id.view_profile})
    protected void onUserClick() {
        router.moveTo(routeCreator.createRoute(getArgs().getUser().getId()), forActivity()
                .toolbarConfig(ToolbarConfig.Builder.create().visible(false).build())
                .data(new UserBundle(getArgs().getUser()))
                .build());
    }
}
