package com.worldventures.dreamtrips.social.ui.friends.view.fragment;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.h6ah4i.android.widget.advrecyclerview.decoration.SimpleListDividerDecorator;
import com.worldventures.core.model.Circle;
import com.worldventures.core.model.User;
import com.worldventures.core.ui.util.StatePaginatedRecyclerViewManager;
import com.worldventures.core.ui.util.ViewUtils;
import com.worldventures.core.ui.view.adapter.BaseDelegateAdapter;
import com.worldventures.core.ui.view.recycler.StateRecyclerView;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.module.FragmentClassProviderModule;
import com.worldventures.dreamtrips.core.navigation.ToolbarConfig;
import com.worldventures.dreamtrips.core.navigation.creator.FragmentClassProvider;
import com.worldventures.dreamtrips.core.navigation.router.NavigationConfigBuilder;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragmentWithArgs;
import com.worldventures.dreamtrips.social.ui.friends.bundle.BaseUsersBundle;
import com.worldventures.dreamtrips.social.ui.friends.presenter.BaseUserListPresenter;
import com.worldventures.dreamtrips.social.ui.friends.view.cell.FriendCell;
import com.worldventures.dreamtrips.social.ui.friends.view.cell.delegate.UserActionDelegate;
import com.worldventures.dreamtrips.social.ui.profile.bundle.UserBundle;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import butterknife.InjectView;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public abstract class BaseUsersFragment<T extends BaseUserListPresenter, B extends BaseUsersBundle> extends BaseFragmentWithArgs<T, B>
      implements BaseUserListPresenter.View, SwipeRefreshLayout.OnRefreshListener, UserActionDelegate {

   @InjectView(R.id.empty) protected RelativeLayout emptyView;
   @InjectView(R.id.recyclerViewFriends) protected StateRecyclerView recyclerView;
   @InjectView(R.id.swipe_container) protected SwipeRefreshLayout refreshLayout;
   @InjectView(R.id.caption) protected TextView caption;

   @Inject @Named(FragmentClassProviderModule.PROFILE) FragmentClassProvider<Integer> fragmentClassProvider;

   protected BaseDelegateAdapter<User> adapter;
   private MaterialDialog blockingProgressDialog;

   private StatePaginatedRecyclerViewManager statePaginatedRecyclerViewManager;
   private Bundle saveInstanceState;

   @Override
   public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      this.saveInstanceState = savedInstanceState;
   }

   @Override
   public void afterCreateView(View rootView) {
      super.afterCreateView(rootView);
      adapter = new BaseDelegateAdapter<>(getActivity(), this);
      adapter.registerCell(User.class, FriendCell.class);

      recyclerView.setEmptyView(emptyView);
      recyclerView.setAdapter(adapter);

      statePaginatedRecyclerViewManager = new StatePaginatedRecyclerViewManager(recyclerView, refreshLayout);
      statePaginatedRecyclerViewManager.init(adapter, saveInstanceState, createLayoutManager());
      statePaginatedRecyclerViewManager.setOnRefreshListener(this);
      statePaginatedRecyclerViewManager.setPaginationListener(() -> getPresenter().loadNext());

      refreshLayout.setColorSchemeResources(R.color.theme_main_darker);
      if (!ViewUtils.isLandscapeOrientation(getActivity())) {
         statePaginatedRecyclerViewManager.addItemDecoration(new SimpleListDividerDecorator(getResources().getDrawable(R.drawable.list_divider), true));
      }

   }

   protected LinearLayoutManager createLayoutManager() {
      return ViewUtils.isLandscapeOrientation(getActivity()) ? new GridLayoutManager(getActivity(), ViewUtils.isTablet(getActivity()) ? 3 : 1) : new LinearLayoutManager(getActivity());
   }

   @Override
   public void onDestroyView() {
      super.onDestroyView();
   }

   @Override
   public void onRefresh() {
      getPresenter().reload();
   }

   @Override
   public void startLoading() {
      statePaginatedRecyclerViewManager.startLoading();
   }

   @Override
   public void finishLoading() {
      statePaginatedRecyclerViewManager.finishLoading();
      recyclerView.restoreStateIfNeeded();
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

   @SuppressWarnings("unchecked")
   @Override
   public void refreshUsers(@Nullable List<? extends User> users, boolean noMoreItems) {
      DiffUtil.DiffResult result = DiffUtil.calculateDiff(new UsersDiffUtilCallback(adapter.getItems(), (List<User>) users));
      adapter.setItemsNoNotify((List<User>) users);
      result.dispatchUpdatesTo(adapter);
      statePaginatedRecyclerViewManager.updateLoadingStatus(false, noMoreItems);
      recyclerView.checkIfEmpty();
   }

   @Override
   public void openFriendPrefs(UserBundle userBundle) {
      router.moveTo(FriendPreferenceFragment.class, NavigationConfigBuilder.forActivity()
            .data(userBundle)
            .build());
   }

   @Override
   public void showAddFriendDialog(@Nullable List<? extends Circle> circles, @NotNull Function1<? super Circle, Unit> selectAction) {
      MaterialDialog.Builder builder = new MaterialDialog.Builder(getActivity());
      builder.title(getString(R.string.profile_add_friend))
            .dismissListener((dialogInterface) -> adapter.notifyItemRangeChanged(0, adapter.getItemCount()))
            .adapter(new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, circles), (materialDialog, view, i, charSequence) -> {
               selectAction.invoke(circles.get(i));
               materialDialog.dismiss();
            }).negativeText(R.string.action_cancel)
            .show();
   }

   @Override
   public void openUser(UserBundle userBundle) {
      router.moveTo(fragmentClassProvider.provideFragmentClass(userBundle.getUser()
            .getId()), NavigationConfigBuilder.forActivity().toolbarConfig(ToolbarConfig.Builder.create()
            .visible(false)
            .build()).data(userBundle).build());
   }

   @Override
   public void userClicked(User user) {
      getPresenter().userClicked(user);
   }

   @Override
   public void onCellClicked(User model) {

   }

   @Override
   protected abstract T createPresenter(Bundle savedInstanceState);

   public class UsersDiffUtilCallback extends DiffUtil.Callback {

      protected List<User> oldUsers;
      protected List<User> newUsers;

      public UsersDiffUtilCallback(List<User> oldUsers, List<User> newUsers) {
         this.oldUsers = oldUsers;
         this.newUsers = newUsers;
      }

      @Override
      public int getOldListSize() {
         return oldUsers.size();
      }

      @Override
      public int getNewListSize() {
         return newUsers.size();
      }

      @Override
      public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
         return oldUsers.get(oldItemPosition).equals(newUsers.get(newItemPosition));
      }

      @Override
      public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
         User oldUser = oldUsers.get(oldItemPosition);
         User newUser = newUsers.get(newItemPosition);
         return oldUser.getRelationship() == newUser.getRelationship()
               && oldUser.getCircles().equals(newUser.getCircles());
      }

   }
}

