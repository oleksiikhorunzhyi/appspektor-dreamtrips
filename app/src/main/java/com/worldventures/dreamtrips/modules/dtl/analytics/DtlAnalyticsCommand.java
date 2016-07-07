package com.worldventures.dreamtrips.modules.dtl.analytics;

import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsInteractor;
import com.worldventures.dreamtrips.modules.dtl.service.DtlLocationInteractor;
import com.worldventures.dreamtrips.modules.dtl.service.action.DtlLocationCommand;

import javax.inject.Inject;

import io.techery.janet.Command;
import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class DtlAnalyticsCommand extends Command<Void> implements InjectableAction {

    @Inject
    protected AnalyticsInteractor analyticsInteractor;
    @Inject
    protected DtlLocationInteractor dtlLocationInteractor;

    private final DtlAnalyticsAction action;

    public static DtlAnalyticsCommand create(DtlAnalyticsAction action) {
        return new DtlAnalyticsCommand(action);
    }

    public DtlAnalyticsCommand(DtlAnalyticsAction action) {
        this.action = action;
    }

    @Override
    protected void run(CommandCallback<Void> callback) throws Throwable {
        dtlLocationInteractor.locationPipe()
                .createObservableResult(DtlLocationCommand.last())
                .map(DtlLocationCommand::getResult)
                .map(dtlLocation -> {
                    action.setAnalyticsLocation(dtlLocation);
                    return action;
                })
                .flatMap(action -> analyticsInteractor.analyticsActionPipe()
                        .createObservableResult(action))
                .map(baseAnalyticsAction -> (Void) null)
                .subscribe(callback::onSuccess, callback::onFail);
    }
}
