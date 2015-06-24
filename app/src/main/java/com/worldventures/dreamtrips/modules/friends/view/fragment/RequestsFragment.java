package com.worldventures.dreamtrips.modules.friends.view.fragment;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ArrayAdapter;

import com.afollestad.materialdialogs.MaterialDialog;
import com.innahema.collections.query.functions.Action1;
import com.techery.spares.adapter.BaseArrayListAdapter;
import com.techery.spares.annotations.Layout;
import com.techery.spares.module.Injector;
import com.techery.spares.module.qualifier.ForActivity;
import com.techery.spares.ui.recycler.RecyclerViewStateDelegate;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragment;
import com.worldventures.dreamtrips.modules.friends.model.Circle;
import com.worldventures.dreamtrips.modules.friends.presenter.RequestsPresenter;
import com.worldventures.dreamtrips.modules.friends.view.cell.RequestCell;
import com.worldventures.dreamtrips.modules.friends.view.cell.RequestHeaderCell;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;

import butterknife.InjectView;

@Layout(R.layout.fragment_requests)
public class RequestsFragment extends BaseFragment<RequestsPresenter>
        implements RequestsPresenter.View, SwipeRefreshLayout.OnRefreshListener {

    @Inject
    @ForActivity
    Provider<Injector> injectorProvider;

    @InjectView(R.id.requests)
    RecyclerView recyclerView;
    @InjectView(R.id.swipe_container)
    SwipeRefreshLayout refreshLayout;

    RecyclerViewStateDelegate stateDelegate;
    BaseArrayListAdapter<Object> adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        stateDelegate.setRecyclerView(recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        adapter = new BaseArrayListAdapter<>(getActivity(), injectorProvider);
        adapter.registerCell(User.class, RequestCell.class);
        adapter.registerCell(String.class, RequestHeaderCell.class);

        recyclerView.setAdapter(adapter);
        refreshLayout.setOnRefreshListener(this);
        refreshLayout.setColorSchemeResources(R.color.theme_main_darker);
    }

    @Override
    protected RequestsPresenter createPresenter(Bundle savedInstanceState) {
        return new RequestsPresenter();
    }

    @Override
    public void onRefresh() {
        getPresenter().reloadRequests();
    }

    @Override
    public BaseArrayListAdapter<Object> getAdapter() {
        return adapter;
    }

    @Override
    public void startLoading() {
        refreshLayout.post(() -> refreshLayout.setRefreshing(true));
    }

    @Override
    public void finishLoading() {
        refreshLayout.setRefreshing(false);
        stateDelegate.restoreStateIfNeeded();
    }

    @Override
    public void showAddFriendDialog(List<Circle> circles, Action1<Integer> selectedAction) {
        MaterialDialog.Builder builder = new MaterialDialog.Builder(getActivity());
        builder.title(getString(R.string.friend_add_to))
                .adapter(new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, circles),
                        (materialDialog, view, i, charSequence) -> {
                            selectedAction.apply(i);
                            materialDialog.dismiss();
                        })
                .negativeText(R.string.cancel)
                .show();

    }

}
