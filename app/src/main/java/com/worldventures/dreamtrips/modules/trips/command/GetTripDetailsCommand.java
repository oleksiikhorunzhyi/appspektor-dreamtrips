package com.worldventures.dreamtrips.modules.trips.command;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.api.action.CommandWithError;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.modules.trips.api.GetTripDetailsHttpAction;
import com.worldventures.dreamtrips.modules.trips.model.TripDetails;

import javax.inject.Inject;

import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class GetTripDetailsCommand extends CommandWithError<TripDetails> implements InjectableAction {

    @Inject Janet janet;

    private String tripId;

    public GetTripDetailsCommand(String tripId) {
        this.tripId = tripId;
    }

    @Override
    protected void run(CommandCallback<TripDetails> callback) throws Throwable {
        janet.createPipe(GetTripDetailsHttpAction.class)
                .createObservableResult(new GetTripDetailsHttpAction(tripId))
                .map(GetTripDetailsHttpAction::getTripDetails)
                .subscribe(callback::onSuccess, callback::onFail);
    }

    @Override
    public int getFallbackErrorMessage() {
        return R.string.error_fail_to_load_item_details;
    }
}
