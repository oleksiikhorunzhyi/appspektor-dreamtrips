package com.worldventures.dreamtrips.view.activity;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.techery.spares.ui.routing.BaseRouter;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.ActivityRouter;
import com.worldventures.dreamtrips.view.presentation.CreatePhotoActivityPM;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class CreatePhotoActivity extends BaseActivity {
    public static final String EXTRA_FILE_URI = "EXTRA_FILE_URI";
    CreatePhotoActivityPM pm;
    Uri imageUri;
    @InjectView(R.id.toolbar_actionbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_photo);
        ButterKnife.inject(this);

        imageUri = getIntent().getBundleExtra(BaseRouter.EXTRA_BUNDLE).getParcelable(EXTRA_FILE_URI);
        pm = new CreatePhotoActivityPM(this, this);
        pm.onCreate();

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        toolbar.getBackground().setAlpha(0);

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public Uri getImageUri() {
        return imageUri;
    }
}
