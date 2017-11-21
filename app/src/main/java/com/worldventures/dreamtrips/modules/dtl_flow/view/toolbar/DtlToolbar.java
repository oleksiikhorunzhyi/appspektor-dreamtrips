package com.worldventures.dreamtrips.modules.dtl_flow.view.toolbar;

import android.content.Context;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.SwitchCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.innahema.collections.query.queriables.Queryable;
import com.trello.rxlifecycle.android.RxLifecycleAndroid;
import com.worldventures.core.ui.util.SoftInputUtil;
import com.worldventures.dreamtrips.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import timber.log.Timber;

public class DtlToolbar extends LinearLayout {

   @InjectView(R.id.dtlfb_rootView) protected DtlFilterButton filtersButton;
   @InjectView(R.id.filterDiningsSwitch) protected SwitchCompat filterDiningsSwitch;
   @InjectView(R.id.dtlToolbarMerchantSearchInput) protected AppCompatEditText merchantSearchInput;
   @InjectView(R.id.dtlToolbarLocationSearchInput) protected AppCompatEditText locationSearchInput;
   @InjectView(R.id.transaction_container) protected LinearLayout transactionContainer;

   protected List<FilterButtonListener> filterButtonListeners = new ArrayList<>();
   protected List<TransactionButtonListener> transactionButtonListeners = new ArrayList<>();

   protected String searchQuery;
   protected String searchHint;
   protected FocusedMode focusedMode;
   protected String locationTitle;
   protected String defaultEmptySearchCaption;

   public DtlToolbar(Context context, AttributeSet attrs) {
      super(context, attrs);
      inflateLayout();
      initAttributes(attrs);
   }

   protected void inflateLayout() {
      inflate(getContext(), R.layout.view_dtl_toolbar_content, this);
   }

   @Override
   protected void onFinishInflate() {
      super.onFinishInflate();
      ButterKnife.inject(this);
   }

   @Override
   protected void onAttachedToWindow() {
      super.onAttachedToWindow();
      initState();
      bindSearchQueryPersisting();
   }

   @CallSuper
   protected void initAttributes(AttributeSet attrs) {
      defaultEmptySearchCaption = getResources().getString(R.string.dtlt_search_hint);
   }

   protected void bindSearchQueryPersisting() {
      RxDtlToolbar.merchantSearchTextChanges(this)
            .compose(RxLifecycleAndroid.bindView(this))
            .subscribe(searchQuery -> this.searchQuery = searchQuery);
   }

   protected void updateToolbarCaptions() {
      String hint = getHint();
      merchantSearchInput.setHint(hint);
      merchantSearchInput.setText(TextUtils.isEmpty(searchQuery) ? "" : searchQuery);

      locationSearchInput.setHint(locationTitle);
      locationSearchInput.selectAll();
   }

   protected String getHint() {
      return TextUtils.isEmpty(searchHint) ? defaultEmptySearchCaption : searchHint;
   }

   public AppCompatEditText getLocationSearchInput() {
      return locationSearchInput;
   }

   public void setSearchQuery(String searchQuery) {
      this.searchQuery = searchQuery;
      updateToolbarCaptions();
   }

   public void setCaptions(String searchQuery, String locationTitle) {
      this.searchQuery = searchQuery;
      this.locationTitle = locationTitle;
      updateToolbarCaptions();
   }

   public void setLocationCaption(String locationTitle) {
      this.locationTitle = locationTitle;
      updateToolbarCaptions();
   }

   public void resetLocationCaption() {
      locationSearchInput.setText("");
      locationTitle = "";
      updateToolbarCaptions();
   }

   public void setSearchHint(String searchHint) {
      this.searchHint = searchHint;
      updateToolbarCaptions();
   }

   public void setFilterEnabled(boolean enabled) {
      filtersButton.setFilterEnabled(enabled);
      SoftInputUtil.hideSoftInputMethod(merchantSearchInput);
   }

