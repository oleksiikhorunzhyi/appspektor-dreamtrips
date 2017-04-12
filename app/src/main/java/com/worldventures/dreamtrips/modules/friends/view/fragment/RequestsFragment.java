package com.worldventures.dreamtrips.modules.friends.view.fragment;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;

import com.afollestad.materialdialogs.MaterialDialog;
import com.h6ah4i.android.widget.advrecyclerview.decoration.SimpleListDividerDecorator;
import com.innahema.collections.query.functions.Action1;
import com.techery.spares.adapter.BaseArrayListAdapter;
import com.techery.spares.adapter.BaseDelegateAdapter;
import com.techery.spares.annotations.Layout;
import com.techery.spares.annotations.MenuResource;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.module.RouteCreatorModule;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.navigation.ToolbarConfig;
import com.worldventures.dreamtrips.core.navigation.creator.RouteCreator;
import com.worldventures.dreamtrips.core.navigation.router.NavigationConfigBuilder;
import com.worldventures.dreamtrips.core.utils.ViewUtils;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragment;
import com.worldventures.dreamtrips.modules.feed.view.util.StatePaginatedRecyclerViewManager;
import com.worldventures.dreamtrips.modules.friends.model.AcceptanceHeaderModel;
import com.worldventures.dreamtrips.modules.friends.model.Circle;
import com.worldventures.dreamtrips.modules.friends.model.RequestHeaderModel;
import com.worldventures.dreamtrips.modules.friends.presenter.RequestsPresenter;
import com.worldventures.dreamtrips.modules.friends.view.cell.AcceptanceHeaderCell;
import com.worldventures.dreamtrips.modules.friends.view.cell.RequestCell;
import com.worldventures.dreamtrips.modules.friends.view.cell.RequestHeaderCell;
import com.worldventures.dreamtrips.modules.friends.view.cell.delegate.RequestCellDelegate;
import com.worldventures.dreamtrips.modules.friends.view.cell.delegate.RequestHeaderCellDelegate;
import com.worldventures.dreamtrips.modules.profile.bundle.UserBundle;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

@Layout(R.layout.fragment_requests)
@MenuResource(R.menu.menu_friend)
public class RequestsFragment extends BaseFragment<RequestsPresenter> implements RequestsPresenter.View,
      SwipeRefreshLayout.OnRefreshListener, RequestCellDelegate {

   @Inject @Named(RouteCreatorModule.PROFILE) RouteCreator<Integer> routeCreator;

   private StatePaginatedRecyclerViewManager statePaginatedRecyclerViewManager;
   private Bundle savedInstanceState;
   private BaseDelegateAdapter<Object> adapter;
   private MaterialDialog blockingProgressDialog;

   @Override
   public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      this.savedInstanceState = savedInstanceState;
   }

   @Override
   public boolean onOptionsItemSelected(MenuItem item) {
      switch (item.getItemId()) {
         case R.id.add_friend:
            TrackingHelper.tapFeedButton(TrackingHelper.ATTRIBUTE_ADD_FRIENDS);
            TrackingHelper.tapFeedButton(TrackingHelper.ATTRIBUTE_SEARCH_FRIENDS);
            router.moveTo(Route.FRIEND_SEARCH, NavigationConfigBuilder.forActivity().build());
            break;
      }
      return super.onOptionsItemSelected(item);
   }

   @Override
   public void afterCreateView(View rootView) {
      super.afterCreateView(rootView);
      adapter = new BaseDelegateAdapter<>(getActivity(), this);
      adapter.registerCell(User.class, RequestCell.class);
      adapter.registerCell(RequestHeaderModel.class, RequestHeaderCell.class);
      adapter.registerCell(AcceptanceHeaderModel.class, AcceptanceHeaderCell.class);
      adapter.registerDelegate(User.class, this);
      adapter.registerDelegate(RequestHeaderModel.class, new RequestHeaderCellDelegate() {
         @Override
         public void acceptAllRequests() {
            getPresenter().acceptAllRequests();
         }

         @Override
         public void onCellClicked(RequestHeaderModel model) { }
      });

      statePaginatedRecyclerViewManager = new StatePaginatedRecyclerViewManager(rootView);
      statePaginatedRecyclerViewManager.init(adapter, savedInstanceState, getLayoutManager());
      statePaginatedRecyclerViewManager.setOnRefreshListener(this);
      statePaginatedRecyclerViewManager.setPaginationListener(() -> getPresenter().loadNext());
      statePaginatedRecyclerViewManager.addItemDecoration(
            new SimpleListDividerDecorator(getResources().getDrawable(R.drawable.list_divider), true));
   }

   private LinearLayoutManager getLayoutManager() {
      if (ViewUtils.isLandscapeOrientation(getActivity())) {
         int spanCount = ViewUtils.isTablet(getActivity()) ? 3 : 1;
         GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), spanCount);
         gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
               return adapter.getItem(position) instanceof RequestHeaderModel ? spanCount : 1;
            }
         });
         return gridLayoutManager;
      } else {
         return new LinearLayoutManager(getActivity());
      }
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
   public void finishLoading() {
      statePaginatedRecyclerViewManager.finishLoading();
   }

   @Override
   public void startLoading() {
      statePaginatedRecyclerViewManager.startLoading();
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

   @Override
   public void openUser(UserBundle userBundle) {
      if (isVisibleOnScreen()) router.moveTo(routeCreator.createRoute(userBundle.getUser()
            .getId()), NavigationConfigBuilder.forActivity().toolbarConfig(ToolbarConfig.Builder.create()
            .visible(false)
            .build()).data(userBundle).build());
   }

   @Override
   public void showAddFriendDialog(List<Circle> circles, Action1<Integer> selectedAction) {
      MaterialDialog.Builder builder = new MaterialDialog.Builder(getActivity());
      builder.title(getString(R.string.friend_add_to))
            .adapter(new ArrayAdapter<>(getActivity(), R.layout.simple_list_item_circle, circles), (materialDialog, view, i, charSequence) -> {
               selectedAction.apply(i);
               materialDialog.dismiss();
            })
            .negativeText(R.string.action_cancel)
            .show();
   }

   @Override
   public void itemsLoaded(List<Object> sortedItems, boolean noMoreElements) {
      getAdapter().setItems(sortedItems);
      statePaginatedRecyclerViewManager.updateLoadingStatus(false, noMoreElements);
   }

   @Override
   public void acceptRequest(User user) {
      getPresenter().acceptRequest(user);
   }

   @Override
   public void rejectRequest(User user) {
      getPresenter().rejectRequest(user);
   }

   @Override
   public void hideRequest(User user) {
      getPresenter().hideRequest(user);
   }

   @Override
   public void cancelRequest(User user) {
      getPresenter().cancelRequest(user);
   }

   @Override
   public void userClicked(User user) {
      getPresenter().userClicked(user);
   }

   @Override
   public void onCellClicked(User model) { }
}
