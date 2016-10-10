package com.worldventures.dreamtrips.modules.feed.view.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.badoo.mobile.util.WeakHandler;
import com.h6ah4i.android.widget.advrecyclerview.decoration.SimpleListDividerDecorator;
import com.techery.spares.adapter.BaseDelegateAdapter;
import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.navigation.ToolbarConfig;
import com.worldventures.dreamtrips.core.navigation.router.NavigationConfigBuilder;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.common.view.custom.EmptyRecyclerView;
import com.worldventures.dreamtrips.modules.feed.bundle.CreateEntityBundle;
import com.worldventures.dreamtrips.modules.feed.presenter.FeedListAdditionalInfoPresenter;
import com.worldventures.dreamtrips.modules.feed.view.util.CirclesFilterPopupWindow;
import com.worldventures.dreamtrips.modules.feed.view.util.NestedLinearLayoutManager;
import com.worldventures.dreamtrips.modules.friends.bundle.FriendGlobalSearchBundle;
import com.worldventures.dreamtrips.modules.friends.model.Circle;
import com.worldventures.dreamtrips.modules.friends.view.cell.FeedFriendCell;
import com.worldventures.dreamtrips.modules.friends.view.cell.delegate.UserActionDelegate;
import com.worldventures.dreamtrips.modules.profile.bundle.UserBundle;
import com.worldventures.dreamtrips.modules.profile.view.ProfileViewUtils;

import java.util.List;

import butterknife.InjectView;
import butterknife.OnClick;

import static android.support.v7.widget.RecyclerView.OnScrollListener;

