package com.worldventures.dreamtrips.modules.friends.view.fragment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.techery.spares.adapter.BaseArrayListAdapter;
import com.techery.spares.annotations.Layout;
import com.techery.spares.module.Injector;
import com.techery.spares.module.qualifier.ForActivity;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.view.custom.EmptyRecyclerView;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragmentWithArgs;
import com.worldventures.dreamtrips.modules.friends.presenter.FriendPreferencesPresenter;
import com.worldventures.dreamtrips.modules.profile.FriendGroupRelation;
import com.worldventures.dreamtrips.modules.profile.bundle.UserBundle;
import com.worldventures.dreamtrips.modules.profile.view.cell.FriendPrefGroupCell;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;

import butterknife.InjectView;
import butterknife.OnClick;

@Layout(R.layout.fragment_friend_preference)
public class FriendPreferenceFragment extends BaseFragmentWithArgs<FriendPreferencesPresenter, UserBundle>
        implements FriendPreferencesPresenter.View {

    @InjectView(R.id.recyclerViewGroups)
    EmptyRecyclerView recyclerViewGroups;
    BaseArrayListAdapter<FriendGroupRelation> adapter;

    @Inject
    @ForActivity
    Provider<Injector> injectorProvider;

    @Override
    protected FriendPreferencesPresenter createPresenter(Bundle savedInstanceState) {
        return new FriendPreferencesPresenter(getArgs());
    }

    @Override
    public void afterCreateView(View rootView) {
        recyclerViewGroups.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new BaseArrayListAdapter<>(getActivity(), injectorProvider);
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

}
