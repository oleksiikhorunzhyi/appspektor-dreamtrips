package com.worldventures.dreamtrips.social.ui.profile.view.fragment;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.innahema.collections.query.functions.Action1;
import com.worldventures.core.model.Circle;
import com.worldventures.core.model.User;
import com.worldventures.core.ui.annotations.Layout;
import com.worldventures.core.ui.annotations.MenuResource;
import com.worldventures.core.ui.util.DrawableUtil;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.navigation.router.NavigationConfigBuilder;
import com.worldventures.dreamtrips.social.ui.feed.model.FeedItem;
import com.worldventures.dreamtrips.social.ui.profile.bundle.UserBundle;
import com.worldventures.dreamtrips.social.ui.profile.presenter.UserPresenter;
import com.worldventures.dreamtrips.social.ui.profile.view.dialog.FriendActionDialogDelegate;

import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;

@Layout(R.layout.fragment_profile)
@MenuResource(R.menu.menu_empty)
public class UserFragment extends ProfileFragment<UserPresenter> implements UserPresenter.View {

   private MenuItem chatActionItem;
   @Inject protected DrawableUtil drawableUtil;

   private MaterialDialog blockingProgressDialog;

   @Override
   protected UserPresenter createPresenter(Bundle savedInstanceState) {
      return new UserPresenter(getArgs());
   }

   @Override
   public void afterCreateView(View rootView) {
      super.afterCreateView(rootView);
      profileToolbarTitle.setVisibility(View.INVISIBLE);
      profileToolbarUserStatus.setVisibility(View.INVISIBLE);

      profileToolbar.inflateMenu(R.menu.user_profile_fragment);
      chatActionItem = profileToolbar.getMenu().findItem(R.id.action_chat);
      chatActionItem.setOnMenuItemClickListener(item -> {
         getPresenter().onStartChatClicked();
         return true;
      });
      showChatButtonForFriend(getPresenter().getUser());
   }

   @Override
   public void refreshFeedItems(List<FeedItem> items, User user) {
      super.refreshFeedItems(items, user);
      showChatButtonForFriend(user);
   }

   public void showChatButtonForFriend(User user) {
      chatActionItem.setVisible(user.getRelationship() == User.Relationship.FRIEND);
   }

   public void showAddFriendDialog(List<Circle> circles, Action1<Circle> selectedAction) {
      MaterialDialog.Builder builder = new MaterialDialog.Builder(getActivity());
      builder.title(getString(R.string.friend_add_to))
            .adapter(new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, circles), (materialDialog, view, i, charSequence) -> {
               selectedAction.apply(circles.get(i));
               materialDialog.dismiss();
            })
            .negativeText(R.string.action_cancel)
            .show();
   }

   @Override
   public void showFriendDialog(User user) {
      ImageView userPhoto = ButterKnife.findById(statePaginatedRecyclerViewManager.stateRecyclerView, R.id.user_photo);
      if (userPhoto != null) {
         userPhoto.setDrawingCacheEnabled(true);
         new FriendActionDialogDelegate(getActivity())
               .onFriendPrefsAction(getPresenter()::openPrefs)
               .onUnfriend(item -> getPresenter().unfriend())
               .showFriendDialogSkipChat(user, drawableUtil.copyIntoDrawable(userPhoto.getDrawingCache()));
      }
   }

   @Override
   public void openFriendPrefs(UserBundle userBundle) {
      router.moveTo(Route.FRIEND_PREFERENCES, NavigationConfigBuilder.forActivity().data(userBundle).build());
   }

   @Override
   protected void initToolbar() {
      profileToolbar.setNavigationIcon(R.drawable.back_icon);
      profileToolbar.setNavigationOnClickListener(view -> getActivity().onBackPressed());
   }

   @Override
   public void onAcceptRequest() {
      getPresenter().acceptClicked();
   }

   @Override
   public void onRejectRequest() {
      getPresenter().rejectClicked();
   }

   @Override
   public void onAddFriend() {
      getPresenter().addFriendClicked();
   }

   @Override
   public void showBlockingProgress() {
      blockingProgressDialog = new MaterialDialog.Builder(getActivity()).progress(true, 0)
            .content(R.string.loading)
            .cancelable(false)
            .canceledOnTouchOutside(false)
            .show();
   }

   @Override
   public void hideBlockingProgress() {
      if (blockingProgressDialog != null) {
         blockingProgressDialog.dismiss();
      }
   }

   @Override
   public Route getRoute() {
      return Route.FOREIGN_PROFILE;
   }
}
