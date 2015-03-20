package com.worldventures.dreamtrips.view.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.ActivityRouter;
import com.worldventures.dreamtrips.presentation.BookItActivityPresentation;

import butterknife.InjectView;

/**
 * Created by 1 on 23.01.15.
 */
@Layout(R.layout.activity_book_it)
public class BookItActivity extends PresentationModelDrivenActivity<BookItActivityPresentation> implements BookItActivityPresentation.View {
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
    protected BookItActivityPresentation createPresentationModel(Bundle savedInstanceState) {
        return new BookItActivityPresentation(this);
    }
}