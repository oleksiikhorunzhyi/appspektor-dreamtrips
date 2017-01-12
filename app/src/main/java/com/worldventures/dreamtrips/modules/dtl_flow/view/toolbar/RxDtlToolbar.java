package com.worldventures.dreamtrips.modules.dtl_flow.view.toolbar;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.view.inputmethod.EditorInfo;
import android.widget.CompoundButton;

import com.jakewharton.rxbinding.view.RxView;
import com.jakewharton.rxbinding.widget.RxCompoundButton;
import com.jakewharton.rxbinding.widget.RxTextView;

import rx.Observable;
import rx.functions.Action1;

import static com.jakewharton.rxbinding.internal.Preconditions.checkNotNull;

/**
 * Static factory methods for creating {@linkplain Observable observables} and {@linkplain Action1
 * actions} for {@link DtlToolbar} and it's descendants.
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
   public static Observable<Void> expands(@NonNull ExpandableDtlToolbar dtlToolbar) {
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
   public static Observable<Void> collapses(@NonNull ExpandableDtlToolbar dtlToolbar) {
      checkNotNull(dtlToolbar, "dtlToolbar == null");
      return Observable.create(new DtlToolbarCollapsesOnSubscribe(dtlToolbar));
   }

   /**
    * Create an observable which emits on {@code DtlToolbar} navigation icon clicks. The emitted value is
    * unspecified and should only be used as notification.
    * <p>
    * <em>Warning:</em> The created observable keeps a strong reference to {@code view}. Unsubscribe
    * to free this reference.
    */
   @CheckResult
   @NonNull
   public static Observable<Void> navigationClicks(@NonNull ExpandableDtlToolbar dtlToolbar) {
      checkNotNull(dtlToolbar, "dtlToolbar == null");
      return Observable.create(new DtlToolbarNavigationClicksOnSubscribe(dtlToolbar));
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
   public static Observable<Void> actionViewClicks(@NonNull ExpandableDtlToolbar dtlToolbar) {
      checkNotNull(dtlToolbar, "dtlToolbar == null");
      return Observable.create(new DtlToolbarNavigationControlClicksOnSubscribe(dtlToolbar));
   }

   /**
    * Create an observable which emits on {@code DtlToolbar} filter button clicks.<br />
    * The emitted value is unspecified and should only be used as notification.<br />
    * <p>
    * <em>Warning:</em> The created observable keeps a strong reference to {@code view}. Unsubscribe
    * to free this reference.
    */
   @CheckResult
   @NonNull
   public static Observable<Void> filterButtonClicks(@NonNull DtlToolbar dtlToolbar) {
      checkNotNull(dtlToolbar, "dtlToolbar == null");
      return Observable.create(new DtlToolbarFilterClicksOnSubscribe(dtlToolbar));
   }

   /**
    * Create an observable of character sequences for text changes on {@code view}.
    * <p>
    * <em>Note:</em> reacts only when text changes and view has focus.
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
      return RxTextView.textChangeEvents(dtlToolbar.getMerchantSearchView())
            .filter(textViewTextChangeEvent -> textViewTextChangeEvent.view().hasFocus())
            .map(textViewTextChangeEvent -> textViewTextChangeEvent.text().toString());
   }

   /**
    * Create an observable of character sequences for text when ime action 'search' applied on {@code view}.
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
   public static Observable<String> merchantSearchApplied(@NonNull DtlToolbar dtlToolbar) {
      checkNotNull(dtlToolbar, "dtlToolbar == null");
      checkNotNull(dtlToolbar.getMerchantSearchView(), "view == null");
      return RxTextView.editorActionEvents(dtlToolbar.getMerchantSearchView())
            .filter(actionEvent -> actionEvent.actionId() == EditorInfo.IME_ACTION_SEARCH)
            .map(actionEvent -> actionEvent.view().getText().toString());
   }

   /**
    * Create an observable of character sequences for text changes on {@code view}.
    * <p>
    * <em>Note:</em> reacts only when text changes and view has focus.
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
   public static Observable<String> locationSearchTextChanges(@NonNull DtlToolbar dtlToolbar) {
      checkNotNull(dtlToolbar, "dtlToolbar == null");
      checkNotNull(dtlToolbar.getMerchantSearchView(), "view == null");
      return RxTextView.textChangeEvents(dtlToolbar.getLocationSearchView())
            .filter(textViewTextChangeEvent -> textViewTextChangeEvent.view().hasFocus())
            .map(textViewTextChangeEvent -> textViewTextChangeEvent.text().toString());
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
   public static Observable<Boolean> merchantSearchInputFocusChanges(@NonNull DtlToolbar dtlToolbar) {
      checkNotNull(dtlToolbar, "dtlToolbar == null");
      checkNotNull(dtlToolbar.getLocationSearchView(), "view == null");
      return RxView.focusChanges(dtlToolbar.getMerchantSearchView());
   }
}
