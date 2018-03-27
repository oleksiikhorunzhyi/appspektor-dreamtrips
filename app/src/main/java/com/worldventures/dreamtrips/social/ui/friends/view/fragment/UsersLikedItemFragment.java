package com.worldventures.dreamtrips.social.ui.friends.view.fragment;

import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.TextView;

import com.techery.spares.utils.ui.OrientationUtil;
import com.worldventures.core.model.User;
import com.worldventures.core.ui.annotations.Layout;
import com.worldventures.core.utils.QuantityHelper;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.social.ui.activity.presenter.ComponentPresenter;
import com.worldventures.dreamtrips.social.ui.friends.bundle.UsersLikedEntityBundle;
import com.worldventures.dreamtrips.social.ui.friends.presenter.BaseUserListPresenter;
import com.worldventures.dreamtrips.social.ui.friends.presenter.UsersLikedItemPresenter;
import com.worldventures.dreamtrips.social.ui.friends.view.cell.UserCell;
import com.worldventures.dreamtrips.social.ui.friends.view.cell.delegate.UserCellDelegate;

import org.jetbrains.annotations.Nullable;

import java.util.List;

import butterknife.InjectView;

@Layout(R.layout.fragment_likes)
@ComponentPresenter.ComponentTitle(R.string.users_who_liked_title)
public class UsersLikedItemFragment extends BaseUsersFragment<UsersLikedItemPresenter, UsersLikedEntityBundle>
      implements BaseUserListPresenter.View, UserCellDelegate {

   @InjectView(R.id.title) TextView header;

   @Override
   protected UsersLikedItemPresenter createPresenter(Bundle savedInstanceState) {
      return new UsersLikedItemPresenter(getArgs());
   }

   @Override
   public void afterCreateView(View rootView) {
      super.afterCreateView(rootView);
      adapter.registerCell(User.class, UserCell.class);
      adapter.registerDelegate(User.class, this);
   }

   @Override
   public void onResume() {
      super.onResume();
      OrientationUtil.lockOrientation(getActivity());
      //hack for https://trello.com/c/oKIh9Rnb/922-nav-bar-of-likers-pop-up-becomes-grey-if-go-back-from-profile (reproducible on android 5.0+ )
      header.getBackground().mutate().setAlpha(255);
   }

   @Override
   public void onPause() {
      super.onPause();
      OrientationUtil.unlockOrientation(getActivity());
   }

   @SuppressWarnings("unchecked")
   @Override
   public void refreshUsers(@Nullable List<? extends User> users, boolean noMoreItems) {
      super.refreshUsers(users, noMoreItems);
      if (isTabletLandscape()) {
         String titleArg = users.size() == 1 ? users.get(0)
               .getFullName() : String.valueOf(getLikersCount((List<User>) users));
         @StringRes int quantityStringId = QuantityHelper.chooseResource(users.size(), R.string.users_who_liked_title, R.string.people_liked_one, R.string.people_liked_other);
         String title = String.format(getResources().getString(quantityStringId), titleArg);
         header.setText(title);
         header.setVisibility(View.VISIBLE);
      }
   }

   private int getLikersCount(List<User> users) {
      return getArgs() != null && getArgs().getLikersCount() > 0 ? getArgs().getLikersCount() : users.size();
   }

   @Override
   protected LinearLayoutManager createLayoutManager() {
      return new LinearLayoutManager(getActivity());
   }

   @Override
   public void acceptRequest(User user) {
      getPresenter().acceptRequest(user.copy());
   }

   @Override
   public void addUserRequest(User user) {
      getPresenter().addUserRequest(user.copy());
   }

   @Override
   public void onOpenPrefs(User user) {
      getPresenter().openPrefs(user);
   }

   @Override
   public void onStartSingleChat(User user) {
      getPresenter().startChat(user);
   }

   @Override
   public void onUnfriend(User user) {
      getPresenter().unfriend(user.copy());
   }
}
