package com.worldventures.dreamtrips.modules.dtl_flow.view.toolbar;

import android.content.Context;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.SwitchCompat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.innahema.collections.query.queriables.Queryable;
import com.trello.rxlifecycle.RxLifecycle;
import com.worldventures.dreamtrips.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import timber.log.Timber;

public abstract class BaseDtlToolbar extends LinearLayout {

    protected static final FocusedMode DEF_FOCUSED_MODE = FocusedMode.UNDEFINED;

    @InjectView(R.id.dtlfb_rootView)
    protected DtlFilterButton filtersButton;
    @InjectView(R.id.filterDiningsSwitch)
    protected SwitchCompat filterDiningsSwitch;
    @InjectView(R.id.dtlToolbarMerchantSearchInput)
    protected AppCompatEditText merchantSearchInput;
    @InjectView(R.id.dtlToolbarLocationSearchInput)
    protected AppCompatEditText locationSearchInput;

    protected List<FilterButtonListener> filterButtonListeners = new ArrayList<>();

    protected String searchQuery;
    protected FocusedMode focusedMode;
    protected String locationTitle;
    protected String defaultEmptySearchCaption;

    public BaseDtlToolbar(Context context, AttributeSet attrs) {
        super(context, attrs);
        inflate(getContext(), R.layout.view_dtl_toolbar_content, this);
        initAttributes(attrs);
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
                .skip(1)
//                .filter(s -> !isCollapsed())
                .compose(RxLifecycle.bindView(this))
                .subscribe(searchQuery -> this.searchQuery = searchQuery);
    }

    protected abstract void updateToolbarCaptions();

    public void setToolbarCaptions(@Nullable String searchQuery, String locationTitle) {
        this.searchQuery = searchQuery;
        this.locationTitle = locationTitle;
        updateToolbarCaptions();
    }

    public void setFilterEnabled(boolean enabled) {
        filtersButton.setFilterEnabled(enabled);
    }

    public void toggleDiningFilterSwitch(boolean enabled) {
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
        Queryable.from(filterButtonListeners)
                .forEachR(listener -> listener.onFilterButtonClicked());
    }

    @OnClick(R.id.dtlToolbarMerchantSearchInput)
    protected void merchantSearchInputClicked(View view) {
        focusedMode = FocusedMode.SEARCH;
        onMerchantSearchInputClicked();
    }

    protected abstract void initState();

    protected abstract void onMerchantSearchInputClicked();

    /**
     * Sugar listener nullability checker-method.
     * @param listener object to check
     * @return true if listener is null
     */
    protected boolean checkListenerNull(Object listener) {
        if (listener == null) {
            Timber.e(new IllegalArgumentException(
                            "Listener should not be null! Skipping listener adding"),
                    "Skipping listener adding.");
            return true;
        }
        return false;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Listeners
    ///////////////////////////////////////////////////////////////////////////

    public void addFilterButtonListener(@NonNull FilterButtonListener listener) {
        if (checkListenerNull(listener)) return;
        filterButtonListeners.add(listener);
    }

    public void removeFilterButtonListener(FilterButtonListener listener) {
        filterButtonListeners.remove(listener);
    }

    public interface FilterButtonListener {

        void onFilterButtonClicked();
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
                if (value.id == attributeId) return value;
            }
            throw new IllegalArgumentException("DtlToolbar: wrong argument provided for focused" +
                    " mode attribute: must be one of " +
                    Queryable.from(values()).joinStrings(" ", element -> element.name()));
        }
    }
}
