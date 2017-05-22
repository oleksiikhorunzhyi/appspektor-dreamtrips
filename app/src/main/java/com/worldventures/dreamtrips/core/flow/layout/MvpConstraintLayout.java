/*
 * Copyright 2015 Hannes Dorfmann.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.worldventures.dreamtrips.core.flow.layout;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;

import com.hannesdorfmann.mosby.mvp.MvpPresenter;
import com.hannesdorfmann.mosby.mvp.MvpView;
import com.hannesdorfmann.mosby.mvp.delegate.BaseMvpDelegateCallback;
import com.hannesdorfmann.mosby.mvp.delegate.ViewGroupMvpDelegate;
import com.hannesdorfmann.mosby.mvp.delegate.ViewGroupMvpDelegateImpl;

/**
 * A ConstraintLayout that can be used as view with an presenter
 */
public abstract class MvpConstraintLayout<V extends MvpView, P extends MvpPresenter<V>>
      extends ConstraintLayout implements MvpView, BaseMvpDelegateCallback<V, P> {

   protected P presenter;
   protected ViewGroupMvpDelegate<V, P> mvpDelegate;

   public MvpConstraintLayout(Context context) {
      super(context);
   }

   public MvpConstraintLayout(Context context, AttributeSet attrs) {
      super(context, attrs);
   }

   public MvpConstraintLayout(Context context, AttributeSet attrs, int defStyleAttr) {
      super(context, attrs, defStyleAttr);
   }

   /**
    * Get the mvp delegate. This is internally used for creating presenter, attaching and detaching
    * view from presenter etc.
    * <p>
    * <p><b>Please note that only one instance of mvp delegate should be used per android.view.View
    * instance</b>.
    * </p>
    * <p>
    * <p>
    * Only override this method if you really know what you are doing.
    * </p>
    *
    * @return {@link ViewGroupMvpDelegateImpl}
    */
   @NonNull
   protected ViewGroupMvpDelegate<V, P> getMvpDelegate() {
      if (mvpDelegate == null) {
         mvpDelegate = new ViewGroupMvpDelegateImpl<>(this);
      }

      return mvpDelegate;
   }

   @Override
   protected void onAttachedToWindow() {
      super.onAttachedToWindow();
      if (!isInEditMode()) getMvpDelegate().onAttachedToWindow();
   }

   @Override
   protected void onDetachedFromWindow() {
      super.onDetachedFromWindow();
      if (!isInEditMode()) getMvpDelegate().onDetachedFromWindow();
   }

   /**
    * Instantiate a presenter instance
    *
    * @return The {@link MvpPresenter} for this view
    */
   public abstract P createPresenter();

   @Override
   public P getPresenter() {
      return presenter;
   }

   @Override
   public void setPresenter(P presenter) {
      this.presenter = presenter;
   }

   @Override
   public V getMvpView() {
      return (V) this;
   }

   @Override
   public boolean isRetainInstance() {
      return false;
   }

   @Override
   public void setRetainInstance(boolean retainingInstance) {
      throw new UnsupportedOperationException("Retaining instance is not supported / implemented yet");
   }

   @Override
   public boolean shouldInstanceBeRetained() {
      return false;
   }
}
