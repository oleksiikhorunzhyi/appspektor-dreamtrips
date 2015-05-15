package com.worldventures.dreamtrips.modules.trips.view.activity;

import android.os.Bundle;

import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.ActivityRouter;
import com.worldventures.dreamtrips.modules.common.view.activity.ToolbarActivity;
import com.worldventures.dreamtrips.modules.trips.presenter.BookItActivityPresenter;

@Layout(R.layout.activity_book_it)
public class BookItActivity extends ToolbarActivity<BookItActivityPresenter> implements BookItActivityPresenter.View {
    public static final String EXTRA_TRIP_ID = "TRIP_ID";

    private String  tripId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundleExtra = getIntent().getBundleExtra(ActivityRouter.EXTRA_BUNDLE);
        tripId = bundleExtra.getString(EXTRA_TRIP_ID);
        getPresentationModel().onCreate();
    }

    @Override
    protected int getToolbarTitle() {
        return R.string.book_it;
    }

    @Override
    public String getTripId() {
        return tripId;
    }

    @Override
    protected BookItActivityPresenter createPresentationModel(Bundle savedInstanceState) {
        return new BookItActivityPresenter(this);
    }
}