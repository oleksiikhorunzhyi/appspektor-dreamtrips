package com.worldventures.dreamtrips.social.ui.friends.view.fragment;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;

import com.afollestad.materialdialogs.MaterialDialog;
import com.h6ah4i.android.widget.advrecyclerview.decoration.SimpleListDividerDecorator;
import com.worldventures.core.model.Circle;
import com.worldventures.core.model.User;
import com.worldventures.core.ui.annotations.Layout;
import com.worldventures.core.ui.annotations.MenuResource;
import com.worldventures.core.ui.util.StatePaginatedRecyclerViewManager;
import com.worldventures.core.ui.util.ViewUtils;
import com.worldventures.core.ui.view.adapter.BaseArrayListAdapter;
import com.worldventures.core.ui.view.adapter.BaseDelegateAdapter;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.module.FragmentClassProviderModule;
import com.worldventures.dreamtrips.core.navigation.ToolbarConfig;
import com.worldventures.dreamtrips.core.navigation.creator.FragmentClassProvider;
import com.worldventures.dreamtrips.core.navigation.router.NavigationConfigBuilder;
import com.worldventures.dreamtrips.modules.common.view.adapter.BaseDiffUtilCallback;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragment;
import com.worldventures.dreamtrips.social.service.users.base.model.AcceptanceHeaderModel;
import com.worldventures.dreamtrips.social.service.users.base.model.RequestHeaderModel;
import com.worldventures.dreamtrips.social.ui.friends.presenter.RequestsPresenter;
import com.worldventures.dreamtrips.social.ui.friends.view.cell.AcceptanceHeaderCell;
import com.worldventures.dreamtrips.social.ui.friends.view.cell.RequestCell;
import com.worldventures.dreamtrips.social.ui.friends.view.cell.RequestHeaderCell;
import com.worldventures.dreamtrips.social.ui.friends.view.cell.delegate.RequestCellDelegate;
import com.worldventures.dreamtrips.social.ui.friends.view.cell.delegate.RequestHeaderCellDelegate;
import com.worldventures.dreamtrips.social.ui.profile.bundle.UserBundle;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

@Layout(R.layout.fragment_requests)
@MenuResource(R.menu.menu_friend)
public class RequestsFragment extends BaseFragment<RequestsPresenter> implements RequestsPresenter.View,
      SwipeRefreshLayout.OnRefreshListener, RequestCellDelegate {

   @Inject @Named(FragmentClassProviderModule.PROFILE) FragmentClassProvider<Integer> fragmentClassProvider;

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
            getPresenter().onAddFriendsPressed();
            router.moveTo(FriendSearchFragment.class, NavigationConfigBuilder.forActivity().build());
            break;
         default:
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

      statePaginatedRecyclerViewManager = new StatePaginatedRecyclerViewManager(rootView.findViewById(R.id.recyclerView),
            rootView.findViewById(R.id.swipe_container));
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
      if (blockingProgressDialog != null) {
         blockingProgressDialog.dismiss();
      }
   }

   @Override
   public void openUser(UserBundle userBundle) {
      if (isVisibleOnScreen()) {
         router.moveTo(fragmentClassProvider.provideFragmentClass(userBundle.getUser()
               .getId()), NavigationConfigBuilder.forActivity().toolbarConfig(ToolbarConfig.Builder.create()
               .visible(false)
               .build()).data(userBundle).build());
      }
   }

   @Override
   public void showAddFriendDialog(@NotNull List<? extends Circle> circles, @NotNull Function1<? super Circle, Unit> selectAction) {
      MaterialDialog.Builder builder = new MaterialDialog.Builder(getActivity());
      builder.title(getString(R.string.friend_add_to))
            .adapter(new ArrayAdapter<>(getActivity(), R.layout.simple_list_item_circle, circles), (materialDialog, view, i, charSequence) -> {
               selectAction.invoke(circles.get(i));
               materialDialog.dismiss();
            })
            .negativeText(R.string.action_cancel)
            .show();
   }

   @SuppressWarnings("unchecked")
   @Override
   public void itemsLoaded(@NotNull List<?> sortedItems, boolean noMoreElements) {
      DiffUtil.DiffResult result = DiffUtil.calculateDiff(new BaseDiffUtilCallback(getAdapter().getItems(),
            sortedItems));
      getAdapter().setItemsNoNotify((List<Object>) sortedItems);
      result.dispatchUpdatesTo(getAdapter());
      statePaginatedRecyclerViewManager.updateLoadingStatus(false, noMoreElements);
   }

   @Override
   public void acceptRequest(User user) {
      getPresenter().acceptRequest(user.copy());
   }

   @Override
   public void rejectRequest(User user) {
      getPresenter().rejectRequest(user.copy());
   }

   @Override
   public void hideRequest(User user) {
      getPresenter().hideRequest(user.copy());
   }

   @Override
   public void cancelRequest(User user) {
      getPresenter().cancelRequest(user.copy());
   }

   @Override
   public void userClicked(User user) {
      getPresenter().userClicked(user.copy());
   }

   @Override
   public void onCellClicked(User model) { }

   @Override
   public void notifyItemsStateChanged() {
      adapter.notifyItemRangeChanged(0, getAdapter().getItemCount());
   }
}
