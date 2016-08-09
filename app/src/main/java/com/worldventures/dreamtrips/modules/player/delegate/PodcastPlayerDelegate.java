package com.worldventures.dreamtrips.modules.player.delegate;

import android.net.Uri;

import com.worldventures.dreamtrips.modules.player.playback.DtPlayer;

import rx.Observable;

public class PodcastPlayerDelegate {

    public Observable<DtPlayer> getPlayer(Uri uri) {
        return Observable.empty();
    }
}
