package com.worldventures.dreamtrips.modules.player;

import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.view.activity.BaseActivity;
import com.worldventures.dreamtrips.modules.player.view.PodcastPlayerScreenImpl;

import butterknife.InjectView;

@Layout(R.layout.activity_podcast_player)
public class PodcastPlayerActivity extends BaseActivity {

   @InjectView(R.id.toolbar_actionbar) protected Toolbar toolbar;
   @InjectView(R.id.view_podcast_player) protected PodcastPlayerScreenImpl podcastPlayerScreen;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setVolumeControlStream(AudioManager.STREAM_MUSIC);
   }

   @Override
   protected void afterCreateView(Bundle savedInstanceState) {
      super.afterCreateView(savedInstanceState);
      setSupportActionBar(this.toolbar);

      getSupportActionBar().setDisplayHomeAsUpEnabled(true);
      getSupportActionBar().setDisplayShowHomeEnabled(true);

      toolbar.setNavigationOnClickListener(v -> onBackPressed());
   }

   @Override
   protected void onNewIntent(Intent intent) {
      super.onNewIntent(intent);
   }

   @Override
   protected void onDestroy() {
      super.onDestroy();
   }

   @Override
   public void onBackPressed() {
      super.onBackPressed();
      podcastPlayerScreen.onBackPressed();
   }
}
