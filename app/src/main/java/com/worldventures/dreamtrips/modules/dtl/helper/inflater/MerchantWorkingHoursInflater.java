package com.worldventures.dreamtrips.modules.dtl.helper.inflater;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.techery.spares.adapter.BaseArrayListAdapter;
import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.utils.ViewUtils;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.operational_hour.OperationDay;
import com.worldventures.dreamtrips.modules.dtl.view.cell.DtlWorkingHoursCell;
import com.worldventures.dreamtrips.modules.dtl.view.cell.adapter.MerchantWorkingHoursAdapter;
import com.worldventures.dreamtrips.modules.dtl.view.custom.ExpandableOfferView;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class MerchantWorkingHoursInflater extends MerchantDataInflater {

    @InjectView(R.id.expandedWorkingHoursView)
    protected ExpandableOfferView expandedView;
    protected RecyclerView hoursRecyclerView;
    protected BaseArrayListAdapter adapter;

    private final Injector injector;

    public MerchantWorkingHoursInflater(Injector injector) {
        this.injector = injector;
    }

    @Override
    public void setView(View rootView) {
        super.setView(rootView);
        //
        hoursRecyclerView = ButterKnife.findById(expandedView, R.id.workingHoursView);
        hoursRecyclerView.setLayoutManager(new LinearLayoutManager(rootView.getContext()));
        hoursRecyclerView.setNestedScrollingEnabled(false);
    }

    @Override
    protected void onMerchantApply() {
        if (merchant.getOperationDays().isEmpty()) {
            ViewUtils.setViewVisibility(expandedView, View.GONE);
            return;
        }
        adapter = new MerchantWorkingHoursAdapter(rootView.getContext(), merchant, injector);
        adapter.registerCell(OperationDay.class, DtlWorkingHoursCell.class);
        adapter.setItems(merchant.getOperationDays());
        hoursRecyclerView.setAdapter(adapter);
    }

    public boolean isViewExpanded() {
        return expandedView.isOpened();
    }

    public void preexpand() {
        expandedView.showWithoutAnimation();
    }
}
