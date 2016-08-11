package com.worldventures.dreamtrips.modules.player;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;

import com.worldventures.dreamtrips.modules.common.view.activity.BaseActivity;

import butterknife.InjectView;
import timber.log.Timber;

@Layout(R.layout.activity_podcast_player)
public class PodcastPlayerActivity extends BaseActivity {

    @InjectView(R.id.toolbar_actionbar) protected Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Timber.d("Podcasts -- PodcastPlayerActivity -- onCreate()");
    }

    @Override
    protected void afterCreateView(Bundle savedInstanceState) {
        super.afterCreateView(savedInstanceState);
        setSupportActionBar(this.toolbar);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Timber.d("Podcasts -- PodcastPlayerActivity -- onNewIntent()");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Timber.d("Podcasts -- PodcastPlayerActivity -- onDestroy()");
    }
}
