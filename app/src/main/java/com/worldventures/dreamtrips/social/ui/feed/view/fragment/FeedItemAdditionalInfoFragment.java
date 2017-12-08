package com.worldventures.dreamtrips.social.ui.feed.view.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.worldventures.core.di.qualifier.ForActivity;
import com.worldventures.core.janet.Injector;
import com.worldventures.core.model.User;
import com.worldventures.core.ui.annotations.Layout;
import com.worldventures.core.ui.util.GraphicUtils;
import com.worldventures.core.utils.ProjectTextUtils;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.module.FragmentClassProviderModule;
import com.worldventures.dreamtrips.core.navigation.ToolbarConfig;
import com.worldventures.dreamtrips.core.navigation.creator.FragmentClassProvider;
import com.worldventures.dreamtrips.core.rx.RxBaseFragmentWithArgs;
import com.worldventures.dreamtrips.social.ui.feed.bundle.FeedAdditionalInfoBundle;
import com.worldventures.dreamtrips.social.ui.feed.presenter.FeedItemAdditionalInfoPresenter;
import com.worldventures.dreamtrips.social.ui.profile.bundle.UserBundle;
import com.worldventures.dreamtrips.social.ui.profile.view.widgets.SmartAvatarView;

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

   @Inject @Named(FragmentClassProviderModule.PROFILE) FragmentClassProvider<Integer> fragmentClassProvider;
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
      userPhoto.setup(user, injectorProvider.get());
      userPhoto.post(() -> setUserPhotoAndCover(user));
      userName.setText(user.getFullName());
      companyName.setText(user.getCompany());
      viewProfile.setVisibility(View.VISIBLE);
   }

   @Optional
   @OnClick({R.id.user_cover, R.id.view_profile})
   protected void onUserClick() {
      router.moveTo(fragmentClassProvider.provideFragmentClass(getArgs().getUser()
            .getId()), forActivity().toolbarConfig(ToolbarConfig.Builder.create().visible(false).build())
            .data(new UserBundle(getArgs().getUser()))
            .build());
   }

   private void setUserPhotoAndCover(User user) {
      if (!ProjectTextUtils.isEmpty(user.getAvatar().getThumb())) {
         userPhoto.setController(GraphicUtils.provideFrescoResizingController(user.getAvatar().getThumb(),
               userPhoto.getController(), userPhoto.getWidth(), userPhoto.getHeight()));
      }
      if (!ProjectTextUtils.isEmpty(user.getBackgroundPhotoUrl())) {
         userCover.setController(GraphicUtils.provideFrescoResizingController(user.getBackgroundPhotoUrl(),
               userCover.getController(), userCover.getWidth(), userCover.getHeight()));
      }
   }

}
