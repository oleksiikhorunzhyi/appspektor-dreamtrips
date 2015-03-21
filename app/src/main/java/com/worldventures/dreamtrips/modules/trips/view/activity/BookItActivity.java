package com.worldventures.dreamtrips.modules.trips.view.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.ActivityRouter;
import com.worldventures.dreamtrips.modules.common.view.activity.PresentationModelDrivenActivity;
import com.worldventures.dreamtrips.modules.trips.presenter.BookItActivityPresenter;

import butterknife.InjectView;

@Layout(R.layout.activity_book_it)
public class BookItActivity extends PresentationModelDrivenActivity<BookItActivityPresenter> implements BookItActivityPresenter.View {
    public static final String EXTRA_TRIP_ID = "TRIP_ID";

    @InjectView(R.id.toolbar_actionbar)
    Toolbar toolbar;

    private int tripId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundleExtra = getIntent().getBundleExtra(ActivityRouter.EXTRA_BUNDLE);
        tripId = bundleExtra.getInt(EXTRA_TRIP_ID);
        getPresentationModel().onCreate();
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.title_book_it);
        toolbar.setBackgroundColor(getResources().getColor(R.color.theme_main));
    }

    @Override
    public int getTripId() {
        return tripId;
    }

    @Override
    protected BookItActivityPresenter createPresentationModel(Bundle savedInstanceState) {
        return new BookItActivityPresenter(this);
    }
}