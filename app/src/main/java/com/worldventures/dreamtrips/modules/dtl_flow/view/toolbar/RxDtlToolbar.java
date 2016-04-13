package com.worldventures.dreamtrips.modules.dtl_flow.view.toolbar;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;

import com.jakewharton.rxbinding.view.RxView;
import com.jakewharton.rxbinding.widget.RxTextView;

import rx.Observable;
import rx.functions.Action1;

import static com.jakewharton.rxbinding.internal.Preconditions.checkNotNull;

/**
 * Static factory methods for creating {@linkplain Observable observables} and {@linkplain Action1
 * actions} for {@link DtlToolbar}.
 */
public class RxDtlToolbar {

    /**
     * Create an observable which emits on {@code DtlToolbar} expand. The emitted value is
     * unspecified and should only be used as notification.
     * <p>
     * <em>Warning:</em> The created observable keeps a strong reference to {@code view}. Unsubscribe
     * to free this reference.
     */
    @CheckResult
    @NonNull
    public static Observable<Void> expands(@NonNull DtlToolbar dtlToolbar) {
        checkNotNull(dtlToolbar, "dtlToolbar == null");
        return Observable.create(new DtlToolbarExpandsOnSubscribe(dtlToolbar));
    }

    /**
     * Create an observable which emits on {@code DtlToolbar} collapse. The emitted value is
     * unspecified and should only be used as notification.
     * <p>
     * <em>Warning:</em> The created observable keeps a strong reference to {@code view}. Unsubscribe
     * to free this reference.
     */
    @CheckResult
    @NonNull
    public static Observable<Void> collapses(@NonNull DtlToolbar dtlToolbar) {
        checkNotNull(dtlToolbar, "dtlToolbar == null");
        return Observable.create(new DtlToolbarCollapsesOnSubscribe(dtlToolbar));
    }

    /**
     * Create an observable which emits on {@code DtlToolbar} map icon clicks. The emitted value is
     * unspecified and should only be used as notification.
     * <p>
     * <em>Warning:</em> The created observable keeps a strong reference to {@code view}. Unsubscribe
     * to free this reference.
     */
    @CheckResult
    @NonNull
    public static Observable<Void> mapClicks(@NonNull DtlToolbar dtlToolbar) {
        checkNotNull(dtlToolbar, "dtlToolbar == null");
        return Observable.create(new DtlToolbarMapClicksOnSubscribe(dtlToolbar));
    }

    /**
     * Create an observable which emits on {@code DtlToolbar} navigatin control clicks.<br />
     * The emitted value is unspecified and should only be used as notification.<br />
     * <p>
     * <em>Warning:</em> The created observable keeps a strong reference to {@code view}. Unsubscribe
     * to free this reference.
     */
    @CheckResult
    @NonNull
    public static Observable<Void> navigationClicks(@NonNull DtlToolbar dtlToolbar) {
        checkNotNull(dtlToolbar, "dtlToolbar == null");
        return Observable.create(new DtlToolbarNavigationControlClicksOnSubscribe(dtlToolbar));
    }

    /**
     * Create an observable of character sequences for text changes on {@code view}.
     * <p>
     * <em>Warning:</em> Values emitted by this observable are <b>mutable</b> and owned by the host
     * {@code TextView} and thus are <b>not safe</b> to cache or delay reading (such as by observing
     * on a different thread). If you want to cache or delay reading the items emitted then you must
     * map values through a function which calls {@link String#valueOf} or
     * {@link CharSequence#toString() .toString()} to create a copy.
     * <p>
     * <em>Warning:</em> The created observable keeps a strong reference to {@code view}. Unsubscribe
     * to free this reference.
     * <p>
     * <em>Note:</em> A value will be emitted immediately on subscribe.
     */
    @CheckResult
    @NonNull
    public static Observable<String> merchantSearchTextChanges(@NonNull DtlToolbar dtlToolbar) {
        checkNotNull(dtlToolbar, "dtlToolbar == null");
        checkNotNull(dtlToolbar.getMerchantSearchView(), "view == null");
        return RxTextView.textChanges(dtlToolbar.getMerchantSearchView())
                .map(CharSequence::toString);
    }

    /**
     * Create an observable of character sequences for text changes on {@code view}.
     * <p>
     * <em>Warning:</em> Values emitted by this observable are <b>mutable</b> and owned by the host
     * {@code TextView} and thus are <b>not safe</b> to cache or delay reading (such as by observing
     * on a different thread). If you want to cache or delay reading the items emitted then you must
     * map values through a function which calls {@link String#valueOf} or
     * {@link CharSequence#toString() .toString()} to create a copy.
     * <p>
     * <em>Warning:</em> The created observable keeps a strong reference to {@code view}. Unsubscribe
     * to free this reference.
     * <p>
     * <em>Note:</em> A value will be emitted immediately on subscribe.
     */
    @CheckResult
    @NonNull
    public static Observable<Boolean> locationInputFocusChanges(@NonNull DtlToolbar dtlToolbar) {
        checkNotNull(dtlToolbar, "dtlToolbar == null");
        checkNotNull(dtlToolbar.getLocationSearchView(), "view == null");
        return RxView.focusChanges(dtlToolbar.getLocationSearchView());
    }
}
