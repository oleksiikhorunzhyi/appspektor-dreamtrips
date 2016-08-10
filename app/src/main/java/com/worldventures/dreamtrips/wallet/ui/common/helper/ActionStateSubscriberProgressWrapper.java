package com.worldventures.dreamtrips.wallet.ui.common.helper;

import android.support.annotation.Nullable;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.OperationScreen;

import io.techery.janet.helper.ActionStateSubscriber;
import rx.functions.Action1;
import rx.functions.Action2;
import rx.functions.Func1;

public final class ActionStateSubscriberProgressWrapper<T> {
    public static <T> ActionStateSubscriberProgressWrapper<T> forView(OperationScreen view) {
        return new ActionStateSubscriberProgressWrapper<>(view);
    }

    private OperationScreen view;

    private MessageActionHolder<T> onStartHolder;
    private MessageActionHolder<T> onSuccessHolder;

    private Func1<Throwable, MessageActionHolder<T>> onFailFactory;
    private Action2<T, Integer> onProgress;

    private ActionStateSubscriberProgressWrapper(OperationScreen view) {
        this.view = view;
    }

    public ActionStateSubscriberProgressWrapper<T> onStart(String message, @Nullable Action1<T> onStart) {
        this.onStartHolder = new MessageActionHolder<>(message, onStart);
        return this;
    }

    public ActionStateSubscriberProgressWrapper<T> onStart(String message) {
        return onStart(message, null);
    }

    public ActionStateSubscriberProgressWrapper<T> onSuccess(String message, @Nullable Action1<T> onSuccess) {
        this.onSuccessHolder = new MessageActionHolder<>(message, onSuccess);
        return this;
    }

    public ActionStateSubscriberProgressWrapper<T> onSuccess(@Nullable Action1<T> onSuccess) {
        return onSuccess(null, onSuccess);
    }

    public ActionStateSubscriberProgressWrapper<T> onFail(String message, @Nullable Action1<T> onFail) {
        this.onFailFactory = throwable -> new MessageActionHolder<>(message, onFail);
        return this;
    }

    public ActionStateSubscriberProgressWrapper<T> onFail(String message) {
        return onFail(message, null);
    }

    public ActionStateSubscriberProgressWrapper<T> onFail(Func1<Throwable, MessageActionHolder<T>> factoryFailFunc) {
        this.onFailFactory = factoryFailFunc;
        return this;
    }

    @SuppressWarnings("unused")
    public ActionStateSubscriberProgressWrapper<T> onProgress(Action2<T, Integer> onProgress) {
        this.onProgress = onProgress;
        return this;
    }

    public ActionStateSubscriber<T> wrap() {
        return new ActionStateSubscriber<T>()
                .onStart(t -> {
                    String message = hasActionMessage(onStartHolder) ?
                            view.context().getString(R.string.loading) : onStartHolder.message;

                    view.showProgress(message);
                    if (onStartHolder.action != null) {
                        onStartHolder.action.call(t);
                    }
                })
                .onSuccess(t -> {
                    String message = hasActionMessage(onSuccessHolder) ?
                            view.context().getString(R.string.ok) : onSuccessHolder.message;

                    view.hideProgress();
                    view.showSuccess(message, o -> onSuccessHolder.action.call(t));
                })
                .onFail((t, throwable) -> {
                    MessageActionHolder<T> failHolder = onFailFactory.call(throwable);
                    String message = hasActionMessage(failHolder) ?
                            view.context().getString(R.string.error_something_went_wrong) : failHolder.message;

                    view.hideProgress();
                    view.notifyError(message, o -> failHolder.action.call(t));
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
        return holder == null || holder.message == null;
    }
}