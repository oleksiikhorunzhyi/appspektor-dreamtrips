package com.worldventures.dreamtrips.modules.friends.view.fragment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.techery.spares.adapter.BaseArrayListAdapter;
import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.view.custom.EmptyRecyclerView;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragmentWithArgs;
import com.worldventures.dreamtrips.modules.friends.presenter.FriendPreferencesPresenter;
import com.worldventures.dreamtrips.modules.profile.bundle.UserBundle;
import com.worldventures.dreamtrips.modules.profile.model.FriendGroupRelation;
import com.worldventures.dreamtrips.modules.profile.view.cell.FriendPrefGroupCell;

import java.util.List;

import butterknife.InjectView;
import butterknife.OnClick;

@Layout(R.layout.fragment_friend_preference)
public class FriendPreferenceFragment extends BaseFragmentWithArgs<FriendPreferencesPresenter, UserBundle> implements FriendPreferencesPresenter.View {

   @InjectView(R.id.recyclerViewGroups) EmptyRecyclerView recyclerViewGroups;
   BaseArrayListAdapter<FriendGroupRelation> adapter;

   private MaterialDialog blockingProgressDialog;

   @Override
   protected FriendPreferencesPresenter createPresenter(Bundle savedInstanceState) {
      return new FriendPreferencesPresenter(getArgs());
   }

   @Override
   public void afterCreateView(View rootView) {
      recyclerViewGroups.setLayoutManager(new LinearLayoutManager(getActivity()));
      adapter = new BaseArrayListAdapter<>(getActivity(), this);
      adapter.registerCell(FriendGroupRelation.class, FriendPrefGroupCell.class);
      recyclerViewGroups.setAdapter(adapter);
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
