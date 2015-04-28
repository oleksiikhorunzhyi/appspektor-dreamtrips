package com.worldventures.dreamtrips.modules.membership.view;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Spinner;

import com.techery.spares.adapter.BaseArrayListAdapter;
import com.techery.spares.annotations.Layout;
import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragment;
import com.worldventures.dreamtrips.modules.membership.model.Member;
import com.worldventures.dreamtrips.modules.membership.presenter.InvitePresenter;
import com.worldventures.dreamtrips.modules.membership.request.PhoneContactRequest;
import com.worldventures.dreamtrips.modules.membership.view.adapter.SimpleImageArrayAdapter;
import com.worldventures.dreamtrips.modules.membership.view.cell.MemberCell;
import com.worldventures.dreamtrips.modules.membership.view.cell.MemberCellSelectAll;

import java.util.List;

import butterknife.InjectView;
import butterknife.OnClick;

@Layout(R.layout.fragment_invite)
public class InviteFragment extends BaseFragment<InvitePresenter> implements InvitePresenter.View, SwipeRefreshLayout.OnRefreshListener, AdapterView.OnItemSelectedListener {


    @InjectView(R.id.lv_users)
    RecyclerView lvUsers;
    @InjectView(R.id.spinner)
    Spinner spinner;
    @InjectView(R.id.iv_add_contact)
    ImageView ivAddContact;
    @InjectView(R.id.tv_search)
    SearchView tvSearch;
    @InjectView(R.id.swipe_container)
    protected SwipeRefreshLayout refreshLayout;
    protected BaseArrayListAdapter adapter;

    @Override
    public void afterCreateView(View rootView) {
        super.afterCreateView(rootView);
        lvUsers.setLayoutManager(new LinearLayoutManager(getActivity()));
        lvUsers.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));
        adapter = new BaseArrayListAdapter<>(getActivity(), (Injector) getActivity());
        adapter.registerCell(Member.class, MemberCell.class);
        adapter.registerCell(Object.class, MemberCellSelectAll.class);

        lvUsers.setAdapter(adapter);

        SimpleImageArrayAdapter adapter = new SimpleImageArrayAdapter(getActivity(), new Integer[]{R.drawable.ic_invite_mail, R.drawable.ic_invite_phone});
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
        this.refreshLayout.setOnRefreshListener(this);
        this.refreshLayout.setColorSchemeResources(R.color.theme_main_darker);
    }


    @OnClick(R.id.iv_add_contact)
    public void addContact() {
        new AddContactDialog(getActivity()).show(member -> getPresenter().onMemberAdded(member));
    }

    @Override
    protected InvitePresenter createPresenter(Bundle savedInstanceState) {
        return new InvitePresenter(this);
    }

    @Override
    public void addItems(List<Member> memberList) {
        adapter.clear();
        adapter.addItem(new Object());
        adapter.addItems(memberList);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void startLoading() {
        refreshLayout.setRefreshing(true);
    }

    @Override
    public void finishLoading() {
        refreshLayout.setRefreshing(false);
    }

    @Override
    public int getSelectedType() {
        int pos = spinner.getSelectedItemPosition();
        return pos == 0 ? PhoneContactRequest.EMAIL : PhoneContactRequest.SMS;
    }

    @Override
    public List<Member> getItems() {
        return adapter.getItems();
    }

    @Override
    public void notifyAdapter() {
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onRefresh() {
        getPresenter().reload();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        getPresenter().reload();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void addItem(Member member) {
        adapter.addItem(1, member);
        adapter.notifyItemInserted(1);
    }
}
