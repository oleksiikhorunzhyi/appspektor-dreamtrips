package com.worldventures.dreamtrips.modules.friends.view.fragment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.TextView;

import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.friends.bundle.MutualFriendsBundle;
import com.worldventures.dreamtrips.modules.friends.presenter.MutualFriendsPresenter;
import com.worldventures.dreamtrips.modules.friends.view.cell.MutualFriendCell;
import com.worldventures.dreamtrips.modules.friends.view.cell.delegate.FriendCellDelegate;

import butterknife.InjectView;

@Layout(R.layout.fragment_mutuals)
public class MutualFriendsFragment extends BaseUsersFragment<MutualFriendsPresenter, MutualFriendsBundle>
      implements MutualFriendsPresenter.View, FriendCellDelegate {

   @InjectView(R.id.title) TextView header;

   @Override
   protected MutualFriendsPresenter createPresenter(Bundle savedInstanceState) {
      return new MutualFriendsPresenter(getArgs());
   }

   @Override
   public void afterCreateView(View rootView) {
      super.afterCreateView(rootView);
      if (isTabletLandscape()) header.setVisibility(View.VISIBLE);
      caption.setText(R.string.no_mutual_friends);
      adapter.registerCell(User.class, MutualFriendCell.class);
      adapter.registerDelegate(User.class, this);
   }

   @Override
   public void onResume() {
      super.onResume();
      //hack for https://trello.com/c/oKIh9Rnb/922-nav-bar-of-likers-pop-up-becomes-grey-if-go-back-from-profile (reproducible on android 5.0+ )
      header.getBackground().setAlpha(255);
   }

   @Override
   protected LinearLayoutManager createLayoutManager() {
      return new LinearLayoutManager(getActivity());
   }

   @Override
   public void onOpenPrefs(User user) {
      getPresenter().openPrefs(user);
   }

   @Override
   public void onStartSingleChat(User user) {
      //not required
   }

   @Override
   public void onUnfriend(User user) {
      getPresenter().unfriend(user);
   }
}
