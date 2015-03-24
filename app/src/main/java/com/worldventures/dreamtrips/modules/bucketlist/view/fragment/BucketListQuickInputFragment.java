package com.worldventures.dreamtrips.modules.bucketlist.view.fragment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.techery.spares.annotations.Layout;
import com.techery.spares.annotations.MenuResource;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.bucketlist.presenter.BucketListQuickInputPM;
import com.worldventures.dreamtrips.modules.bucketlist.view.activity.BucketListEditActivity;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragment;

import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.Optional;

/**
 * Created by 1 on 26.02.15.
 */
@Layout(R.layout.fragment_bucket_item_edit)
@MenuResource(R.menu.menu_bucket_quick)
public class BucketListQuickInputFragment extends BaseFragment<BucketListQuickInputPM> implements BucketListQuickInputPM.View {

    @Optional
    @InjectView(R.id.done)
    ImageView imageViewDone;


    @Override
    public void afterCreateView(View rootView) {
        super.afterCreateView(rootView);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
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
                break;

        }
        return super.onOptionsItemSelected(item);
    }


    @Optional
    @OnClick(R.id.done)
    void onDone() {
    }


    @Override
    protected BucketListQuickInputPM createPresenter(Bundle savedInstanceState) {
        BucketTabsFragment.Type type = (BucketTabsFragment.Type) getArguments().getSerializable(BucketListEditActivity.EXTRA_TYPE);
        return new BucketListQuickInputPM(this, type);
    }
}




