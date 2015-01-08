package com.worldventures.dreamtrips.view.activity;

import android.os.Bundle;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.view.presentation.CreatePhotoActivityPM;

public class CreatePhotoActivity extends BaseActivity {
    CreatePhotoActivityPM pm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_photo);
        pm = new CreatePhotoActivityPM(this, this);
        pm.onCreate();
    }

}
