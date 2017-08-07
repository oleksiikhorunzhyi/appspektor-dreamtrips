package com.worldventures.dreamtrips.modules.tripsimages.presenter;

import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.common.view.viewpager.FragmentItem;

import java.util.List;

import icepick.State;

public abstract class BaseImageViewPagerPresenter<VT extends BaseImageViewPagerPresenter.View> extends Presenter<VT> {
   private static final int LOAD_THRESHOLD = 5;

   protected boolean lastPageReached;
   protected boolean loading;

   @State int selectedPosition;

   public BaseImageViewPagerPresenter(boolean lastPageReached, int selectedPosition) {
      this.lastPageReached = lastPageReached;
      this.selectedPosition = selectedPosition;
   }

   @Override
   public void onViewTaken() {
      super.onViewTaken();
      initItems();
   }

   protected void initItems() {
      view.setItems(getItems());
      view.setSelectedPosition(selectedPosition);
   }

   protected abstract List<FragmentItem> getItems();

   public void pageSelected(int position) {
      if (!lastPageReached && !loading && getCurrentItemsSize() - position < LOAD_THRESHOLD)
         loadMore();
   }

   protected int getCurrentItemsSize() {
      return 0;
   }

   protected void loadMore() {

   }

   public interface View extends Presenter.View {
      void setSelectedPosition(int position);

      void setItems(List<FragmentItem> fragmentItems);
   }
}
