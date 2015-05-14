package com.worldventures.dreamtrips.modules.membership.view.fragment;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.techery.spares.annotations.Layout;
import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.view.adapter.FilterableArrayListAdapter;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragment;
import com.worldventures.dreamtrips.modules.membership.model.Member;
import com.worldventures.dreamtrips.modules.membership.presenter.InvitePresenter;
import com.worldventures.dreamtrips.modules.membership.view.adapter.SimpleImageArrayAdapter;
import com.worldventures.dreamtrips.modules.membership.view.cell.MemberCell;
import com.worldventures.dreamtrips.modules.membership.view.dialog.AddContactDialog;
import com.worldventures.dreamtrips.modules.membership.view.util.DividerItemDecoration;

import java.util.Comparator;
import java.util.List;

import butterknife.InjectView;
import butterknife.OnClick;

@Layout(R.layout.fragment_invite)
public class InviteFragment
        extends BaseFragment<InvitePresenter>
        implements InvitePresenter.View, SwipeRefreshLayout.OnRefreshListener,
        SearchView.OnQueryTextListener, AdapterView.OnItemSelectedListener {

    @InjectView(R.id.frameContactCount)
    LinearLayout frameContactCount;
    @InjectView(R.id.lv_users)
    RecyclerView lvUsers;
    @InjectView(R.id.spinner)
    Spinner spinner;
    @InjectView(R.id.iv_add_contact)
    ImageView ivAddContact;
    @InjectView(R.id.tv_search)
    SearchView tvSearch;
    @InjectView(R.id.swipe_container)
    SwipeRefreshLayout refreshLayout;
    @InjectView(R.id.container_templates)
    FrameLayout containerTemplates;
    @InjectView(R.id.bt_continue)
    Button buttonContinue;
    @InjectView(R.id.textViewContactCount)
    TextView textViewSelectedCount;

    FilterableArrayListAdapter<Member> adapter;

    @Override
    protected InvitePresenter createPresenter(Bundle savedInstanceState) {
        return new InvitePresenter(this);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setUpView();
    }

    private void setUpView() {
        if (isTabletLandscape()) {
            containerTemplates.setVisibility(View.VISIBLE);
            buttonContinue.setVisibility(View.GONE);
            getPresenter().continueAction();
        } else {
            containerTemplates.setVisibility(View.GONE);
            if (!tvSearch.hasFocus()) buttonContinue.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void afterCreateView(View rootView) {
        super.afterCreateView(rootView);
        setUpView();
        lvUsers.setLayoutManager(new LinearLayoutManager(getActivity()));
        lvUsers.addItemDecoration(new DividerItemDecoration(getActivity(),
                DividerItemDecoration.VERTICAL_LIST));
        adapter = new FilterableArrayListAdapter<>(getActivity(), (Injector) getActivity());
        adapter.registerCell(Member.class, MemberCell.class);

        lvUsers.setAdapter(adapter);

        refreshLayout.setOnRefreshListener(this);
        refreshLayout.setColorSchemeResources(R.color.theme_main_darker);
        lvUsers.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                int firstPos = ((LinearLayoutManager) lvUsers.getLayoutManager()).findFirstCompletelyVisibleItemPosition();
                refreshLayout.setEnabled(firstPos == 0);
                tvSearch.clearFocus();
            }
        });

        SimpleImageArrayAdapter adapter = new SimpleImageArrayAdapter(getActivity(),
                new Integer[]{R.drawable.ic_invite_mail, R.drawable.ic_invite_phone});
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        tvSearch.setOnQueryTextListener(this);
        tvSearch.clearFocus();
        tvSearch.setIconifiedByDefault(false);

        setSelectedCount(0);
        tvSearch.setOnQueryTextFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                buttonContinue.setVisibility(View.GONE);
            } else {
                getPresenter().searchHiden();
            }

            getPresenter().searchToggle(hasFocus);
        });

        buttonContinue.setVisibility(View.GONE);
    }

    @OnClick(R.id.textViewDeselectAll)
    public void deselectOnClick() {
        getPresenter().deselectAll();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        lvUsers.setAdapter(null);
    }

    @Override
    public void setSelectedCount(int count) {
        textViewSelectedCount.setText(String.format(getString(R.string.selected), count));
    }

    @Override
    public void showContinue() {
        buttonContinue.postDelayed(() -> buttonContinue.setVisibility(View.VISIBLE), 500l);
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
        return spinner.getSelectedItemPosition();
    }

    @Override
    public void setMembers(List<Member> memberList) {
        adapter.setItems(memberList);
    }

    @Override
    public void moved() {
        lvUsers.scrollToPosition(0);
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
        getPresenter().onFilter(newText);
        return false;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        tvSearch.clearFocus();
        // adapter already has items filtered, nothing to do
        return false;
    }

    @Override
    public void setFilter(String newText) {
        adapter.setFilter(newText);
    }

    @Override
    public void sort(Comparator comparator) {
        adapter.sort(comparator);
    }

    @OnClick(R.id.bt_continue)
    public void continueAction() {
        getPresenter().continueAction();
    }

    @Override
    public void showNextStepButtonVisibility(boolean isVisible) {
        int visibility = isVisible ? View.VISIBLE : View.GONE;
        frameContactCount.setVisibility(visibility);
        if (!tvSearch.hasFocus()) buttonContinue.setVisibility(visibility);
    }
}
