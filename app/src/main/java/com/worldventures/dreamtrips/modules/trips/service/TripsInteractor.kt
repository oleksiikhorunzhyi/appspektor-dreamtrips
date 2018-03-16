package com.worldventures.dreamtrips.modules.trips.service

import com.worldventures.core.janet.SessionActionPipeCreator
import com.worldventures.dreamtrips.modules.trips.service.command.CheckTripsByUidCommand
import com.worldventures.dreamtrips.modules.trips.service.command.GetTripDetailsCommand
import com.worldventures.dreamtrips.modules.trips.service.command.GetTripsByUidCommand
import com.worldventures.dreamtrips.modules.trips.service.command.GetTripsCommand
import com.worldventures.dreamtrips.modules.trips.service.command.GetTripsFilterDataCommand
import com.worldventures.dreamtrips.modules.trips.service.command.GetTripsLocationsCommand
import com.worldventures.dreamtrips.modules.trips.service.command.TripFiltersAppliedCommand
import rx.schedulers.Schedulers

class TripsInteractor(pipeCreator: SessionActionPipeCreator) {

   val tripsPipe = pipeCreator.createPipe(GetTripsCommand::class.java, Schedulers.io())
   val detailsPipe = pipeCreator.createPipe(GetTripDetailsCommand::class.java, Schedulers.io())
   val tripsByUidPipe = pipeCreator.createPipe(GetTripsByUidCommand::class.java, Schedulers.io())
   val checkTripsByUidPipe = pipeCreator.createPipe(CheckTripsByUidCommand::class.java, Schedulers.io())
   val mapObjectsActionPipe = pipeCreator.createPipe(GetTripsLocationsCommand::class.java, Schedulers.io())
   val tripFiltersPipe = pipeCreator.createPipe(GetTripsFilterDataCommand::class.java, Schedulers.io())
   val tripFiltersAppliedPipe = pipeCreator.createPipe(TripFiltersAppliedCommand::class.java, Schedulers.io())

   init {
      tripFiltersAppliedPipe.send(TripFiltersAppliedCommand())
   }
}
