package com.worldventures.dreamtrips.wallet.service;


import com.worldventures.dreamtrips.core.janet.SessionActionPipeCreator;
import com.worldventures.dreamtrips.wallet.service.command.ConnectSmartCardCommand;
import com.worldventures.dreamtrips.wallet.service.lostcard.command.PostLocationCommand;
import com.worldventures.dreamtrips.wallet.service.lostcard.command.DetectGeoLocationCommand;
import com.worldventures.dreamtrips.wallet.service.lostcard.command.FetchAddressCommand;
import com.worldventures.dreamtrips.wallet.service.lostcard.command.GetLocationCommand;

import io.techery.janet.ActionPipe;
import io.techery.janet.Janet;
import io.techery.janet.smartcard.action.support.DisconnectAction;
import rx.schedulers.Schedulers;

public final class SmartCardLocationInteractor {

   private final ActionPipe<ConnectSmartCardCommand> connectionPipe;
   private final ActionPipe<DisconnectAction> disconnectPipe;
   private final ActionPipe<PostLocationCommand> postLocationPipe;
   private final ActionPipe<GetLocationCommand> getLocationPipe;
   private final ActionPipe<DetectGeoLocationCommand> detectGeoLocationPipe;
   private final ActionPipe<FetchAddressCommand> fetchAddressPipe;

   public SmartCardLocationInteractor(SessionActionPipeCreator pipeCreator) {
      connectionPipe = pipeCreator.createPipe(ConnectSmartCardCommand.class, Schedulers.io());
      disconnectPipe = pipeCreator.createPipe(DisconnectAction.class, Schedulers.io());

      postLocationPipe = pipeCreator.createPipe(PostLocationCommand.class, Schedulers.io());
      getLocationPipe = pipeCreator.createPipe(GetLocationCommand.class, Schedulers.io());

      detectGeoLocationPipe = pipeCreator.createPipe(DetectGeoLocationCommand.class, Schedulers.io());
      fetchAddressPipe = pipeCreator.createPipe(FetchAddressCommand.class, Schedulers.io());
   }

   public ActionPipe<ConnectSmartCardCommand> connectActionPipe() {
      return connectionPipe;
   }

   public ActionPipe<DisconnectAction> disconnectPipe() {
      return disconnectPipe;
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
}
