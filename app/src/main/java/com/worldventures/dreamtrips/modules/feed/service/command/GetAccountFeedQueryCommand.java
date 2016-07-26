package com.worldventures.dreamtrips.modules.feed.service.command;

import com.messenger.api.UiErrorAction;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.modules.feed.api.GetAccountFeedsQueryHttpAction;
import com.worldventures.dreamtrips.modules.feed.model.feed.base.ParentFeedItem;

import java.util.List;

import javax.inject.Inject;

import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import rx.schedulers.Schedulers;

public class GetAccountFeedQueryCommand extends Command<List<ParentFeedItem>> implements InjectableAction, UiErrorAction {

    private static final int LIMIT = 20;

    @Inject
    Janet janet;

    private String circleId;
    private String before;

    public GetAccountFeedQueryCommand(String circleId, String before) {
        this.circleId = circleId;
        this.before = before;
    }

    @Override
    protected void run(CommandCallback<List<ParentFeedItem>> callback) throws Throwable {
        janet.createPipe(GetAccountFeedsQueryHttpAction.class, Schedulers.io())
                .createObservableResult(new GetAccountFeedsQueryHttpAction(circleId, LIMIT, before))
                .map(GetAccountFeedsQueryHttpAction::getResponseItems)
                .subscribe(callback::onSuccess, callback::onFail);
    }

    @Override
    public int getErrorMessage() {
        return R.string.error_fail_to_load_feed;
    }

    @CommandAction
    public static class LoadNext extends GetAccountFeedQueryCommand {
        public LoadNext(String circleId, String before) {
            super(circleId, before);
        }
    }

    @CommandAction
    public static class Refresh extends GetAccountFeedQueryCommand {
        public Refresh(String circleId) {
            super(circleId, null);
        }
    }
}
