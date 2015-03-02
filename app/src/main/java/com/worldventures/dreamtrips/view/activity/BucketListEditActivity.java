package com.worldventures.dreamtrips.view.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.ActivityRouter;
import com.worldventures.dreamtrips.presentation.BucketListEditActivityPM;

import butterknife.InjectView;

/**
 * Created by 1 on 26.02.15.
 */
@Layout(R.layout.activity_book_it)
public class BucketListEditActivity extends PresentationModelDrivenActivity<BucketListEditActivityPM>{

    public static final String EXTRA_TYPE = "EXTRA_TYPE";

    @InjectView(R.id.toolbar_actionbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundleExtra = getIntent().getBundleExtra(ActivityRouter.EXTRA_BUNDLE);
        getPresentationModel().onCreate(bundleExtra);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.bucket_list_my_title);
        toolbar.setBackgroundColor(getResources().getColor(R.color.theme_main));
    }

    @Override
    protected BucketListEditActivityPM createPresentationModel(Bundle savedInstanceState) {
        return new BucketListEditActivityPM(this);
    }
}
