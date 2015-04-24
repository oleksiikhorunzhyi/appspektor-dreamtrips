package com.worldventures.dreamtrips.modules.membership.view;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.techery.spares.adapter.BaseArrayListAdapter;
import com.techery.spares.annotations.Layout;
import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragment;
import com.worldventures.dreamtrips.modules.membership.presenter.InvitePresenter;

import butterknife.InjectView;

@Layout(R.layout.fragment_invite)
public class InviteFragment extends BaseFragment<InvitePresenter> {

    @InjectView(R.id.subtoolbar)
    Toolbar subtoolbar;

    @InjectView(R.id.lv_users)
    RecyclerView lvUsers;

    @Override
    public void afterCreateView(View rootView) {
        super.afterCreateView(rootView);
        subtoolbar.setTitle("Select contacts");
        subtoolbar.inflateMenu(R.menu.menu_subinvite);
        lvUsers.setLayoutManager(new LinearLayoutManager(getActivity()));
        BaseArrayListAdapter<Object> adapter = new BaseArrayListAdapter<>(getActivity(), (Injector) getActivity());
        lvUsers.setAdapter(adapter);
    }

    @Override
    protected InvitePresenter createPresenter(Bundle savedInstanceState) {
        return new InvitePresenter(this);
    }
}
