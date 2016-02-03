package com.worldventures.dreamtrips.modules.dtl.view.dialog;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.techery.spares.adapter.BaseDelegateAdapter;
import com.techery.spares.annotations.Layout;
import com.techery.spares.module.Injector;
import com.techery.spares.module.qualifier.ForActivity;
import com.techery.spares.ui.view.cell.CellDelegate;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.selectable.SelectionManager;
import com.worldventures.dreamtrips.core.selectable.SingleSelectionManager;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragment;
import com.worldventures.dreamtrips.modules.dtl.event.CloseDialogEvent;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.filter.DtlFilterData;
import com.worldventures.dreamtrips.modules.dtl.presenter.DtlDistancePresenter;
import com.worldventures.dreamtrips.modules.dtl.view.cell.DtlDistanceCell;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;

import butterknife.InjectView;
import butterknife.OnClick;


@Layout(R.layout.fragment_dtl_distance)
public class DtlDistanceFragment extends BaseFragment<DtlDistancePresenter> implements
        DtlDistancePresenter.View, CellDelegate<DtlFilterData.DistanceType> {

    @Inject
    @ForActivity
    Provider<Injector> injectorProvider;
    //
    @InjectView(R.id.recyclerView)
    RecyclerView recyclerView;
    //
    SelectionManager selectionManager;

    private BaseDelegateAdapter<DtlFilterData.DistanceType> distanceAdapter;

    @Override
    public void afterCreateView(View rootView) {
        super.afterCreateView(rootView);
        distanceAdapter = new BaseDelegateAdapter<>(getActivity(), injectorProvider.get());
        distanceAdapter.registerCell(DtlFilterData.DistanceType.class, DtlDistanceCell.class);
        distanceAdapter.registerDelegate(DtlFilterData.DistanceType.class, this);
        //
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        //
        selectionManager = new SingleSelectionManager(recyclerView);
        //
        recyclerView.setAdapter(selectionManager.provideWrappedAdapter(distanceAdapter));
    }

    @Override
    public void onCellClicked(DtlFilterData.DistanceType model) {
        eventBus.post(new CloseDialogEvent());
    }

    @OnClick(R.id.button_cancel)
    void onCancel() {
        eventBus.post(new CloseDialogEvent());
    }

    @Override
    public void attachDistance(List<DtlFilterData.DistanceType> distanceTypes) {
        distanceAdapter.setItems(distanceTypes);
    }

    @Override
    protected DtlDistancePresenter createPresenter(Bundle savedInstanceState) {
        return new DtlDistancePresenter();
    }
}
