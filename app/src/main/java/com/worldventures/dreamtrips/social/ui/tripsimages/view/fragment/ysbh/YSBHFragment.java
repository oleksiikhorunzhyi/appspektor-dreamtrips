package com.worldventures.dreamtrips.social.ui.tripsimages.view.fragment.ysbh;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.GridLayoutManager;
import android.view.View;

import com.worldventures.core.ui.annotations.Layout;
import com.worldventures.core.ui.util.ViewUtils;
import com.worldventures.core.ui.view.adapter.BaseDelegateAdapter;
import com.worldventures.core.ui.view.cell.CellDelegate;
import com.worldventures.core.ui.view.custom.EmptyRecyclerView;
import com.worldventures.core.ui.view.recycler.RecyclerViewStateDelegate;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.ToolbarConfig;
import com.worldventures.dreamtrips.core.navigation.router.NavigationConfigBuilder;
import com.worldventures.dreamtrips.modules.common.view.adapter.BaseDiffUtilCallback;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragment;
import com.worldventures.dreamtrips.modules.common.view.viewpager.SelectablePagerFragment;
import com.worldventures.dreamtrips.social.ui.tripsimages.model.YSBHPhoto;
import com.worldventures.dreamtrips.social.ui.tripsimages.presenter.ysbh.YSBHPresenter;
import com.worldventures.dreamtrips.social.ui.tripsimages.view.args.YsbhPagerArgs;
import com.worldventures.dreamtrips.social.ui.tripsimages.view.cell.YsbhPhotoCell;
import com.worldventures.dreamtrips.social.ui.tripsimages.view.util.GridLayoutManagerPaginationDelegate;

import java.util.ArrayList;
import java.util.List;

import butterknife.InjectView;

@Layout(R.layout.fragment_images_list)
public class YSBHFragment extends BaseFragment<YSBHPresenter>
      implements YSBHPresenter.View, SelectablePagerFragment {

   public static final int VISIBLE_THRESHOLD = 5;

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
      recyclerView.addOnScrollListener(new GridLayoutManagerPaginationDelegate(getPresenter()::loadNext,
            VISIBLE_THRESHOLD));
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

   @Override
   protected YSBHPresenter createPresenter(Bundle savedInstanceState) {
      return new YSBHPresenter();
   }


   @Override
   public void openFullscreen(List<YSBHPhoto> photos, boolean lastPageReached, int selectedItemIndex) {
      router.moveTo(YsbhViewPagerFragment.class, NavigationConfigBuilder
            .forActivity()
            .toolbarConfig(ToolbarConfig.Builder.create().visible(false).build())
            .data(new YsbhPagerArgs(new ArrayList<>(photos), lastPageReached, selectedItemIndex))
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
