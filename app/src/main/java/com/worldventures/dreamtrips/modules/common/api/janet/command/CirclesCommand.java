package com.worldventures.dreamtrips.modules.common.api.janet.command;

import com.messenger.api.UiErrorAction;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.core.session.acl.Feature;
import com.worldventures.dreamtrips.core.session.acl.FeatureManager;
import com.worldventures.dreamtrips.modules.common.api.janet.GetCirclesHttpAction;
import com.worldventures.dreamtrips.modules.friends.model.Circle;

import java.util.ArrayList;

import javax.inject.Inject;

import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class CirclesCommand extends Command<ArrayList<Circle>> implements InjectableAction, UiErrorAction {

    @Inject
    SnappyRepository repository;
    @Inject
    Janet janet;

    @Override
    protected void run(CommandCallback<ArrayList<Circle>> callback) throws Throwable {
        janet.createPipe(GetCirclesHttpAction.class)
                .createObservableResult(new GetCirclesHttpAction())
                .map(GetCirclesHttpAction::getCircles)
                .doOnNext(getCirclesAction -> repository.saveCircles(getCirclesAction))
                .subscribe(callback::onSuccess, callback::onFail);
    }

    @Override
    public int getErrorMessage() {
        return R.string.error_fail_to_load_circles;
    }
}
