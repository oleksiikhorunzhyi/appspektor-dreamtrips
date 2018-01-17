package com.worldventures.dreamtrips.social.ui.tripsimages.presenter

import com.worldventures.dreamtrips.modules.common.presenter.Presenter
import com.worldventures.dreamtrips.modules.common.view.viewpager.FragmentItem
import icepick.State

abstract class BaseImageViewPagerPresenter<VT : BaseImageViewPagerPresenter.View>(
      internal var lastPageReached: Boolean,
      @JvmField @State var selectedPosition: Int) : Presenter<VT>() {

   internal var loading: Boolean = false

   protected abstract fun makeFragmentItems(): List<FragmentItem>

   open val currentItemsSize
      get() = 0

   override fun onViewTaken() {
      super.onViewTaken()
      initItems()
   }

   internal open fun initItems() {
      view.setItems(makeFragmentItems())
      view.setSelectedPosition(selectedPosition)
   }

   open fun pageSelected(position: Int) {
      if (!lastPageReached && !loading && currentItemsSize - position < VISIBLE_THRESHOLD) {
         loadMore()
      }
   }

   protected open fun loadMore() {}

   interface View : Presenter.View {
      fun setSelectedPosition(position: Int)

      fun setItems(fragmentItems: List<FragmentItem>)

      fun remove(position: Int)

      fun goBack()
   }

   companion object {
      private const val VISIBLE_THRESHOLD = 5
   }
}
