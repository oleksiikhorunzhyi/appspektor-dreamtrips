package com.worldventures.dreamtrips.modules.dtl_flow.parts.master_toolbar;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;

import com.google.android.gms.common.api.Status;
import com.h6ah4i.android.widget.advrecyclerview.decoration.SimpleListDividerDecorator;
import com.jakewharton.rxbinding.view.RxView;
import com.techery.spares.adapter.BaseDelegateAdapter;
import com.techery.spares.ui.view.cell.CellDelegate;
import com.trello.rxlifecycle.RxLifecycle;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.flow.activity.FlowActivity;
import com.worldventures.dreamtrips.core.utils.ActivityResultDelegate;
import com.worldventures.dreamtrips.core.utils.ViewUtils;
import com.worldventures.dreamtrips.modules.dtl.model.LocationSourceType;
import com.worldventures.dreamtrips.modules.dtl.model.location.DtlExternalLocation;
import com.worldventures.dreamtrips.modules.dtl.model.location.DtlLocation;
import com.worldventures.dreamtrips.modules.dtl.view.cell.DtlLocationSearchCell;
import com.worldventures.dreamtrips.modules.dtl.view.cell.DtlLocationSearchHeaderCell;
import com.worldventures.dreamtrips.modules.dtl_flow.DtlActivity;
import com.worldventures.dreamtrips.modules.dtl_flow.DtlLayout;
import com.worldventures.dreamtrips.modules.dtl_flow.view.toolbar.DtlToolbar;
import com.worldventures.dreamtrips.modules.dtl_flow.view.toolbar.RxDtlToolbar;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import rx.Observable;
import timber.log.Timber;

