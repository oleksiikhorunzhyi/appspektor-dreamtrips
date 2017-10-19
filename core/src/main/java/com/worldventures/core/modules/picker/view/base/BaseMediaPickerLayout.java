package com.worldventures.core.modules.picker.view.base;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.trello.rxlifecycle.android.RxLifecycleAndroid;
import com.worldventures.core.R;
import com.worldventures.core.modules.picker.presenter.base.BaseMediaPickerPresenter;
import com.worldventures.core.modules.picker.util.MediaPickerStep;
import com.worldventures.core.modules.picker.util.adapter.MediaPickerAdapter;
import com.worldventures.core.modules.picker.util.adapter.MediaPickerHolderFactoryImpl;
import com.worldventures.core.modules.picker.view.custom.PickerGridRecyclerView;
import com.worldventures.core.modules.picker.viewmodel.BaseMediaPickerViewModel;
import com.worldventures.core.ui.view.custom.GridAutofitLayoutManager;
import com.worldventures.core.ui.view.custom.RecyclerItemClickListener;

import java.util.ArrayList;
import java.util.List;

import io.techery.janet.operationsubscriber.view.ErrorView;
import io.techery.janet.operationsubscriber.view.ProgressView;
import rx.Observable;

public abstract class BaseMediaPickerLayout<P extends BaseMediaPickerPresenter, M extends BaseMediaPickerViewModel> extends FrameLayout
      implements BaseMediaPickerView<M>, ProgressView, ErrorView {

   private ProgressBar progressBar;
   private FrameLayout pickerErrorLayout;
   private TextView tvPickerError;
   protected PickerGridRecyclerView pickerRecyclerView;

   private MediaPickerAdapter<M> adapter;
   private GridAutofitLayoutManager layoutManager;
   private OnNextClickListener onNextClickListener;
   private OnBackClickListener onBackClickListener;
   private OnAttachedListener onAttachedListener;
   private Bundle arguments;

   public BaseMediaPickerLayout(@NonNull Context context) {
      this(context, null);
   }

   public BaseMediaPickerLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
      super(context, attrs);
      initView();
   }

   protected void initView() {
      LayoutInflater.from(getContext()).inflate(R.layout.picker_layout, this);
      initUI();
      initListeners();
      tvPickerError.setText(getContext().getString(R.string.media_picker_error_contents, getFailedActionText()));
      adapter = new MediaPickerAdapter<>(new ArrayList<>(), new MediaPickerHolderFactoryImpl());
      layoutManager = new GridAutofitLayoutManager(getContext(), getContext().getResources()
            .getDimension(R.dimen.photo_picker_size));
      pickerRecyclerView.setLayoutManager(layoutManager);
      pickerRecyclerView.setAdapter(adapter);
      pickerRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getContext(),
            (view, position) -> handleItemClick(position)));
   }

   private void initUI() {
      progressBar = findViewById(R.id.picker_progress);
      pickerErrorLayout = findViewById(R.id.picker_error_view);
      tvPickerError = findViewById(R.id.tv_picker_error);
      pickerRecyclerView = findViewById(R.id.picker_recycler_view);
   }

   private void initListeners() {
      findViewById(R.id.btn_picker_retry).setOnClickListener(view -> getPresenter().loadItems());
   }

   public MediaPickerAdapter<M> getAdapter() {
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
      if (onAttachedListener != null) onAttachedListener.onAttached();
   }

   @Override
   public void onDetachedFromWindow() {
      super.onDetachedFromWindow();
      getPresenter().performCleanup();
      getPresenter().detachView(true);
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

   public OnAttachedListener getOnAttachedListener() {
      return onAttachedListener;
   }

   public void setOnAttachedListener(OnAttachedListener onAttachedListener) {
      this.onAttachedListener = onAttachedListener;
   }

   public Bundle getArguments() {
      return arguments;
   }

   public void setArguments(Bundle arguments) {
      this.arguments = arguments;
   }

   @Override
   public <T> Observable.Transformer<T, T> lifecycle() {
      return RxLifecycleAndroid.bindView(this);
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

   public abstract void handleItemClick(int position);

   public abstract P getPresenter();

   public abstract MediaPickerStep getStep();

   public abstract String getFailedActionText();

   public abstract Observable<List<M>> attachedItems();

   public interface OnNextClickListener {
      void onNextClick(Bundle args);
   }

   public interface OnBackClickListener {
      void onBackClick();
   }

   public interface OnAttachedListener {
      void onAttached();
   }
}
