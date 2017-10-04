package com.worldventures.core.modules.picker.view.base;


import com.hannesdorfmann.mosby.mvp.MvpView;

import java.util.List;

import rx.Observable;

public interface BaseMediaPickerView<M> extends MvpView {

   void addItems(List<M> items);

   void clearItems();

   <T> Observable.Transformer<T, T> lifecycle();

}
