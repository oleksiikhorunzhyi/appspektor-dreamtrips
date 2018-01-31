package com.worldventures.dreamtrips.social.ui.reptools.view.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.worldventures.core.model.ShareType;
import com.worldventures.core.ui.annotations.Layout;
import com.worldventures.core.ui.util.ViewUtils;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.router.NavigationConfigBuilder;
import com.worldventures.dreamtrips.modules.common.view.dialog.ShareDialog;
import com.worldventures.dreamtrips.social.ui.activity.presenter.ComponentPresenter;
import com.worldventures.dreamtrips.social.ui.infopages.view.fragment.staticcontent.StaticInfoFragment;
import com.worldventures.dreamtrips.social.ui.membership.bundle.UrlBundle;
import com.worldventures.dreamtrips.social.ui.reptools.model.SuccessStory;
import com.worldventures.dreamtrips.social.ui.reptools.presenter.SuccessStoryDetailsPresenter;
import com.worldventures.dreamtrips.social.ui.share.bundle.ShareBundle;
import com.worldventures.dreamtrips.social.ui.share.view.ShareFragment;

import org.jetbrains.annotations.NotNull;

import butterknife.InjectView;
import butterknife.OnClick;

@Layout(R.layout.fragment_success_stories_details)
public class SuccessStoryDetailsFragment extends StaticInfoFragment<SuccessStoryDetailsPresenter, UrlBundle> implements SuccessStoryDetailsPresenter.View {

   public static final String EXTRA_STORY = "STORY";
   public static final String EXTRA_SLAVE = "SLAVE";

   @InjectView(R.id.iv_like) protected ImageView likeIcon;
   @InjectView(R.id.iv_full_screen) protected ImageView fullscreenIcon;

   private boolean slave = false;

   private String storyUrl = "";

   @OnClick(R.id.iv_like)
   public void onLike() {
      getPresenter().like();
   }

   @OnClick(R.id.iv_share)
   public void onShare() {
      new ShareDialog(getActivity(), type -> getPresenter().onShare(type)).show();
   }

   @OnClick(R.id.iv_full_screen)
   public void onFullScreen() {
      if (!slave) {
         getActivity().finish();
      } else {
         getPresenter().onFullscreenPressed();
      }
   }

   @Override
   public void openFullscreen(@NotNull SuccessStory story) {
      Bundle bundle = new Bundle();
      bundle.putParcelable(SuccessStoryDetailsFragment.EXTRA_STORY, story);
      router.moveTo(SuccessStoryDetailsFragment.class, NavigationConfigBuilder.forActivity().data(bundle).build());
   }

   @Override
   public void afterCreateView(View rootView) {
      super.afterCreateView(rootView);
      slave = getArguments().getBundle(ComponentPresenter.EXTRA_DATA).getBoolean(EXTRA_SLAVE);
      if (!ViewUtils.isTablet(getActivity())) {
         fullscreenIcon.setVisibility(View.GONE);
      }
      webView.getSettings().setUseWideViewPort(true);
   }

   @Override
   public void onResume() {
      super.onResume();
      if (!slave) {
         getPresenter().onUpdateAuthorRequired();
         fullscreenIcon.setImageResource(R.drawable.ic_fullscreen_collapse);
         fullscreenIcon.setVisibility(isTabletLandscape() ? View.VISIBLE : View.INVISIBLE);
      } else {
         fullscreenIcon.setImageResource(R.drawable.ic_fullscreen_open);
      }
   }

   @Override
   public void updateStoryTitle(@NonNull String author) {
      ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(author);
   }

   @Override
   protected String getURL() {
      return storyUrl;
   }

   @Override
   protected SuccessStoryDetailsPresenter createPresenter(Bundle savedInstanceState) {
      SuccessStory story = getArguments().getBundle(ComponentPresenter.EXTRA_DATA).getParcelable(EXTRA_STORY);
      storyUrl = story.getUrl();
      return new SuccessStoryDetailsPresenter(story, getURL());
   }

   @Override
   public void likeRequestSuccess(boolean isLiked) {
      informUser(isLiked ? getString(R.string.ss_has_been_added_to_favorites) : getString(R.string.ss_has_been_removed_from_favorites));
   }

   @Override
   public void updateLikeStatus(boolean isLike) {
      likeIcon.setImageResource(isLike ? R.drawable.ic_success_heart_selected : R.drawable.ic_success_heart_normal);
      likeIcon.setContentDescription(isLike ? "selected" : "");
   }

   @Override
   public void openShare(@NonNull String url, @NonNull @ShareType String type) {
      ShareBundle data = new ShareBundle();
      data.setShareUrl(url);
      data.setShareType(type);
      router.moveTo(ShareFragment.class, NavigationConfigBuilder.forActivity().data(data).build());
   }
}
