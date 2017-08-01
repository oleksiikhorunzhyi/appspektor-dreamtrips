package com.worldventures.dreamtrips.modules.tripsimages.view.fragment.ysbh;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.techery.spares.adapter.BaseDelegateAdapter;
import com.techery.spares.annotations.Layout;
import com.techery.spares.ui.recycler.RecyclerViewStateDelegate;
import com.techery.spares.ui.view.cell.CellDelegate;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.navigation.ToolbarConfig;
import com.worldventures.dreamtrips.core.navigation.router.NavigationConfigBuilder;
import com.worldventures.dreamtrips.core.utils.ViewUtils;
import com.worldventures.dreamtrips.modules.common.view.adapter.BaseDiffUtilCallback;
import com.worldventures.dreamtrips.modules.common.view.custom.EmptyRecyclerView;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragment;
import com.worldventures.dreamtrips.modules.common.view.viewpager.SelectablePagerFragment;
import com.worldventures.dreamtrips.modules.tripsimages.presenter.ysbh.YouShouldBeHerePresenter;
import com.worldventures.dreamtrips.modules.tripsimages.view.args.YsbhPagerArgs;
import com.worldventures.dreamtrips.modules.tripsimages.view.cell.YsbhPhotoCell;
import com.worldventures.dreamtrips.modules.tripsimages.model.YSBHPhoto;

import java.util.List;

import butterknife.InjectView;

@Layout(R.layout.fragment_images_list)
public class YouShouldBeHereFragment extends BaseFragment<YouShouldBeHerePresenter>
      implements YouShouldBeHerePresenter.View, SelectablePagerFragment {

   @InjectView(R.id.recyclerView) EmptyRecyclerView recyclerView;
   @InjectView(R.id.swipeLayout) SwipeRefreshLayout refreshLayout;

   protected BaseDelegateAdapter adapter;
   protected GridLayoutManager layoutManager;
   private RecyclerViewStateDelegate stateDelegate;

   @Override
   public void onCreate(@Nullable Bundle savedInstanceState) {
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
      initAdapter();
      initRecyclerView();
      refreshLayout.setOnRefreshListener(() -> getPresenter().reload());
      refreshLayout.setColorSchemeResources(R.color.theme_main_darker);
   }

   private void initAdapter() {
      initLayoutManager(getSpanCount());
      stateDelegate.setRecyclerView(recyclerView);
      adapter = new BaseDelegateAdapter(getContext(), this);
      registerCellsAndDelegates();
      recyclerView.setAdapter(this.adapter);
   }

   protected void registerCellsAndDelegates() {
      CellDelegate<YSBHPhoto> delegate = getPresenter()::onItemClick;
      adapter.registerCell(YSBHPhoto.class, YsbhPhotoCell.class);
      adapter.registerDelegate(YSBHPhoto.class, delegate);
   }

   protected void initLayoutManager(int spanCount) {
      layoutManager = new GridLayoutManager(getActivity(), spanCount);
      recyclerView.setLayoutManager(layoutManager);
   }

   protected int getSpanCount() {
      boolean landscape = ViewUtils.isLandscapeOrientation(getActivity());
      return landscape ? 4 : ViewUtils.isTablet(getActivity()) ? 3 : 2;
   }

   private void initRecyclerView() {
      recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
         @Override
         public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            int visibleCount = recyclerView.getChildCount();
            int totalCount = layoutManager.getItemCount();
            int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();
            getPresenter().scrolled(visibleCount, totalCount, firstVisibleItemPosition);
         }
      });
   }

   @Override
   protected YouShouldBeHerePresenter createPresenter(Bundle savedInstanceState) {
      return new YouShouldBeHerePresenter();
   }


   @Override
   public void openFullscreen(List<YSBHPhoto> photos, boolean lastPageReached, int selectedItemIndex) {
      router.moveTo(Route.YSBH_PAGER_IMAGES,
            NavigationConfigBuilder.forActivity()
                  .toolbarConfig(ToolbarConfig.Builder.create().visible(false).build())
                  .data(new YsbhPagerArgs(photos, lastPageReached, selectedItemIndex))
                  .build());
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
   public void updatePhotos(List<YSBHPhoto> items) {
      DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new BaseDiffUtilCallback(adapter.getItems(), items));
      adapter.setItemsNoNotify(items);
      diffResult.dispatchUpdatesTo(adapter);
   }

   @Override
   public void onSelectedFromPager() {

   }
}
