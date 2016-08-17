package com.messenger.ui.module;


public interface ModulePresenter<V extends ModuleView> {

   V getView();

   void onParentViewAttachedToWindow();

   void onParentViewDetachedFromWindow();
}

