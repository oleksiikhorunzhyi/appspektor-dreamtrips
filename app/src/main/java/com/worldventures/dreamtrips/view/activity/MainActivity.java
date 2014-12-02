package com.worldventures.dreamtrips.view.activity;

import android.os.Bundle;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.view.presentation.activity.MainActivityPresentation;

public class MainActivity extends BaseActivity implements MainActivityPresentation.View {

    private MainActivityPresentation presentationModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presentationModel = new MainActivityPresentation(this, getDataManager());
        initializeContentView(R.layout.activity_main, presentationModel);
    }


    @Override
    protected void onResume() {
        super.onResume();
        presentationModel.loadTrips();
    }

    @Override
    public void tripsLoaded() {
        //todo now nothing
    }
}
