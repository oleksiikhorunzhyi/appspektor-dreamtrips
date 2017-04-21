package com.worldventures.dreamtrips.modules.friends.view.fragment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.techery.spares.adapter.BaseDelegateAdapter;
import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.view.custom.EmptyRecyclerView;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragmentWithArgs;
import com.worldventures.dreamtrips.modules.friends.presenter.FriendPreferencesPresenter;
import com.worldventures.dreamtrips.modules.profile.bundle.UserBundle;
import com.worldventures.dreamtrips.modules.profile.model.FriendGroupRelation;
import com.worldventures.dreamtrips.modules.profile.view.cell.FriendPrefGroupCell;
import com.worldventures.dreamtrips.modules.profile.view.cell.delegate.FriendPrefsCellDelegate;
import com.worldventures.dreamtrips.modules.profile.view.cell.delegate.State;

import java.util.List;

import butterknife.InjectView;
import butterknife.OnClick;

@Layout(R.layout.fragment_friend_preference)
public class FriendPreferenceFragment extends BaseFragmentWithArgs<FriendPreferencesPresenter, UserBundle>
      implements FriendPreferencesPresenter.View, FriendPrefsCellDelegate {

   @InjectView(R.id.recyclerViewGroups) EmptyRecyclerView recyclerViewGroups;

   private BaseDelegateAdapter<FriendGroupRelation> adapter;
   private MaterialDialog blockingProgressDialog;

   @Override
   protected FriendPreferencesPresenter createPresenter(Bundle savedInstanceState) {
      return new FriendPreferencesPresenter(getArgs());
   }

   @Override
   public void afterCreateView(View rootView) {
      recyclerViewGroups.setLayoutManager(new LinearLayoutManager(getActivity()));
      adapter = new BaseDelegateAdapter<>(getActivity(), this);
      adapter.registerCell(FriendGroupRelation.class, FriendPrefGroupCell.class);
      adapter.registerDelegate(FriendGroupRelation.class, this);
      recyclerViewGroups.setAdapter(adapter);
   }

   @Override
   public void onCellClicked(FriendGroupRelation model) {
      //not needed
   }

   @Override
   public void onRelationChanged(FriendGroupRelation friendGroupRelation, State state) {
      getPresenter().onRelationshipChanged(friendGroupRelation.circle(), state);
   }

   @Override
   public void addItems(List<FriendGroupRelation> circles) {
      adapter.addItems(circles);
   }

   @OnClick(R.id.createNewListBtn)
   void onCreateClick() {
      informUser("TODO");
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
      if (blockingProgressDialog != null) blockingProgressDialog.dismiss();
   }

}
