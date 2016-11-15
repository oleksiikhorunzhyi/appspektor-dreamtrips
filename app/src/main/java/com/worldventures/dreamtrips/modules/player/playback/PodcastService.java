package com.worldventures.dreamtrips.modules.player.playback;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.NotificationManagerCompat;
import android.text.TextUtils;

import com.worldventures.dreamtrips.core.rx.composer.NonNullFilter;
import com.worldventures.dreamtrips.modules.player.delegate.audiofocus.AudioFocusDelegate;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class PodcastService extends Service {

   private static final int WIDGET_NOTIFICATION_ID = 144;

   static final String NOTIFICATION_ACTION_PLAY = "ACTION_PODCAST_PLAY";
   static final String NOTIFICATION_ACTION_PAUSE = "ACTION_PODCAST_PAUSE";
   static final String NOTIFICATION_ACTION_STOP = "ACTION_PODCAST_STOP";

   private final PodcastServiceBinder binder = new PodcastServiceBinder();

   private DtPlayer player;
   private Subscription activePlayerSubscription;
   // player can be tried to initialized from different threads
   private volatile boolean isPreparingPlayer = false;

   private AudioFocusDelegate audioFocusDelegate;
   private Subscription audioFocusSubscription;
   private boolean wasPausedByTemporaryAudioFocusLoss;

   private BroadcastReceiver headphonesReceiver;
   private boolean headhphonesReceiverRegistered;

   private PodcastServiceNotificationFactory notificationFactory;

   @Override
   public IBinder onBind(Intent intent) {
      return binder;
   }

   @Override
   public int onStartCommand(Intent intent, int flags, int startId) {
      String action = intent.getAction();
      if (!TextUtils.isEmpty(action)) {
         processIntentAction(action);
      }
      return START_NOT_STICKY;
   }

   private void processIntentAction(String action) {
      switch (action) {
         case NOTIFICATION_ACTION_PLAY:
            startPlayer();
            break;
         case NOTIFICATION_ACTION_PAUSE:
            pausePlayer();
            break;
         case NOTIFICATION_ACTION_STOP:
            onPlayerCleanup();
            break;
      }
   }

   ///////////////////////////////////////////////////////////////////////////
   // Player creation
   ///////////////////////////////////////////////////////////////////////////

   public Observable<ReadOnlyPlayer> createPlayer(Uri uri) {
      Observable<ReadOnlyPlayer> observable = Observable.just(player)
            .filter(player -> player != null && player.getSourceUri().equals(uri))
            .compose(new NonNullFilter<>())
            .switchIfEmpty(Observable.fromCallable(() -> createPlayerInternal(uri)))
            .flatMap(this::preparePlayerIfNeeded);
      observable = observable.replay(1).autoConnect();
      observable.subscribe();
      return observable;
   }

   private DtPlayer createPlayerInternal(Uri uri) {
      releasePlayerIfNeeded();
      player = new DtMediaPlayer(this, uri);
      activePlayerSubscription = player.getStateObservable()
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(state -> {
               Timber.d("Podcasts -- service -- state %s", state);
               if (state == DtPlayer.State.PLAYING) {
                  startForeground(WIDGET_NOTIFICATION_ID, notificationFactory.getPlayingNotification());
               } else if (state == DtPlayer.State.PAUSED) {
                  NotificationManagerCompat.from(getApplicationContext())
                        .notify(WIDGET_NOTIFICATION_ID, notificationFactory.getPausedNotification());
                  stopForeground(false);
               } else if (state == DtPlayer.State.STOPPED || state == DtPlayer.State.RELEASED || state == DtPlayer.State.ERROR) {
                  onPlayerCleanup();
               }
            });

      notificationFactory = new PodcastServiceNotificationFactory(this, uri);

      wasPausedByTemporaryAudioFocusLoss = false;

      return player;
   }

   private Observable<DtPlayer> preparePlayerIfNeeded(DtPlayer dtPlayer) {
      Observable<DtPlayer> playerObservable = Observable.just(player);
      if (player.getState() == DtPlayer.State.UNKNOWN) {
         playerObservable = playerObservable.subscribeOn(Schedulers.io()).doOnNext(player -> {
            if (isPreparingPlayer) return;
            isPreparingPlayer = true;
            player.prepare();
            isPreparingPlayer = false;
         }).observeOn(AndroidSchedulers.mainThread());
      }
      return playerObservable;
   }

   ///////////////////////////////////////////////////////////////////////////
   // Player operations
   ///////////////////////////////////////////////////////////////////////////

   public void startPlayer() {
      if (player == null) return;

      registerHeadphonesReceiver();
      wasPausedByTemporaryAudioFocusLoss = false;
      if (audioFocusDelegate == null) {
         audioFocusDelegate = new AudioFocusDelegate(getApplicationContext());
      }
      AudioFocusDelegate.AudioFocusState currentState = audioFocusDelegate.requestFocus().toBlocking().first();
      if (currentState == AudioFocusDelegate.AudioFocusState.GAINED) {
         player.start();
         if (audioFocusSubscription != null) {
            audioFocusSubscription.unsubscribe();
         }
         audioFocusSubscription = audioFocusDelegate.getAudioFocusObservable().subscribe(this::processAudioFocusState);
      }
   }

   public void pausePlayer() {
      if (player == null) return;
      pausePlayerInternal(true);
   }

   private void pausePlayerInternal(boolean abandonAudiofocusCompletely) {
      player.pause();
      if (abandonAudiofocusCompletely) abandonAudioFocusCompletely();
      unregisterHeadphonesReceiver();
   }

   public void stopPlayer() {
      if (player != null) {
         // according to specs we do full player release currently
         onPlayerCleanup();
      }
   }

   private void onPlayerCleanup() {
      releasePlayerIfNeeded();
      stopForeground(true);
      stopSelf();
      abandonAudioFocusCompletely();
      unregisterHeadphonesReceiver();
   }

   private void releasePlayerIfNeeded() {
      if (activePlayerSubscription != null) {
         activePlayerSubscription.unsubscribe();
         activePlayerSubscription = null;
      }
      if (player != null) {
         if (player.getState() != DtPlayer.State.STOPPED) player.stop();
         if (player.getState() != DtPlayer.State.RELEASED) player.release();
         player = null;
      }
      isPreparingPlayer = false;
   }

   public void seekTo(int position) {
      if (player != null) player.seekTo(position);
   }

   ///////////////////////////////////////////////////////////////////////////
   // Audio focus
   ///////////////////////////////////////////////////////////////////////////

   private void processAudioFocusState(AudioFocusDelegate.AudioFocusState audioFocusState) {
      if (player == null) return;
      if (audioFocusState == AudioFocusDelegate.AudioFocusState.LOSS || audioFocusState == AudioFocusDelegate.AudioFocusState.LOSS_TRANSIENT) {
         if (!player.isPlaying()) return;
         if (audioFocusState == AudioFocusDelegate.AudioFocusState.LOSS_TRANSIENT) {
            wasPausedByTemporaryAudioFocusLoss = true;
         }
         boolean shouldAbandonAudioFocusCompletely = !wasPausedByTemporaryAudioFocusLoss;
         pausePlayerInternal(shouldAbandonAudioFocusCompletely);
      } else if (audioFocusState == AudioFocusDelegate.AudioFocusState.GAINED) {
         if (wasPausedByTemporaryAudioFocusLoss) {
            startPlayer();
         }
      }
   }

   private void abandonAudioFocusCompletely() {
      wasPausedByTemporaryAudioFocusLoss = false;
      if (audioFocusSubscription != null) {
         audioFocusSubscription.unsubscribe();
         audioFocusSubscription = null;
      }
      if (audioFocusDelegate != null) audioFocusDelegate.abandonFocus();
      audioFocusDelegate = null;
   }

   ///////////////////////////////////////////////////////////////////////////
   // Headphones
   ///////////////////////////////////////////////////////////////////////////

   private void registerHeadphonesReceiver() {
      if (!headhphonesReceiverRegistered) {
         headphonesReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
               pausePlayer();
            }
         };
         getApplicationContext().registerReceiver(headphonesReceiver, new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY));
      }
      headhphonesReceiverRegistered = true;
   }

   private void unregisterHeadphonesReceiver() {
      if (headhphonesReceiverRegistered && headphonesReceiver != null) {
         getApplicationContext().unregisterReceiver(headphonesReceiver);
         headphonesReceiver = null;
      }
      headhphonesReceiverRegistered = false;
   }

   ///////////////////////////////////////////////////////////////////////////
   // Lifecycle and helpers
   ///////////////////////////////////////////////////////////////////////////

   @Override
   public void onDestroy() {
      super.onDestroy();
      releasePlayerIfNeeded();
   }

   @Override
   public void onTaskRemoved(Intent rootIntent) {
      super.onTaskRemoved(rootIntent);
      onPlayerCleanup();
   }

   public class PodcastServiceBinder extends Binder {
      public PodcastService getPodcastService() {
         return PodcastService.this;
      }
   }
}