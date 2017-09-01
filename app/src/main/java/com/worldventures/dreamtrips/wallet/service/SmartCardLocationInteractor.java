package com.worldventures.dreamtrips.wallet.service;


import com.worldventures.dreamtrips.core.janet.SessionActionPipeCreator;
import com.worldventures.dreamtrips.wallet.service.lostcard.command.DetectGeoLocationCommand;
import com.worldventures.dreamtrips.wallet.service.lostcard.command.FetchAddressWithPlacesCommand;
import com.worldventures.dreamtrips.wallet.service.lostcard.command.FetchTrackingStatusCommand;
import com.worldventures.dreamtrips.wallet.service.lostcard.command.GetLocationCommand;
import com.worldventures.dreamtrips.wallet.service.lostcard.command.PostLocationCommand;
import com.worldventures.dreamtrips.wallet.service.lostcard.command.UpdateTrackingStatusCommand;
import com.worldventures.dreamtrips.wallet.service.lostcard.command.WalletLocationCommand;

import io.techery.janet.ActionPipe;
import io.techery.janet.ReadActionPipe;
import io.techery.janet.smartcard.action.support.ConnectAction;
import io.techery.janet.smartcard.action.support.DisconnectAction;
import rx.schedulers.Schedulers;

public final class SmartCardLocationInteractor {

   private final ReadActionPipe<ConnectAction> connectionPipe;
   private final ReadActionPipe<DisconnectAction> disconnectPipe;
   private final ActionPipe<WalletLocationCommand> walletLocationCommandPipe;
   private final ActionPipe<PostLocationCommand> postLocationPipe;
   private final ActionPipe<GetLocationCommand> getLocationPipe;
   private final ActionPipe<DetectGeoLocationCommand> detectGeoLocationPipe;
   private final ActionPipe<FetchAddressWithPlacesCommand> fetchAddressPipe;
   private final ActionPipe<FetchTrackingStatusCommand> fetchTrackingStatusPipe;
   private final ActionPipe<UpdateTrackingStatusCommand> updateTrackingStatusPipe;

   public SmartCardLocationInteractor(SessionActionPipeCreator pipeCreator) {
      connectionPipe = pipeCreator.createPipe(ConnectAction.class);
      disconnectPipe = pipeCreator.createPipe(DisconnectAction.class);

      walletLocationCommandPipe = pipeCreator.createPipe(WalletLocationCommand.class, Schedulers.io());

      postLocationPipe = pipeCreator.createPipe(PostLocationCommand.class, Schedulers.io());
      getLocationPipe = pipeCreator.createPipe(GetLocationCommand.class, Schedulers.io());

      detectGeoLocationPipe = pipeCreator.createPipe(DetectGeoLocationCommand.class, Schedulers.io());
      fetchAddressPipe = pipeCreator.createPipe(FetchAddressWithPlacesCommand.class, Schedulers.io());

      fetchTrackingStatusPipe = pipeCreator.createPipe(FetchTrackingStatusCommand.class, Schedulers.io());
      updateTrackingStatusPipe = pipeCreator.createPipe(UpdateTrackingStatusCommand.class, Schedulers.io());
   }

   public ReadActionPipe<ConnectAction> connectActionPipe() {
      return connectionPipe;
   }

   public ReadActionPipe<DisconnectAction> disconnectPipe() {
      return disconnectPipe;
   }

   public ActionPipe<WalletLocationCommand> walletLocationCommandPipe() {
      return walletLocationCommandPipe;
   }

   public ActionPipe<PostLocationCommand> postLocationPipe() {
      return postLocationPipe;
   }

   public ActionPipe<GetLocationCommand> getLocationPipe() {
      return getLocationPipe;
   }

   public ActionPipe<DetectGeoLocationCommand> detectGeoLocationPipe() {
      return detectGeoLocationPipe;
   }

   public ActionPipe<FetchAddressWithPlacesCommand> fetchAddressPipe() {
      return fetchAddressPipe;
   }

   public ActionPipe<FetchTrackingStatusCommand> fetchTrackingStatusPipe() {
      return fetchTrackingStatusPipe;
   }

   public ActionPipe<UpdateTrackingStatusCommand> updateTrackingStatusPipe() {
      return updateTrackingStatusPipe;
   }
}
