package com.worldventures.dreamtrips.wallet.ui.widget;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Color;
import android.graphics.SurfaceTexture;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.annotation.RawRes;
import android.support.percent.PercentFrameLayout;
import android.support.percent.PercentLayoutHelper;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;

import com.worldventures.dreamtrips.R;

import java.io.IOException;

import butterknife.ButterKnife;
import butterknife.InjectView;
import timber.log.Timber;

public class WizardVideoView extends PercentFrameLayout implements TextureView.SurfaceTextureListener {

   public static final long CROSSFADE_DURATION = 1000;

   @InjectView(R.id.textureView) TextureView textureView;
   @InjectView(R.id.crossFade) View crossfadeView;
   private MediaPlayer mediaPlayer;
   private Uri videoUri;

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
      textureView.setBackgroundColor(Color.WHITE);
      textureView.setSurfaceTextureListener(this);
      crossfadeView.setOnClickListener(view -> {
         mediaPlayer.seekTo(0);
         mediaPlayer.start();
      });
   }

   @Override
   protected void onAttachedToWindow() {
      super.onAttachedToWindow();
      textureView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
   }

   @Override
   protected void onDetachedFromWindow() {
      super.onDetachedFromWindow();
      textureView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
   }

   public void setVideoSource(@RawRes int videoResId) {
      videoUri = new Uri.Builder()
            .scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
            .authority(getResources().getResourcePackageName(videoResId))
            .appendPath(getResources().getResourceTypeName(videoResId))
            .appendPath(getResources().getResourceEntryName(videoResId))
            .build();
      calibrateVideoDimensions();
   }

   private void setupMediaPlayer(Surface surface) {
      crossfadeView.animate().alpha(0f).setDuration(CROSSFADE_DURATION);
      mediaPlayer = new MediaPlayer();
      mediaPlayer.setOnInfoListener((mediaPlayer, i, i1) -> {
         if (i == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START) {
            textureView.setBackgroundColor(Color.TRANSPARENT);
         }
         return false;
      });
      try {
         mediaPlayer.setDataSource(getContext(), videoUri);
      } catch (IOException e) {
         Timber.e(e, "");
      }
      mediaPlayer.setOnPreparedListener(MediaPlayer::start);
      mediaPlayer.setSurface(surface);
      mediaPlayer.prepareAsync();
   }

   private void calibrateVideoDimensions() {
      if (isInEditMode()) return;

      try {
         MediaMetadataRetriever metaRetriever = new MediaMetadataRetriever();
         metaRetriever.setDataSource(getContext(), videoUri);
         String width = metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);
         String height = metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT);

         float aspectRatio = Float.parseFloat(width) / Float.parseFloat(height);

         ((PercentLayoutHelper.PercentLayoutParams) textureView.getLayoutParams()).getPercentLayoutInfo().aspectRatio = aspectRatio;
         ((PercentLayoutHelper.PercentLayoutParams) crossfadeView.getLayoutParams()).getPercentLayoutInfo().aspectRatio = aspectRatio;
      } catch (Exception e) {
         Timber.e(e, "%s", e.getMessage());
      }
   }

   @Override
   public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int width, int height) {
      final Surface surface = new Surface(surfaceTexture);
      setupMediaPlayer(surface);
   }

   @Override
   public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

   }

   @Override
   public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
      crossfadeView.animate().alpha(1f).setDuration(0);
      mediaPlayer.stop();
      mediaPlayer.release();
      return true;
   }

   @Override
   public void onSurfaceTextureUpdated(SurfaceTexture surface) {

   }
}
