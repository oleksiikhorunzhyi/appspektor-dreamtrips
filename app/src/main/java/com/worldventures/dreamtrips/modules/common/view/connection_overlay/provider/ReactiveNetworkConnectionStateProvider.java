package com.worldventures.dreamtrips.modules.common.view.connection_overlay.provider;

import android.content.Context;

import com.github.pwittchen.reactivenetwork.library.Connectivity;
import com.github.pwittchen.reactivenetwork.library.ReactiveNetwork;
import com.worldventures.dreamtrips.core.flow.util.Utils;
import com.worldventures.dreamtrips.modules.common.view.connection_overlay.ConnectionState;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.subjects.PublishSubject;

public class ReactiveNetworkConnectionStateProvider implements ConnectionStateProvider<Connectivity> {

   private Context context;

   private PublishSubject<ConnectionState> connectionStatusObservable =
         PublishSubject.create();

   public ReactiveNetworkConnectionStateProvider(Context context) {
      this.context = context;
   }

   public void startMonitoringNetworkState(Observable stopper) {
      Observable.merge(Observable.just(null), ReactiveNetwork.observeNetworkConnectivity(context))
            .compose(bindToStopper(stopper))
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(this::reportConnectionState);
   }

   @Override
   public void reportConnectionState(Connectivity state) {
      ConnectionState connectionState = Utils.isConnected(context) ?
            ConnectionState.CONNECTED : ConnectionState.DISCONNECTED;
      connectionStatusObservable.onNext(connectionState);
   }

   @Override
   public Observable<ConnectionState> connectionStateObservable() {
      return connectionStatusObservable;
   }

   protected <T> Observable.Transformer<T, T> bindToStopper(Observable stopper) {
      return input -> input.takeUntil(stopper);
   }
}
