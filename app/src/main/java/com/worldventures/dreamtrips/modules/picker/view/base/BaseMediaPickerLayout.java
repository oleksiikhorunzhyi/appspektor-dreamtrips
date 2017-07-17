package com.worldventures.dreamtrips.modules.picker.view.base;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.trello.rxlifecycle.RxLifecycle;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.view.custom.RecyclerItemClickListener;
import com.worldventures.dreamtrips.modules.feed.view.util.GridAutofitLayoutManager;
import com.worldventures.dreamtrips.modules.picker.model.BaseMediaPickerViewModel;
import com.worldventures.dreamtrips.modules.picker.presenter.base.BaseMediaPickerPresenter;
import com.worldventures.dreamtrips.modules.picker.util.MediaPickerStep;
import com.worldventures.dreamtrips.modules.picker.util.adapter.MediaPickerAdapter;
import com.worldventures.dreamtrips.modules.picker.util.adapter.MediaPickerHolderFactoryImpl;
import com.worldventures.dreamtrips.modules.picker.view.custom.PickerGridRecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import io.techery.janet.operationsubscriber.view.ErrorView;
import io.techery.janet.operationsubscriber.view.ProgressView;
import rx.Observable;


public abstract class BaseMediaPickerLayout<P extends BaseMediaPickerPresenter, M extends BaseMediaPickerViewModel> extends FrameLayout
      implements BaseMediaPickerView<M>, ProgressView, ErrorView {

   @InjectView(R.id.picker_recycler_view) PickerGridRecyclerView pickerRecyclerView;
   @InjectView(R.id.picker_progress) ProgressBar progressBar;
   @InjectView(R.id.picker_error_view) FrameLayout pickerErrorLayout;
   @InjectView(R.id.tv_picker_error) TextView tvPickerError;
   private MediaPickerAdapter<M> adapter;
   private GridAutofitLayoutManager layoutManager;
   private OnNextClickListener onNextClickListener;
   private OnBackClickListener onBackClickListener;
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
      ButterKnife.inject(this);
      tvPickerError.setText(getContext().getString(R.string.media_picker_error_contents, getFailedActionText()));
      adapter = new MediaPickerAdapter<>(new ArrayList<>(), new MediaPickerHolderFactoryImpl());
      layoutManager = new GridAutofitLayoutManager(getContext(), getContext().getResources()
            .getDimension(R.dimen.photo_picker_size));
      pickerRecyclerView.setLayoutManager(layoutManager);
      pickerRecyclerView.setAdapter(adapter);
      final DefaultItemAnimator gridAnimator = new DefaultItemAnimator();
      gridAnimator.setSupportsChangeAnimations(false);
      pickerRecyclerView.setItemAnimator(gridAnimator);
      pickerRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getContext(),
            (view, position) -> handleItemClick(position)));
   }

   @OnClick(R.id.btn_picker_retry)
   public void onRetryClick() {
      getPresenter().loadItems();
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
      pickerRecyclerView.scheduleLayoutAnimation();
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
}
