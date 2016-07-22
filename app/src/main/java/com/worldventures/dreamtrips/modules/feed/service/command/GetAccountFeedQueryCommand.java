package com.worldventures.dreamtrips.modules.feed.service.command;

import com.messenger.api.UiErrorAction;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.core.utils.DateTimeUtils;
import com.worldventures.dreamtrips.modules.feed.api.GeAccountFeedsQueryHttpAction;
import com.worldventures.dreamtrips.modules.feed.model.feed.base.ParentFeedItem;

import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import rx.schedulers.Schedulers;

public class GetAccountFeedQueryCommand extends Command<List<ParentFeedItem>> implements InjectableAction, UiErrorAction {

    @Inject
    Janet janet;

    private static final int LIMIT = 20;
    private String circleId;
    private Date before;

    public GetAccountFeedQueryCommand(String circleId, Date before) {
        this.circleId = circleId;
        this.before = before;
    }

    @Override
    protected void run(CommandCallback<List<ParentFeedItem>> callback) throws Throwable {
        String before = this.before == null ? null : DateTimeUtils.convertDateToUTCString(this.before);
        janet.createPipe(GeAccountFeedsQueryHttpAction.class, Schedulers.io())
                .createObservableResult(new GeAccountFeedsQueryHttpAction(circleId, LIMIT, before))
                .map(GeAccountFeedsQueryHttpAction::getResponseItems)
                .subscribe(callback::onSuccess, callback::onFail);
    }

    @Override
    public int getErrorMessage() {
        return R.string.error_fail_to_load_feed;
    }

    @CommandAction
    public static class LoadNext extends GetAccountFeedQueryCommand {
        public LoadNext(String circleId, Date before) {
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
