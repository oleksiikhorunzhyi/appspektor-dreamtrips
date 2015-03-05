package com.worldventures.dreamtrips.view.fragment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.h6ah4i.android.widget.advrecyclerview.decoration.SimpleListDividerDecorator;
import com.techery.spares.adapter.BaseArrayListAdapter;
import com.techery.spares.annotations.Layout;
import com.techery.spares.annotations.MenuResource;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.model.BucketItem;
import com.worldventures.dreamtrips.presentation.BucketListQuickInputPM;
import com.worldventures.dreamtrips.view.activity.BucketListEditActivity;
import com.worldventures.dreamtrips.view.cell.BucketItemCell;
import com.worldventures.dreamtrips.view.cell.BucketQuickCell;

import butterknife.InjectView;

/**
 * Created by 1 on 26.02.15.
 */
@Layout(R.layout.fragment_bucket__quick_input)
@MenuResource(R.menu.menu_bucket_quick)
public class BucketListQuickInputFragment extends BaseFragment<BucketListQuickInputPM> {

    @InjectView(R.id.recyclerViewBucketItems)
    RecyclerView recyclerView;

    @InjectView(R.id.editTextQuickInput)
    EditText editTextQuick;

    private BaseArrayListAdapter<BucketItem> arrayListAdapter;

    @Override
    public void afterCreateView(View rootView) {
        super.afterCreateView(rootView);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());

        this.recyclerView.setLayoutManager(layoutManager);
        this.arrayListAdapter = new BaseArrayListAdapter<>(getActivity(), (com.techery.spares.module.Injector) getActivity());
        this.arrayListAdapter.registerCell(BucketItem.class, BucketQuickCell.class);

        this.recyclerView.setAdapter(this.arrayListAdapter);

        this.arrayListAdapter.setContentLoader(getPresentationModel().getAdapterController());
        editTextQuick.setOnEditorActionListener((v, actionId, event) -> {
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        getPresentationModel().addToBucketList(editTextQuick.getText().toString());
                        editTextQuick.setText("");
                    }
                    return false;
                }
        );
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_done:
                String text = editTextQuick.getText().toString();
                if (!TextUtils.isEmpty(text)) {
                    getPresentationModel().addToBucketList(text);
                    editTextQuick.setText("");
                } else {
                    editTextQuick.requestFocus();
                }
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected BucketListQuickInputPM createPresentationModel(Bundle savedInstanceState) {
        BucketTabsFragment.Type type = (BucketTabsFragment.Type) getArguments().getSerializable(BucketListEditActivity.EXTRA_TYPE);
        return new BucketListQuickInputPM(this, type);
    }
}




