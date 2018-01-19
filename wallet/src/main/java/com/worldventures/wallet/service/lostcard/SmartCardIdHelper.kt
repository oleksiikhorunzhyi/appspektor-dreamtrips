package com.worldventures.wallet.service.lostcard

import com.worldventures.core.model.session.UserSession
import rx.Observable

internal interface SmartCardIdHelper {

   fun smartCardIdObservable(): Observable<String?>

   fun fetchSmartCardFromServer(userSession: UserSession)
}
