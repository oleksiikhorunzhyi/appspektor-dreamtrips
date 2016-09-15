package com.worldventures.dreamtrips.modules.reptools.view.fragment;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.navigation.router.NavigationConfigBuilder;
import com.worldventures.dreamtrips.core.utils.ViewUtils;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.common.model.ShareType;
import com.worldventures.dreamtrips.modules.common.presenter.ComponentPresenter;
import com.worldventures.dreamtrips.modules.common.view.bundle.ShareBundle;
import com.worldventures.dreamtrips.modules.common.view.dialog.ShareDialog;
import com.worldventures.dreamtrips.modules.infopages.view.fragment.staticcontent.StaticInfoFragment;
import com.worldventures.dreamtrips.modules.membership.bundle.UrlBundle;
import com.worldventures.dreamtrips.modules.reptools.model.SuccessStory;
import com.worldventures.dreamtrips.modules.reptools.presenter.SuccessStoryDetailsPresenter;

import butterknife.InjectView;
import butterknife.OnClick;

@Layout(R.layout.fragment_success_stories_details)
public class SuccessStoryDetailsFragment extends StaticInfoFragment<SuccessStoryDetailsPresenter, UrlBundle> implements SuccessStoryDetailsPresenter.View {

   public static final String EXTRA_STORY = "STORY";
   public static final String EXTRA_SLAVE = "SLAVE";

   private SuccessStory story;

   @InjectView(R.id.iv_share) protected ImageView ivShare;
   @InjectView(R.id.iv_like) protected ImageView ivLike;
   @InjectView(R.id.iv_full_screen) protected ImageView ivFullscreen;

   private boolean slave = false;

   @OnClick(R.id.iv_like)
   public void onLike() {
      TrackingHelper.favoriteSuccessStory(story.getUrl());
      getPresenter().like(story);
   }

   @OnClick(R.id.iv_share)
   public void onShare() {
      getPresenter().share();
   }

   @OnClick(R.id.iv_full_screen)
   public void onFullScreen() {
      if (!slave) {
         getActivity().finish();
      } else {
         Bundle bundle = new Bundle();
         bundle.putParcelable(SuccessStoryDetailsFragment.EXTRA_STORY, story);
         router.moveTo(Route.SUCCESS_STORES_DETAILS, NavigationConfigBuilder.forActivity().data(bundle).build());
      }
   }

   @Override
   public void afterCreateView(View rootView) {
      slave = getArguments().getBundle(ComponentPresenter.EXTRA_DATA).getBoolean(EXTRA_SLAVE);

      if (!ViewUtils.isTablet(getActivity())) {
         ivFullscreen.setVisibility(View.GONE);
      }
      super.afterCreateView(rootView);
      updateStoryLike(story.isLiked());

      webView.getSettings().setUseWideViewPort(true);
      TrackingHelper.viewSuccessStory(story.getUrl());
   }

   @Override
   public void onResume() {
      super.onResume();
      if (!slave) {
         ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(story.getAuthor());
         ivFullscreen.setImageResource(R.drawable.ic_fullscreen_collapse);

         ivFullscreen.setVisibility(isTabletLandscape() ? View.VISIBLE : View.INVISIBLE);
      } else {
         ivFullscreen.setImageResource(R.drawable.ic_fullscreen_open);
      }
   }

   @Override
   protected String getURL() {
      return story != null ? story.getUrl() : "";
   }

   @Override
   protected SuccessStoryDetailsPresenter createPresenter(Bundle savedInstanceState) {
      story = getArguments().getBundle(ComponentPresenter.EXTRA_DATA).getParcelable(EXTRA_STORY);
      return new SuccessStoryDetailsPresenter(story, getURL());
   }

   @Override
   public void showShareDialog() {
      new ShareDialog(getActivity(), type -> {
         TrackingHelper.shareSuccessStory(type, story.getUrl());
         getPresenter().onShare(type, story);
      }).show();
   }

   @Override
   public void likeRequestSuccess() {
      boolean isLike = !story.isLiked();
      story.setLiked(isLike);
      ivLike.setImageResource(isLike ? R.drawable.ic_success_heart_selected : R.drawable.ic_success_heart_normal);
      if (isLike) {
         informUser(getString(R.string.ss_has_been_added_to_favorites));
      } else {
         informUser(getString(R.string.ss_has_been_removed_from_favorites));
      }

      getPresenter().onStoryLiked(story);
   }

   @Override
   public void openShare(String url, @ShareType String type) {
      ShareBundle data = new ShareBundle();
      data.setShareUrl(url);
      data.setShareType(type);
      router.moveTo(Route.SHARE, NavigationConfigBuilder.forActivity().data(data).build());
   }

   @Override
   public void updateStoryLike(boolean isLiked) {
      if (isLiked) {
         ivLike.setImageResource(R.drawable.ic_success_heart_selected);
         ivLike.setContentDescription("selected");
      } else {
         ivLike.setImageResource(R.drawable.ic_success_heart_normal);
         ivLike.setContentDescription("");
      }
   }
}
