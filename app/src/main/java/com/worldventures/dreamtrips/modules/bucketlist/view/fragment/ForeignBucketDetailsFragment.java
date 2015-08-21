package com.worldventures.dreamtrips.modules.bucketlist.view.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.bucketlist.presenter.ForeignBucketItemDetailsPresenter;

import butterknife.InjectView;

@Layout(R.layout.layout_bucket_item_details)
public class ForeignBucketDetailsFragment extends BucketDetailsFragment<ForeignBucketItemDetailsPresenter> {

    @InjectView(R.id.controllerWrapper)
    View controllerWrapper;
    @InjectView(R.id.pictures_title)
    TextView picturesTitle;

    @Override
    protected ForeignBucketItemDetailsPresenter createPresenter(Bundle savedInstanceState) {
        return new ForeignBucketItemDetailsPresenter(getArguments());
    }


    @Override
    public void afterCreateView(View rootView) {
        super.afterCreateView(rootView);
        checkBox.setVisibility(View.GONE);
        controllerWrapper.setVisibility(View.GONE);
        picturesTitle.setText(R.string.pictures);
        bucketPhotosView.hideCreateBtn();
    }
}