   public void toggleOffersOnly(boolean enabled) {
      filterDiningsSwitch.setChecked(enabled);
   }

   AppCompatEditText getMerchantSearchView() {
      return merchantSearchInput;
   }

   AppCompatEditText getLocationSearchView() {
      return locationSearchInput;
   }

   SwitchCompat getDiningFilterToggle() {
      return filterDiningsSwitch;
   }

   @OnClick(R.id.dtlfb_rootView)
   protected void filterButtonClicked(View view) {
      Queryable.from(filterButtonListeners).forEachR(listener -> listener.onFilterButtonClicked());
   }

   @OnClick(R.id.transaction_container)
   protected void transactionButtonClicked(View view) {
      Queryable.from(transactionButtonListeners).forEachR(listener -> listener.onTransactionButtonClicked());
   }

   @OnClick(R.id.dtlToolbarMerchantSearchInput)
   protected void merchantSearchInputClicked(View view) {
      focusedMode = FocusedMode.SEARCH;
      onMerchantSearchInputClicked();
   }

   public String getSearchQuery() {
      return searchQuery;
   }

   protected void initState() {
      focusedMode = FocusedMode.UNDEFINED;
      patchInputFields();
   }

   protected void patchInputFields() {
      if (focusedMode != FocusedMode.UNDEFINED) {
         switch (focusedMode) {
            case SEARCH:
               merchantSearchInput.requestFocus();
               SoftInputUtil.showSoftInputMethod(getContext());
               break;
            case LOCATION:
               SoftInputUtil.showSoftInputMethod(getContext());
               locationSearchInput.requestFocus();
               break;
            default:
               break;
         }
      }
      // due to possible bug in EditText - hint not saved after rotation. Fix below:
      if (TextUtils.isEmpty(searchQuery) || TextUtils.isEmpty(merchantSearchInput.getHint())) {
         merchantSearchInput.setHint(getHint());
      }
   }

   protected void onMerchantSearchInputClicked() {
   }

   /**
    * Sugar listener nullability checker-method.
    *
    * @param listener object to check
    * @return true if listener is null
    */
   protected boolean checkListenerNull(Object listener) {
      if (listener == null) {
         Timber.e(new IllegalArgumentException("Listener should not be null! Skipping listener adding"), "Skipping listener adding.");
         return true;
      }
      return false;
   }

   ///////////////////////////////////////////////////////////////////////////
   // Listeners
   ///////////////////////////////////////////////////////////////////////////

   public void addFilterButtonListener(@NonNull FilterButtonListener listener) {
      if (checkListenerNull(listener)) {
         return;
      }
      filterButtonListeners.add(listener);
   }

   public void removeFilterButtonListener(FilterButtonListener listener) {
      filterButtonListeners.remove(listener);
   }

   public interface FilterButtonListener {

      void onFilterButtonClicked();
   }

   public interface TransactionButtonListener {

      void onTransactionButtonClicked();
   }

   public void addTransactionButtonListener(@NonNull TransactionButtonListener listener) {
      if (checkListenerNull(listener)) {
         return;
      }
      transactionButtonListeners.add(listener);
   }

   public void removeTransactionButtonListener(TransactionButtonListener listener) {
      transactionButtonListeners.remove(listener);
   }

   ///////////////////////////////////////////////////////////////////////////
   // Attributes enum
   ///////////////////////////////////////////////////////////////////////////

   protected enum FocusedMode {

      UNDEFINED(0), SEARCH(1), LOCATION(2);

      int id;

      FocusedMode(int attributeId) {
         this.id = attributeId;
      }

      public static FocusedMode fromAttribute(int attributeId) {
         for (FocusedMode value : values()) {
            if (value.id == attributeId) {
               return value;
            }
         }
         throw new IllegalArgumentException("DtlToolbar: wrong argument provided for focused"
               + " mode attribute: must be one of "
               + Queryable.from(values()).joinStrings(" ", element -> element.name()));
      }
   }
}
