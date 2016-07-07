package com.worldventures.dreamtrips.modules.membership.view.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
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

import com.badoo.mobile.util.WeakHandler;
import com.techery.spares.annotations.Layout;
import com.techery.spares.ui.recycler.RecyclerViewStateDelegate;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.navigation.router.NavigationConfigBuilder;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
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
    RecyclerViewStateDelegate stateDelegate;

    private boolean inhibitSpinner = true;
    private WeakHandler weakHandler;

    @Override
    protected InvitePresenter createPresenter(Bundle savedInstanceState) {
        return new InvitePresenter();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        weakHandler = new WeakHandler();
        stateDelegate = new RecyclerViewStateDelegate();
        stateDelegate.onCreate(savedInstanceState);
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        stateDelegate.saveStateIfNeeded(outState);
    }

    @Override
    public void afterCreateView(View rootView) {
        super.afterCreateView(rootView);
        stateDelegate.setRecyclerView(lvUsers);
        setUpView();
        lvUsers.setLayoutManager(new LinearLayoutManager(getActivity()));
        lvUsers.addItemDecoration(new DividerItemDecoration(getActivity(),
                DividerItemDecoration.VERTICAL_LIST));
        adapter = new FilterableArrayListAdapter<>(getActivity(), this);
        adapter.registerCell(Member.class, MemberCell.class);

        lvUsers.setAdapter(adapter);

        refreshLayout.setOnRefreshListener(this);
        refreshLayout.setColorSchemeResources(R.color.theme_main_darker);
        lvUsers.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (lvUsers != null) {
                    int firstPos = ((LinearLayoutManager) lvUsers.getLayoutManager())
                            .findFirstCompletelyVisibleItemPosition();
                    refreshLayout.setEnabled(firstPos == 0);
                    tvSearch.clearFocus();
                }
            }
        });

        SimpleImageArrayAdapter spinnerAdapter = new SimpleImageArrayAdapter(getActivity(),
                new Integer[]{R.drawable.ic_invite_mail, R.drawable.ic_invite_phone});
        spinner.setAdapter(spinnerAdapter);
        spinner.setOnItemSelectedListener(this);

        tvSearch.setOnQueryTextListener(this);
        tvSearch.clearFocus();
        tvSearch.setIconifiedByDefault(false);
        tvSearch.setOnClickListener(v -> TrackingHelper.searchRepTools(TrackingHelper.ACTION_REP_TOOLS_INVITE_SHARE));

        tvSearch.setOnQueryTextFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                buttonContinue.setVisibility(View.GONE);
            } else {
                getPresenter().showContinueBtnIfNeed();
            }

            getPresenter().searchToggle(hasFocus);
        });

        buttonContinue.setVisibility(View.GONE);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) getPresenter().track();
    }

    private void setUpView() {
        if (isTabletLandscape()) {
            containerTemplates.setVisibility(View.VISIBLE);
            buttonContinue.setVisibility(View.GONE);
        } else {
            containerTemplates.setVisibility(View.GONE);
            if (!tvSearch.hasFocus() && getPresenter().isVisible())
                buttonContinue.setVisibility(View.VISIBLE);
        }
    }

    @OnClick(R.id.textViewDeselectAll)
    public void deselectOnClick() {
        getPresenter().deselectAll();
    }

    @Override
    public void onDestroyView() {
        lvUsers.setAdapter(null);
        stateDelegate.onDestroyView();
        tvSearch.setOnQueryTextListener(null);
        tvSearch.setOnQueryTextFocusChangeListener(null);
        spinner.setOnItemSelectedListener(null);
        super.onDestroyView();
    }

    @Override
    public void setSelectedCount(int count) {
        textViewSelectedCount.setText(String.format(getString(R.string.selected), count));
    }

    @Override
    public void startLoading() {
        weakHandler.post(() -> {
            if (refreshLayout != null) refreshLayout.setRefreshing(true);
        });
    }

    @Override
    public void finishLoading() {
        weakHandler.post(() -> {
            if (refreshLayout != null) refreshLayout.setRefreshing(false);
        });
        stateDelegate.restoreStateIfNeeded();
    }

    @Override
    public void onRefresh() {
        getPresenter().loadMembers();
    }

    @OnClick(R.id.iv_add_contact)
    public void addContact() {
        TrackingHelper.actionRepToolsInviteShare(TrackingHelper.ATTRIBUTE_ADD_CONTACT);
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
    public void move(Member member, int to) {
        if (to > 0)
            lvUsers.scrollToPosition(0);
        //
        adapter.moveItemSafely(member, to);
    }

    @Override
    public void openTemplateView() {
        router.moveTo(Route.SELECT_INVITE_TEMPLATE, NavigationConfigBuilder.forFragment()
                .backStackEnabled(false)
                .fragmentManager(getChildFragmentManager())
                .containerId(R.id.container_templates)
                .build());
    }

    @Override
    public void continueAction2() {
        router.moveTo(Route.SELECT_INVITE_TEMPLATE, NavigationConfigBuilder.forRemoval()
                .fragmentManager(getChildFragmentManager())
                .containerId(R.id.container_templates)
                .build());
        if (isTabletLandscape()) {
            openTemplateView();
        } else {
            router.moveTo(Route.SELECT_INVITE_TEMPLATE, NavigationConfigBuilder.forActivity()
                    .build());
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (inhibitSpinner) {
            inhibitSpinner = false;
            return;
        }
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

    @Override
    public void setAdapterComparator(Comparator comparator) {
        adapter.setDefaultComparator(comparator);
    }

    @OnClick(R.id.bt_continue)
    public void continueAction() {
        getPresenter().continueAction();
    }

    @Override
    public void showNextStepButtonVisibility(boolean isVisible) {
        frameContactCount.setVisibility(isVisible ? View.VISIBLE : View.GONE);
        if (!tvSearch.hasFocus())
            buttonContinue.setVisibility(!isTabletLandscape() && isVisible ? View.VISIBLE : View.GONE);
    }
}
