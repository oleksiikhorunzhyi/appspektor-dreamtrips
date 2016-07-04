package com.worldventures.dreamtrips.modules.feed.command;

import com.messenger.api.UiErrorAction;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.modules.feed.api.GetFeedsByHashtagHttpAction;
import com.worldventures.dreamtrips.modules.feed.model.feed.base.ParentFeedItem;

import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import rx.schedulers.Schedulers;

@CommandAction
public class GetFeedsByHashtagsCommand extends Command<List<ParentFeedItem>> implements InjectableAction, UiErrorAction{
    @Inject
    Janet janet;

    private String query;
    private int perPage;
    private Date before;

    public GetFeedsByHashtagsCommand(String query, int perPage, Date before) {
        this.query = query;
        this.perPage = perPage;
        this.before = before;
    }

    @Override
    protected void run(CommandCallback<List<ParentFeedItem>> callback) throws Throwable {
        janet.createPipe(GetFeedsByHashtagHttpAction.class, Schedulers.io())
                .createObservableResult(new GetFeedsByHashtagHttpAction(query, perPage, before))
                .map(GetFeedsByHashtagHttpAction :: getResponseItems)
                .subscribe(callback:: onSuccess, callback::onFail);
    }

    @Override
    public int getErrorMessage() {
        return R.string.error_fail_to_load_feeds_by_hashtag;
    }
}
