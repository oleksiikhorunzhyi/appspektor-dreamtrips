package com.worldventures.dreamtrips.modules.membership.command;

import com.messenger.api.UiErrorAction;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.modules.membership.api.GetPodcastsHttpAction;
import com.worldventures.dreamtrips.modules.membership.model.Podcast;

import java.util.List;

import javax.inject.Inject;

import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;


@CommandAction
public class PodcastCommand extends Command<List<Podcast>> implements InjectableAction, UiErrorAction {
    @Inject
    Janet janet;
    private int page;
    private int perPage;

    public PodcastCommand(int page, int perPage) {
        this.page = page;
        this.perPage = perPage;
    }

    @Override
    protected void run(Command.CommandCallback<List<Podcast>> callback) throws Throwable {
        janet.createPipe(GetPodcastsHttpAction.class)
                .createObservableResult(new GetPodcastsHttpAction(page, perPage))
                .map(GetPodcastsHttpAction::getResponseItems)
                .subscribe(callback::onSuccess, callback::onFail);
    }

    @Override
    public int getErrorMessage() {
        return R.string.error_fail_to_load_podcast;
    }
}
