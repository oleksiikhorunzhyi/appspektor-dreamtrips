package com.worldventures.dreamtrips.view.fragment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.techery.spares.adapter.BaseArrayListAdapter;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.model.BucketItem;
import com.worldventures.dreamtrips.presentation.BucketListQuickInputPM;
import com.worldventures.dreamtrips.view.cell.BucketItemCell;

import butterknife.InjectView;

/**
 * Created by 1 on 26.02.15.
 */
public class BucketListQuickInputFragment extends BaseFragment<BucketListQuickInputPM> {

    @InjectView(R.id.recyclerViewBucketItems)
    RecyclerView recyclerView;

    @InjectView(R.id.editTextQuickInput)
    EditText editTextQuick;

    private BaseArrayListAdapter<Object> arrayListAdapter;

    @Override
    public void afterCreateView(View rootView) {
        super.afterCreateView(rootView);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());

        this.recyclerView.setLayoutManager(layoutManager);
        this.arrayListAdapter = new BaseArrayListAdapter<>(getActivity(), (com.techery.spares.module.Injector) getActivity());
        this.arrayListAdapter.registerCell(BucketItem.class, BucketItemCell.class);

        this.recyclerView.setAdapter(this.arrayListAdapter);

        this.arrayListAdapter.setContentLoader(getPresentationModel().getAdapterController());
        editTextQuick.setOnEditorActionListener((v, actionId, event) -> {
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        getPresentationModel().addToBucketList();
                    }
                    return false;
                }
        );
    }

    @Override
    protected BucketListQuickInputPM createPresentationModel(Bundle savedInstanceState) {
        return new BucketListQuickInputPM(this);
    }
}
