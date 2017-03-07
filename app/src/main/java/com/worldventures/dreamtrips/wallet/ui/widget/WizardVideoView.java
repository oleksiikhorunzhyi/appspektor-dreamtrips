package com.worldventures.dreamtrips.wallet.ui.widget;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.annotation.RawRes;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.VideoView;

import com.worldventures.dreamtrips.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class WizardVideoView extends FrameLayout {

   public static final long CROSSFADE_DURATION = 1000;

   @InjectView(R.id.videoView) VideoView videoView;
   @InjectView(R.id.crossFade) View crossfadeView;

   public WizardVideoView(Context context) {
      this(context, null);
   }

   public WizardVideoView(Context context, @Nullable AttributeSet attrs) {
      super(context, attrs);
      setup();
   }

   private void setup() {
      LayoutInflater.from(getContext()).inflate(R.layout.custom_view_wallet_wizard_video, this);
      ButterKnife.inject(this);
   }

   public void playVideo(@RawRes int videoResId) {
      final Uri videoUri = new Uri.Builder()
            .scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
            .authority(getResources().getResourcePackageName(videoResId))
            .appendPath(getResources().getResourceTypeName(videoResId))
            .appendPath(getResources().getResourceEntryName(videoResId))
            .build();

      crossfadeView.animate().alpha(0f).setDuration(CROSSFADE_DURATION);
      crossfadeView.setOnClickListener(view -> {
         videoView.seekTo(0);
         videoView.start();
      });

      // Hack to fix black frames on video start
      videoView.setBackgroundColor(Color.WHITE);
      videoView.setOnInfoListener((mediaPlayer, i, i1) -> {
         if (i == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START) {
            videoView.setBackgroundColor(Color.TRANSPARENT);
         }
         return false;
      });

      videoView.setVideoURI(videoUri);
      videoView.start();
   }

}
