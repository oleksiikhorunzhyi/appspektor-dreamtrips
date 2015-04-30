package com.worldventures.dreamtrips.modules.membership.view.fragment;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Spinner;

import com.techery.spares.annotations.Layout;
import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.view.adapter.FilterableArrayListAdapter;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragment;
import com.worldventures.dreamtrips.modules.membership.event.MemberCellSelectAllRequestEvent;
import com.worldventures.dreamtrips.modules.membership.event.MemberCellSelectedEvent;
import com.worldventures.dreamtrips.modules.membership.model.InviteTemplate;
import com.worldventures.dreamtrips.modules.membership.model.Member;
import com.worldventures.dreamtrips.modules.membership.presenter.InvitePresenter;
import com.worldventures.dreamtrips.modules.membership.view.adapter.SimpleImageArrayAdapter;
import com.worldventures.dreamtrips.modules.membership.view.cell.MemberCell;
import com.worldventures.dreamtrips.modules.membership.view.dialog.AddContactDialog;
import com.worldventures.dreamtrips.modules.membership.view.util.DividerItemDecoration;

import java.util.List;

import butterknife.InjectView;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

@Layout(R.layout.fragment_invite)
public class InviteFragment
        extends BaseFragment<InvitePresenter>
        implements InvitePresenter.View, SwipeRefreshLayout.OnRefreshListener,
        SearchView.OnQueryTextListener, AdapterView.OnItemSelectedListener {


    @InjectView(R.id.lv_users)
    RecyclerView lvUsers;
    @InjectView(R.id.spinner)
    Spinner spinner;
    @InjectView(R.id.iv_add_contact)
    ImageView ivAddContact;
    @InjectView(R.id.tv_search)
    SearchView tvSearch;
    @InjectView(R.id.bt_continue)
    View llContinue;
    @InjectView(R.id.swipe_container)
    SwipeRefreshLayout refreshLayout;

    FilterableArrayListAdapter<Member> adapter;

    @Override
    protected InvitePresenter createPresenter(Bundle savedInstanceState) {
        return new InvitePresenter(this);
    }

    @Override
    public void afterCreateView(View rootView) {
        super.afterCreateView(rootView);
        lvUsers.setLayoutManager(new LinearLayoutManager(getActivity()));
        lvUsers.addItemDecoration(new DividerItemDecoration(getActivity(),
                DividerItemDecoration.VERTICAL_LIST));
        adapter = new FilterableArrayListAdapter<>(getActivity(), (Injector) getActivity());
        adapter.registerCell(Member.class, MemberCell.class);

        lvUsers.setAdapter(adapter);

        this.refreshLayout.setOnRefreshListener(this);
        this.refreshLayout.setColorSchemeResources(R.color.theme_main_darker);
        lvUsers.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                int firstPos = ((LinearLayoutManager) lvUsers.getLayoutManager()).findFirstCompletelyVisibleItemPosition();
                refreshLayout.setEnabled(firstPos == 0);
            }
        });

        SimpleImageArrayAdapter adapter = new SimpleImageArrayAdapter(getActivity(),
                new Integer[]{R.drawable.ic_invite_mail, R.drawable.ic_invite_phone});
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
        tvSearch.setOnQueryTextListener(this);
        llContinue.setVisibility(View.GONE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        lvUsers.setAdapter(null);
    }

    @Override
    public void startLoading() {
        refreshLayout.post(() -> refreshLayout.setRefreshing(true));
    }

    @Override
    public void finishLoading() {
        refreshLayout.post(() -> refreshLayout.setRefreshing(false));
    }

    @Override
    public void onRefresh() {
        getPresenter().loadMembers();
    }

    @OnClick(R.id.iv_add_contact)
    public void addContact() {
        new AddContactDialog(getActivity()).show(getPresenter()::addMember);
    }

    @Override
    public int getSelectedType() {
        int pos = spinner.getSelectedItemPosition();
        return pos;
    }

    @OnCheckedChanged(R.id.cb_select_all)
    public void onCheckedChanged(CompoundButton cb, boolean checked) {
        cb.setText(checked ? R.string.invitation_select_all : R.string.invitation_unselect_all);
        getEventBus().post(new MemberCellSelectAllRequestEvent(checked));
        getEventBus().post(new MemberCellSelectedEvent(checked));
    }

    @Override
    public void setMembers(List<Member> memberList) {
        adapter.setItems(memberList);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        getPresenter().loadMembers();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public boolean onQueryTextChange(String newText) {
        adapter.setFilter(newText);
        return false;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        // adapter already has items filtered, nothing to do
        return false;
    }

    @OnClick(R.id.bt_continue)
    public void continueAction() {
        getPresenter().continueAction();
    }

    @Override
    public void showNextStepButtonVisibility(boolean isVisible) {
        llContinue.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }
}
