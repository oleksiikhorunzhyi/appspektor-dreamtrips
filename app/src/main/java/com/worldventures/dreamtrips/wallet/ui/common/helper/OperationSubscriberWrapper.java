package com.worldventures.dreamtrips.wallet.ui.common.helper;

import android.support.annotation.Nullable;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.OperationScreen;

import io.techery.janet.helper.ActionStateSubscriber;
import rx.functions.Action1;
import rx.functions.Action2;
import rx.functions.Func1;

public final class OperationSubscriberWrapper<T> {
    public static <T> OperationSubscriberWrapper<T> forView(OperationScreen view) {
        return new OperationSubscriberWrapper<>(view);
    }

    private OperationScreen view;

    private MessageActionHolder<T> onStartHolder;
    private MessageActionHolder<T> onSuccessHolder;

    private Func1<Throwable, MessageActionHolder<T>> onFailFactory;
    private Action2<T, Integer> onProgress;

    private OperationSubscriberWrapper(OperationScreen view) {
        this.view = view;
    }

    public OperationSubscriberWrapper<T> onStart(String message, @Nullable Action1<T> onStart) {
        this.onStartHolder = new MessageActionHolder<>(message, onStart);
        return this;
    }

    public OperationSubscriberWrapper<T> onStart(String message) {
        return onStart(message, null);
    }

    public OperationSubscriberWrapper<T> onSuccess(String message, @Nullable Action1<T> onSuccess) {
        this.onSuccessHolder = new MessageActionHolder<>(message, onSuccess);
        return this;
    }

    public OperationSubscriberWrapper<T> onSuccess(@Nullable Action1<T> onSuccess) {
        return onSuccess(null, onSuccess);
    }

    public OperationSubscriberWrapper<T> onFail(String message, @Nullable Action1<T> onFail) {
        this.onFailFactory = throwable -> new MessageActionHolder<>(message, onFail);
        return this;
    }

    public OperationSubscriberWrapper<T> onFail(String message) {
        return onFail(message, null);
    }

    public OperationSubscriberWrapper<T> onFail(Func1<Throwable, MessageActionHolder<T>> factoryFailFunc) {
        this.onFailFactory = factoryFailFunc;
        return this;
    }

    @SuppressWarnings("unused")
    public OperationSubscriberWrapper<T> onProgress(Action2<T, Integer> onProgress) {
        this.onProgress = onProgress;
        return this;
    }

    public ActionStateSubscriber<T> wrap() {
        return new ActionStateSubscriber<T>()
                .onStart(t -> {
                    String message = hasActionMessage(onStartHolder) ?
                            onStartHolder.message : view.context().getString(R.string.loading);

                    view.showProgress(message, o -> onStartHolder.action.call(t));
                })
                .onSuccess(t -> {
                    String message = hasActionMessage(onSuccessHolder) ? onSuccessHolder.message
                            : view.context().getString(R.string.ok);

                    view.hideProgress();
                    view.showSuccess(message, o -> onSuccessHolder.action.call(t));
                })
                .onFail((t, throwable) -> {
                    final MessageActionHolder<T> failHolder = onFailFactory != null ? onFailFactory.call(throwable) : null;
                    String message = hasActionMessage(failHolder) ? failHolder.message
                            : view.context().getString(R.string.error_something_went_wrong);

                    view.hideProgress();
                    view.showError(message, o -> failHolder.action.call(t));
                })
                .onProgress((t, integer) -> {
                    if (onProgress != null) {
                        onProgress.call(t, integer);
                    }
                });
    }

    public static final class MessageActionHolder<T> {
        Action1<T> action;
        String message;

        public MessageActionHolder(String message, Action1<T> action) {
            this.message = message;
            this.action = action;
        }
    }

    private boolean hasActionMessage(MessageActionHolder<T> holder) {
        return holder != null && holder.message != null;
    }
}