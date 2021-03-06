package com.worldventures.wallet.ui.settings.security.lostcard;

import com.hannesdorfmann.mosby.mvp.MvpView;
import com.worldventures.wallet.domain.entity.lostcard.WalletCoordinates;
import com.worldventures.wallet.service.lostcard.command.FetchAddressWithPlacesCommand;
import com.worldventures.wallet.ui.settings.security.lostcard.model.LostCardPin;

import java.util.Date;

import io.techery.janet.operationsubscriber.view.OperationView;
import rx.Observable;

public interface MapScreen extends MvpView {

   void addPin(LostCardPin lostCardPin);

   void setCoordinates(WalletCoordinates position);

   void setLastConnectionDate(Date date);

   OperationView<FetchAddressWithPlacesCommand> provideOperationView();

   <T> Observable.Transformer<T, T> bindUntilDetach();
}
