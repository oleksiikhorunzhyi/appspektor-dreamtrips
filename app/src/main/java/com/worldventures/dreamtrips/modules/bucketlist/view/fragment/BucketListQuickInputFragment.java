package com.worldventures.dreamtrips.modules.bucketlist.view.fragment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;

import com.techery.spares.adapter.BaseArrayListAdapter;
import com.techery.spares.annotations.Layout;
import com.techery.spares.annotations.MenuResource;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.model.bucket.BucketPostItem;
import com.worldventures.dreamtrips.modules.bucketlist.presenter.BucketListQuickInputPM;
import com.worldventures.dreamtrips.modules.bucketlist.view.activity.BucketListEditActivity;
import com.worldventures.dreamtrips.modules.bucketlist.view.cell.BucketQuickCell;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragment;

import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.Optional;

/**
 * Created by 1 on 26.02.15.
 */
@Layout(R.layout.fragment_bucket__quick_input)
@MenuResource(R.menu.menu_bucket_quick)
public class BucketListQuickInputFragment extends BaseFragment<BucketListQuickInputPM> implements BucketListQuickInputPM.View {

    @InjectView(R.id.recyclerViewBucketItems)
    RecyclerView recyclerView;

    @InjectView(R.id.editTextQuickInput)
    EditText editTextQuick;

    @Optional
    @InjectView(R.id.done)
    ImageView imageViewDone;

    private BaseArrayListAdapter<BucketPostItem> arrayListAdapter;

    @Override
    public void afterCreateView(View rootView) {
        super.afterCreateView(rootView);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());

        this.recyclerView.setLayoutManager(layoutManager);
        this.arrayListAdapter = new BaseArrayListAdapter<BucketPostItem>(getActivity(), (com.techery.spares.module.Injector) getActivity());
        this.arrayListAdapter.registerCell(BucketPostItem.class, BucketQuickCell.class);

        this.recyclerView.setAdapter(this.arrayListAdapter);

        editTextQuick.setOnEditorActionListener((v, actionId, event) -> {
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        addItem();
                    }
                    return false;
                }
        );
    }

    @Override
    public void onResume() {
        super.onResume();
        if (imageViewDone != null)
            setHasOptionsMenu(false);
    }

    @Optional
    @OnClick(R.id.mainFrame)
    void onClick() {
        getPresenter().frameClicked();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_done:
                addItem();
                break;

        }
        return super.onOptionsItemSelected(item);
    }


    public void addItem() {
        String text = editTextQuick.getText().toString();
        if (!TextUtils.isEmpty(text.trim())) {
            getPresenter().addToBucketList(text);
            editTextQuick.setText("");
        } else {
            editTextQuick.requestFocus();
        }
    }

    @Optional
    @OnClick(R.id.done)
    void onDone() {
        addItem();
    }

    @Override
    public BaseArrayListAdapter getAdapter() {
        return arrayListAdapter;
    }

    @Override
    protected BucketListQuickInputPM createPresenter(Bundle savedInstanceState) {
        BucketTabsFragment.Type type = (BucketTabsFragment.Type) getArguments().getSerializable(BucketListEditActivity.EXTRA_TYPE);
        return new BucketListQuickInputPM(this, type);
    }
}



