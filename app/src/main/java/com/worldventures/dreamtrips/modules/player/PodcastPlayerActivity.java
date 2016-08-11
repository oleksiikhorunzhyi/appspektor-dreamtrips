package com.worldventures.dreamtrips.modules.player;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;

import com.worldventures.dreamtrips.modules.common.view.activity.BaseActivity;

import butterknife.InjectView;

@Layout(R.layout.activity_podcast_player)
public class PodcastPlayerActivity extends BaseActivity {

    @InjectView(R.id.toolbar_actionbar) protected Toolbar toolbar;

    @Override
    protected void afterCreateView(Bundle savedInstanceState) {
        super.afterCreateView(savedInstanceState);
        setSupportActionBar(this.toolbar);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
    }
}
