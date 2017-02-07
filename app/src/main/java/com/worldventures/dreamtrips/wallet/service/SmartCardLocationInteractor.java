package com.worldventures.dreamtrips.wallet.service;


import com.worldventures.dreamtrips.core.janet.SessionActionPipeCreator;
import com.worldventures.dreamtrips.wallet.service.command.ConnectSmartCardCommand;
import com.worldventures.dreamtrips.wallet.service.lostcard.command.DetectGeoLocationCommand;
import com.worldventures.dreamtrips.wallet.service.lostcard.command.FetchAddressCommand;
import com.worldventures.dreamtrips.wallet.service.lostcard.command.GetEnabledTrackingCommand;
import com.worldventures.dreamtrips.wallet.service.lostcard.command.GetLocationCommand;
import com.worldventures.dreamtrips.wallet.service.lostcard.command.PostLocationCommand;
import com.worldventures.dreamtrips.wallet.service.lostcard.command.SaveEnabledTrackingCommand;
import com.worldventures.dreamtrips.wallet.service.lostcard.command.WalletLocationCommand;

import io.techery.janet.ActionPipe;
import io.techery.janet.smartcard.action.support.DisconnectAction;
import rx.schedulers.Schedulers;

public final class SmartCardLocationInteractor {

   private final ActionPipe<ConnectSmartCardCommand> connectionPipe;
   private final ActionPipe<DisconnectAction> disconnectPipe;
   private final ActionPipe<WalletLocationCommand> walletLocationCommandPipe;
   private final ActionPipe<PostLocationCommand> postLocationPipe;
   private final ActionPipe<GetLocationCommand> getLocationPipe;
   private final ActionPipe<DetectGeoLocationCommand> detectGeoLocationPipe;
   private final ActionPipe<FetchAddressCommand> fetchAddressPipe;

   private final ActionPipe<SaveEnabledTrackingCommand> saveEnabledTrackingPipe;
   private final ActionPipe<GetEnabledTrackingCommand> enabledTrackingCommandActionPipe;

   public SmartCardLocationInteractor(SessionActionPipeCreator pipeCreator) {
      connectionPipe = pipeCreator.createPipe(ConnectSmartCardCommand.class, Schedulers.io());
      disconnectPipe = pipeCreator.createPipe(DisconnectAction.class, Schedulers.io());

      walletLocationCommandPipe = pipeCreator.createPipe(WalletLocationCommand.class, Schedulers.io());

      postLocationPipe = pipeCreator.createPipe(PostLocationCommand.class, Schedulers.io());
      getLocationPipe = pipeCreator.createPipe(GetLocationCommand.class, Schedulers.io());

      detectGeoLocationPipe = pipeCreator.createPipe(DetectGeoLocationCommand.class, Schedulers.io());
      fetchAddressPipe = pipeCreator.createPipe(FetchAddressCommand.class, Schedulers.io());

      saveEnabledTrackingPipe = pipeCreator.createPipe(SaveEnabledTrackingCommand.class, Schedulers.io());
      enabledTrackingCommandActionPipe = pipeCreator.createPipe(GetEnabledTrackingCommand.class, Schedulers.io());
   }

   public ActionPipe<ConnectSmartCardCommand> connectActionPipe() {
      return connectionPipe;
   }

   public ActionPipe<DisconnectAction> disconnectPipe() {
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

   public ActionPipe<FetchAddressCommand> fetchAddressPipe() {
      return fetchAddressPipe;
   }

   public ActionPipe<SaveEnabledTrackingCommand> saveEnabledTrackingPipe() {
      return saveEnabledTrackingPipe;
   }

   public ActionPipe<GetEnabledTrackingCommand> enabledTrackingCommandActionPipe() {
      return enabledTrackingCommandActionPipe;
   }
}