@Layout(R.layout.fragment_feed_list_additional_info)
public class FeedListAdditionalInfoFragment extends FeedItemAdditionalInfoFragment<FeedListAdditionalInfoPresenter>
      implements FeedListAdditionalInfoPresenter.View, UserActionDelegate {

   @InjectView(R.id.account_type) TextView accountType;
   @InjectView(R.id.dt_points) TextView dtPoints;
   @InjectView(R.id.rovia_bucks) TextView roviaBucks;
   @InjectView(R.id.details) ViewGroup details;
   @InjectView(R.id.friends_card) ViewGroup friendsCard;
   @InjectView(R.id.lv_close_friends) EmptyRecyclerView friendsView;
   @InjectView(R.id.circle_title) TextView circleTitle;
   @InjectView(R.id.feed_friend_empty_view) View emptyView;
   @InjectView(R.id.swipe_container) SwipeRefreshLayout refreshLayout;

   private CirclesFilterPopupWindow filterPopupWindow;
   private BaseDelegateAdapter<User> adapter;
   private MaterialDialog blockingProgressDialog;

   WeakHandler handler = new WeakHandler();

   @Override
   protected FeedListAdditionalInfoPresenter createPresenter(Bundle savedInstanceState) {
      return new FeedListAdditionalInfoPresenter(getArgs() == null ? null : getArgs().getUser());
   }

   @Override
   public void afterCreateView(View rootView) {
      super.afterCreateView(rootView);

      if (friendsView == null) return;
      //
      refreshLayout.setOnRefreshListener(() -> getPresenter().reload());
      refreshLayout.setColorSchemeResources(R.color.theme_main_darker);
      //
      friendsView.setEmptyView(emptyView);
      friendsView.addOnScrollListener(new OnScrollListener() {
         @Override
         public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            LinearLayoutManager layoutManager = ((LinearLayoutManager) recyclerView.getLayoutManager());

            int itemCount = recyclerView.getLayoutManager().getItemCount();
            int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();
            getPresenter().onScrolled(itemCount, lastVisibleItemPosition);

            boolean enableSwipeToRefresh = layoutManager.findFirstCompletelyVisibleItemPosition() == 0;
            refreshLayout.setEnabled(enableSwipeToRefresh);
         }
      });
      adapter = new BaseDelegateAdapter<>(getContext(), this);
      adapter.registerCell(User.class, FeedFriendCell.class);
      adapter.registerDelegate(User.class, this);
      friendsView.setAdapter(adapter);
      friendsView.setLayoutManager(new NestedLinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
      friendsView.addItemDecoration(new SimpleListDividerDecorator(ResourcesCompat.getDrawable(getResources(), R.drawable.list_divider, null), true));
   }

   @Override
   public void setFriends(@NonNull List<User> friends) {
      friendsCard.setVisibility(View.VISIBLE);
      adapter.clear();
      adapter.addItems(friends);
   }

   @Override
   public void addFriends(@NonNull List<User> friends) {
      adapter.addItems(friends);
   }

   @Override
   public void startLoading() {
      // timeout was set according to the issue:
      // https://code.google.com/p/android/issues/detail?id=77712
      handler.postDelayed(() -> {
         if (refreshLayout != null) refreshLayout.setRefreshing(true);
      }, 100);
   }

   @Override
   public void finishLoading() {
      handler.post(() -> {
         if (refreshLayout != null) refreshLayout.setRefreshing(false);
      });
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
   public void showCirclePicker(@NonNull List<Circle> circles, Circle activeCircle) {
      if (filterPopupWindow == null || filterPopupWindow.dismissPassed()) {
         filterPopupWindow = new CirclesFilterPopupWindow(circleTitle.getContext());
         filterPopupWindow.setCircles(circles);
         filterPopupWindow.setAnchorView(circleTitle);
         filterPopupWindow.setOnItemClickListener((parent, view, position, id) -> {
            filterPopupWindow.dismiss();
            getPresenter().onCirclePicked(circles.get(position));
            circleTitle.setText(circles.get(position).getName());
         });
         filterPopupWindow.show();
         filterPopupWindow.setCheckedCircle(activeCircle);
      }
   }

   @Override
   public void setCurrentCircle(Circle currentCircle) {
      circleTitle.setText(currentCircle.getName());
   }

   @Override
   public void openUser(UserBundle bundle) {
      router.moveTo(routeCreator.createRoute(bundle.getUser().getId()), NavigationConfigBuilder.forActivity()
            .toolbarConfig(ToolbarConfig.Builder.create().visible(false).build())
            .data(bundle)
            .build());
   }

   private void openPost() {
      router.moveTo(Route.POST_CREATE, NavigationConfigBuilder.forFragment()
            .backStackEnabled(false)
            .fragmentManager(getActivity().getSupportFragmentManager())
            .data(new CreateEntityBundle(false, CreateEntityBundle.Origin.FEED))
            .containerId(R.id.container_details_floating)
            .build());
   }

   private void openSharePhoto() {
      router.moveTo(Route.POST_CREATE, NavigationConfigBuilder.forRemoval()
            .containerId(R.id.container_details_floating)
            .fragmentManager(getActivity().getSupportFragmentManager())
            .build());
      router.moveTo(Route.POST_CREATE, NavigationConfigBuilder.forFragment()
            .backStackEnabled(false)
            .fragmentManager(getActivity().getSupportFragmentManager())
            .containerId(R.id.container_details_floating)
            .data(new CreateEntityBundle(true, CreateEntityBundle.Origin.FEED))
            .build());

   }

   protected void openSearch() {
      router.moveTo(Route.FRIEND_SEARCH, NavigationConfigBuilder.forActivity()
            .data(new FriendGlobalSearchBundle(""))
            .build());
   }

   @OnClick(R.id.share_post)
   protected void onPostClicked() {
      openPost();
   }

   @OnClick(R.id.share_photo)
   protected void onSharePhotoClick() {
      openSharePhoto();
   }

   @OnClick(R.id.global)
   protected void onGlobalSearchClicked() {
      openSearch();
   }

   @OnClick(R.id.circle_title)
   protected void onCircleFilterClicked() {
      getPresenter().onCircleFilterClicked();
   }

   @Override
   public void setupView(User user) {
      super.setupView(user);
      details.setVisibility(View.VISIBLE);
      viewProfile.setVisibility(View.GONE);
      accountType.setText(user.getCompany());
      dtPoints.setText(String.valueOf((int) user.getDreamTripsPoints()));
      roviaBucks.setText(df.format(user.getRoviaBucks()));

      ProfileViewUtils.setUserStatus(user, accountType, companyName.getResources());
   }

   @Override
   public void userClicked(User user) {
      getPresenter().userClicked(user);
   }

   @Override
   public void onCellClicked(User model) {

   }
}
