package com.worldventures.dreamtrips.modules.trips.service.command

import com.worldventures.core.janet.ValueCommandAction
import com.worldventures.dreamtrips.modules.trips.model.filter.TripsFilterData
import io.techery.janet.command.annotations.CommandAction

@CommandAction
class TripFiltersAppliedCommand(tripsFilterData: TripsFilterData = TripsFilterData()) : ValueCommandAction<TripsFilterData>(tripsFilterData)
