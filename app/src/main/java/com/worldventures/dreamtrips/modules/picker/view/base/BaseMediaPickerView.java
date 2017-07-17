package com.worldventures.dreamtrips.modules.picker.view.base;


import com.hannesdorfmann.mosby.mvp.MvpView;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.RxLifecycleView;

import java.util.List;

public interface BaseMediaPickerView<M> extends MvpView, RxLifecycleView {

   void addItems(List<M> items);

   void clearItems();

}
