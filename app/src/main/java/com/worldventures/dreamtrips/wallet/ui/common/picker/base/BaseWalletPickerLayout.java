package com.worldventures.dreamtrips.wallet.ui.common.picker.base;

import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.AsyncLayoutInflater;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.trello.rxlifecycle.RxLifecycle;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.feed.view.util.GridAutofitLayoutManager;
import com.worldventures.dreamtrips.wallet.ui.common.picker.dialog.WalletPickerStep;
import com.worldventures.dreamtrips.wallet.ui.dashboard.util.adapter.RecyclerItemClickListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import io.techery.janet.operationsubscriber.view.ErrorView;
import io.techery.janet.operationsubscriber.view.ProgressView;
import rx.Observable;


public abstract class BaseWalletPickerLayout<P extends BaseWalletPickerPresenter, M extends BasePickerViewModel, A extends BaseWalletPickerAdapter> extends FrameLayout
      implements BaseWalletPickerView<M>, AsyncLayoutInflater.OnInflateFinishedListener, ProgressView, ErrorView {

   @InjectView(R.id.picker_recycler_view) RecyclerView pickerRecyclerView;
   @InjectView(R.id.picker_progress) ProgressBar progressBar;
   @InjectView(R.id.picker_error_view) FrameLayout pickerErrorLayout;
   @InjectView(R.id.tv_picker_error) TextView tvPickerError;
   private AsyncLayoutInflater asyncLayoutInflater;
   private A adapter;
   private GridAutofitLayoutManager layoutManager;
   private OnNextClickListener onNextClickListener;
   private OnBackClickListener onBackClickListener;
   private Bundle arguments;

   public BaseWalletPickerLayout(@NonNull Context context) {
      super(context);
      init();
   }

   public BaseWalletPickerLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
      super(context, attrs);
      init();
   }

   private void init() {
      asyncLayoutInflater = new AsyncLayoutInflater(getContext());
      asyncLayoutInflater.inflate(R.layout.wallet_picker_layout, this, this);
   }

   @Override
   public void onInflateFinished(View view, int resid, ViewGroup parent) {
      parent.addView(view);
      ButterKnife.inject(this, view);
      tvPickerError.setText(getContext().getString(R.string.wallet_picker_error_contents, getFailedActionText()));
      adapter = createAdapter(new ArrayList<>(), new WalletPickerHolderFactoryImpl());
      layoutManager = new GridAutofitLayoutManager(getContext(), getContext().getResources()
            .getDimension(R.dimen.photo_picker_size));
      pickerRecyclerView.setLayoutManager(layoutManager);
      pickerRecyclerView.setAdapter(adapter);
      pickerRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getContext(),
            new RecyclerItemClickListener.OnItemClickListener() {
               @Override
               public void onItemClick(View view, int position) {
                  handleItemClick(position);
               }

               @Override
               public void onItemLongClick(View childView, int position, Point point) {

               }
            }));
   }

   @OnClick(R.id.btn_picker_retry)
   public void onRetryClick() {
      getPresenter().loadItems();
   }

   public A getAdapter() {
      return adapter;
   }

   public RecyclerView getPickerRecyclerView() {
      return pickerRecyclerView;
   }

   public GridAutofitLayoutManager getLayoutManager() {
      return layoutManager;
   }

   @Override
   public void addItems(List<M> items) {
      adapter.updateItems(items);
   }

   @Override
   public void onAttachedToWindow() {
      super.onAttachedToWindow();
      getPresenter().attachView(this);
   }

   @Override
   public void onDetachedFromWindow() {
      super.onDetachedFromWindow();
      getPresenter().performCleanup();
      getPresenter().detachView(true);
   }

   public Observable<WalletPickerAttachment> attachedPhotos() {
      return getPresenter().attachedPhotos();
   }

   public void setOnNextClickListener(OnNextClickListener onNextClickListener) {
      this.onNextClickListener = onNextClickListener;
   }

   public OnNextClickListener getOnNextClickListener() {
      return onNextClickListener;
   }

   public OnBackClickListener getOnBackClickListener() {
      return onBackClickListener;
   }

   public void setOnBackClickListener(OnBackClickListener onBackClickListener) {
      this.onBackClickListener = onBackClickListener;
   }

   public Bundle getArguments() {
      return arguments;
   }

   public void setArguments(Bundle arguments) {
      this.arguments = arguments;
   }

   @Override
   public <T> Observable.Transformer<T, T> lifecycle() {
      return RxLifecycle.bindView(this);
   }

   @Override
   public void showError(Object o, Throwable throwable) {
      pickerErrorLayout.animate().alpha(1.0f).withStartAction(() ->
            pickerErrorLayout.setVisibility(VISIBLE));
   }

   @Override
   public boolean isErrorVisible() {
      return pickerErrorLayout.getVisibility() == VISIBLE;
   }

   @Override
   public void hideError() {
      pickerErrorLayout.animate().alpha(0f).withEndAction(() ->
            pickerErrorLayout.setVisibility(GONE));
   }

   @Override
   public void showProgress(Object o) {
      progressBar.setVisibility(VISIBLE);
   }

   @Override
   public boolean isProgressVisible() {
      return progressBar.getVisibility() == VISIBLE;
   }

   @Override
   public void hideProgress() {
      progressBar.setVisibility(GONE);
   }

   public abstract A createAdapter(List<M> items, WalletPickerHolderFactory holderFactory);

   public abstract void handleItemClick(int position);

   public abstract P getPresenter();

   public abstract WalletPickerStep getStep();

   public abstract String getFailedActionText();

   public interface OnNextClickListener {
      void onNextClick(Bundle args);
   }

   public interface OnBackClickListener {
      void onBackClick();
   }
}