public class MasterToolbarScreenImpl
        extends DtlLayout<MasterToolbarScreen, MasterToolbarPresenter, MasterToolbarPath>
        implements MasterToolbarScreen, ActivityResultDelegate.ActivityResultListener, CellDelegate<DtlExternalLocation> {

    @Inject
    ActivityResultDelegate activityResultDelegate;
    //
    @InjectView(R.id.dtlToolbar)
    DtlToolbar toolbar;
    //
    View searchContentView, autoDetectNearMe, progress;
    RecyclerView recyclerView;
    //
    BaseDelegateAdapter adapter;
    //
    PopupWindow popupWindow;

    @Override
    protected void onPostAttachToWindowView() {
        super.onPostAttachToWindowView();
        injector.inject(this);
        initDtlToolbar();
        //
        prepareViews();
        setupPopup();
        setupRecyclerView();
        //
        activityResultDelegate.addListener(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        activityResultDelegate.removeListener(this);
        onPopupVisibilityChange(false);
        super.onDetachedFromWindow();
    }

    protected void initDtlToolbar() {
        RxDtlToolbar.merchantSearchTextChanges(toolbar)
                .debounce(250L, TimeUnit.MILLISECONDS)
                .skipWhile(TextUtils::isEmpty)
                .compose(RxLifecycle.bindView(this))
                .subscribe(getPresenter()::applySearch);
        RxDtlToolbar.locationInputFocusChanges(toolbar)
                .skip(1)
                .compose(RxLifecycle.bindView(this))
                .subscribe(aBoolean -> informUser("TODO show location change"));
        RxDtlToolbar.filterButtonClicks(toolbar)
                .compose(RxLifecycle.bindView(this))
                .subscribe(aVoid -> ((FlowActivity) getActivity()).openRightDrawer());
        RxDtlToolbar.diningFilterChanges(toolbar)
                .compose(RxLifecycle.bindView(this))
                .subscribe(getPresenter()::applyOffersOnlyFilterState);
    }


    @Override
    public void updateToolbarTitle(@Nullable DtlLocation dtlLocation,
                                   @Nullable String actualSearchQuery) {
        if (dtlLocation == null) return;
        switch (dtlLocation.getLocationSourceType()) {
            case NEAR_ME:
            case EXTERNAL:
                toolbar.setToolbarCaptions(actualSearchQuery, dtlLocation.getLongName());
                break;
            case FROM_MAP:
                String locationTitle = TextUtils.isEmpty(dtlLocation.getLongName()) ?
                        getResources().getString(R.string.dtl_nearby_caption_empty) :
                        getResources().getString(R.string.dtl_nearby_caption_format,
                                dtlLocation.getLongName());
                toolbar.setToolbarCaptions(actualSearchQuery, locationTitle);
                break;
        }
    }

    @Override
    public void toggleDiningFilterSwitch(boolean enabled) {
        toolbar.toggleDiningFilterSwitch(enabled);
    }

    @Override
    public void setFilterButtonState(boolean enabled) {
        toolbar.setFilterEnabled(enabled);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Private screen methods
    ///////////////////////////////////////////////////////////////////////////

    private void prepareViews() {
        searchContentView = LayoutInflater.from(getContext()).inflate(R.layout.view_dtl_location_search, null);
        searchContentView.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
        //
        popupWindow = new PopupWindow(searchContentView, searchContentView.getMeasuredWidth(), WindowManager.LayoutParams.MATCH_PARENT);
        popupWindow.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
        popupWindow.setOutsideTouchable(true);
        //popupWindow.setOnDismissListener(() -> SoftInputUtil.hideSoftInputMethod(this)); TODO :: think about hide keyboard
        //
        this.progress = ButterKnife.findById(searchContentView, R.id.progress);
        this.autoDetectNearMe = ButterKnife.findById(searchContentView, R.id.autoDetectNearMe);
        this.recyclerView = ButterKnife.<RecyclerView>findById(searchContentView, R.id.locationsList);
    }

    protected void setupPopup() {
        Observable<Boolean> focus = RxView.focusChanges(toolbar.getLocationSearchInput());
        Observable<Boolean> clicks = RxView.clicks(toolbar.getLocationSearchInput())
                .flatMap(aVoid -> Observable.fromCallable(() -> Boolean.TRUE));

        Observable.merge(clicks, focus)
                .compose(RxLifecycle.bindView(this))
                .subscribe(this::onPopupVisibilityChange);
    }

    protected void onPopupVisibilityChange(boolean visible) {
        if (visible) popupWindow.showAsDropDown(toolbar.getLocationSearchInput());
        else if (popupWindow.isShowing()){
            hideNearMeButton();
            popupWindow.dismiss();
        }
    }

    private void setupRecyclerView() {
        adapter = new BaseDelegateAdapter<DtlExternalLocation>(getActivity(), injector);
        adapter.registerCell(DtlExternalLocation.class, DtlLocationSearchCell.class);
        adapter.registerCell(DtlLocationSearchHeaderCell.HEADER.class, DtlLocationSearchHeaderCell.class);
        adapter.registerDelegate(DtlExternalLocation.class, this);
        adapter.registerDelegate(DtlLocationSearchHeaderCell.HEADER.class, aVoid -> getPresenter().loadNearMeRequested());
        //
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.addItemDecoration(new SimpleListDividerDecorator(ContextCompat.getDrawable(getContext(), R.drawable.dtl_location_change_list_divider), false));
        //
        recyclerView.setAdapter(adapter);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Overridden and screen implementation methods
    ///////////////////////////////////////////////////////////////////////////

    @Override
    public Observable<String> provideLocationSearchObservable() {
        return RxDtlToolbar.locationSearchTextChanges(toolbar)
                .skip(1);
    }

    @Override
    public void setItems(List<DtlExternalLocation> dtlExternalLocations) {
        hideProgress();
        //
        adapter.clear();
        adapter.addItem(0, new DtlLocationSearchHeaderCell.HEADER());
        adapter.addItems(dtlExternalLocations);
    }

    @Override
    public void hideNearMeButton() {
        adapter.remove(DtlLocationSearchHeaderCell.HEADER.INSTANCE);
    }

    @Override
    public void showSearchPopup() {
        onPopupVisibilityChange(true);
    }

    @Override
    public boolean isSearchPopupShowing() {
        return popupWindow.isShowing();
    }

    @Override
    public void showProgress() {
        ViewUtils.setViewVisibility(View.VISIBLE, progress);
        ViewUtils.setViewVisibility(View.GONE, recyclerView);
    }

    @Override
    public void hideProgress() {
        ViewUtils.setViewVisibility(View.GONE, progress);
        ViewUtils.setViewVisibility(View.VISIBLE, recyclerView);
    }

    @Override
    public void onCellClicked(DtlExternalLocation location) {
        hideSoftInput();
        getPresenter().locationSelected(location);
    }

    @Override
    public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == DtlActivity.GPS_LOCATION_RESOLUTION_REQUEST) {
            switch (resultCode) {
                case Activity.RESULT_OK:
                    // All required changes were successfully made
                    getPresenter().onLocationResolutionGranted();
                    break;
                case Activity.RESULT_CANCELED:
                    // The user was asked to change settings, but chose not to
                    getPresenter().onLocationResolutionDenied();
                    break;
            }
            return true;
        }
        return false;
    }

    @Override
    public void onApiCallFailed() {
        ViewUtils.setViewVisibility(View.GONE, progress);
    }

    @Override
    public void locationResolutionRequired(Status status) {
        try {
            status.startResolutionForResult(getActivity(),
                    DtlActivity.GPS_LOCATION_RESOLUTION_REQUEST);
        } catch (IntentSender.SendIntentException th) {
            Timber.e(th, "Error opening settings activity.");
        }
    }


    ///////////////////////////////////////////////////////////////////////////
    // Boilerplate stuff
    ///////////////////////////////////////////////////////////////////////////

    public MasterToolbarScreenImpl(Context context) {
        super(context);
    }

    public MasterToolbarScreenImpl(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public MasterToolbarPresenter createPresenter() {
        return new MasterToolbarPresenterImpl(getContext(), injector);
    }
}
