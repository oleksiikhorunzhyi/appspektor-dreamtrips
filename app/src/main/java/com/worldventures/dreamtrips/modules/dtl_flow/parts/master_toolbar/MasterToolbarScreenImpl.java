package com.worldventures.dreamtrips.modules.dtl_flow.parts.master_toolbar;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;

import com.trello.rxlifecycle.RxLifecycle;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.flow.activity.FlowActivity;
import com.worldventures.dreamtrips.modules.dtl.model.location.DtlLocation;
import com.worldventures.dreamtrips.modules.dtl_flow.DtlLayout;
import com.worldventures.dreamtrips.modules.dtl_flow.view.toolbar.DtlToolbar;
import com.worldventures.dreamtrips.modules.dtl_flow.view.toolbar.RxDtlToolbar;

import java.util.concurrent.TimeUnit;

import butterknife.InjectView;

public class MasterToolbarScreenImpl
        extends DtlLayout<MasterToolbarScreen, MasterToolbarPresenter, MasterToolbarPath>
        implements MasterToolbarScreen {

    @InjectView(R.id.dtlToolbar)
    DtlToolbar dtlToolbar;

    @Override
    protected void onPostAttachToWindowView() {
        super.onPostAttachToWindowView();
        initDtlToolbar();
    }

    protected void initDtlToolbar() {
        RxDtlToolbar.merchantSearchTextChanges(dtlToolbar)
                .debounce(250L, TimeUnit.MILLISECONDS)
                .skipWhile(TextUtils::isEmpty)
                .compose(RxLifecycle.bindView(this))
                .subscribe(getPresenter()::applySearch);
        RxDtlToolbar.locationInputFocusChanges(dtlToolbar)
                .skip(1)
                .compose(RxLifecycle.bindView(this))
                .subscribe(aBoolean -> informUser("TODO show location change"));
        RxDtlToolbar.filterButtonClicks(dtlToolbar)
                .compose(RxLifecycle.bindView(this))
                .subscribe(aVoid -> ((FlowActivity) getActivity()).openRightDrawer());
        RxDtlToolbar.diningFilterChanges(dtlToolbar)
                .compose(RxLifecycle.bindView(this))
                .subscribe(getPresenter()::applyOffersOnlyFilterState);
    }

    @Override
    public void setFilterButtonState(boolean enabled) {
        dtlToolbar.setFilterEnabled(enabled);
    }

    @Override
    public void updateToolbarTitle(@Nullable DtlLocation dtlLocation,
                                   @Nullable String actualSearchQuery) {
        if (dtlLocation == null) return;
        switch (dtlLocation.getLocationSourceType()) {
            case NEAR_ME:
            case EXTERNAL:
                dtlToolbar.setToolbarCaptions(actualSearchQuery, dtlLocation.getLongName());
                break;
            case FROM_MAP:
                String locationTitle = TextUtils.isEmpty(dtlLocation.getLongName()) ?
                        getResources().getString(R.string.dtl_nearby_caption_empty) :
                        getResources().getString(R.string.dtl_nearby_caption_format,
                                dtlLocation.getLongName());
                dtlToolbar.setToolbarCaptions(actualSearchQuery, locationTitle);
                break;
        }
    }

    @Override
    public void toggleDiningFilterSwitch(boolean enabled) {
        dtlToolbar.toggleDiningFilterSwitch(enabled);
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
