package com.worldventures.dreamtrips.wallet.ui.common.picker.base;


import com.hannesdorfmann.mosby.mvp.MvpView;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.RxLifecycleView;

import java.util.List;

public interface BaseWalletPickerView<M> extends MvpView, RxLifecycleView {

   void addItems(List<M> items);

   void clearItems();

}
