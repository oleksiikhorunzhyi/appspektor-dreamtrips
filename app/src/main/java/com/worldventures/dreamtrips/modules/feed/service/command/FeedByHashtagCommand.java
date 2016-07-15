package com.worldventures.dreamtrips.modules.feed.service.command;

import com.innahema.collections.query.queriables.Queryable;
import com.messenger.api.UiErrorAction;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.core.utils.DateTimeUtils;
import com.worldventures.dreamtrips.modules.feed.api.GetFeedsByHashtagHttpAction;
import com.worldventures.dreamtrips.modules.feed.model.DataMetaData;
import com.worldventures.dreamtrips.modules.feed.model.FeedEntity;
import com.worldventures.dreamtrips.modules.feed.model.FeedItem;

import java.util.Date;

import javax.inject.Inject;

import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import rx.schedulers.Schedulers;

public class FeedByHashtagCommand extends Command<DataMetaData> implements InjectableAction, UiErrorAction {

    @Inject
    Janet janet;

    private String query;
    private int perPage;
    private Date before;

    public FeedByHashtagCommand(String query, int perPage, Date before) {
        this.query = query;
        this.perPage = perPage;
        this.before = before;
    }

    @Override
    protected void run(CommandCallback<DataMetaData> callback) throws Throwable {
        janet.createPipe(GetFeedsByHashtagHttpAction.class, Schedulers.io())
                .createObservableResult(new GetFeedsByHashtagHttpAction(query, perPage, DateTimeUtils.convertDateToUTCString(before)))
                .map(GetFeedsByHashtagHttpAction :: getResponseItems)
                .doOnNext(dataMetaData -> shareMetaDataWithChildren(dataMetaData))
                .subscribe(callback:: onSuccess, callback::onFail);
    }

    private void shareMetaDataWithChildren(DataMetaData dataMetaData){
        Queryable.from(dataMetaData.getParentFeedItems())
                .forEachR(parentFeedItem -> Queryable.from(parentFeedItem.getItems())
                        .forEachR((com.innahema.collections.query.functions.Action1<FeedItem<FeedEntity>>) feedItem -> feedItem.setMetaData(dataMetaData.getMetaData())));
    }

    @Override
    public int getErrorMessage() {
        return R.string.error_fail_to_load_feeds_by_hashtag;
    }

    @CommandAction
    public static class LoadNext extends FeedByHashtagCommand{
        public LoadNext(String query, int perPage, Date before) {
            super(query, perPage, before);
        }
    }

    @CommandAction
    public static class Refresh extends FeedByHashtagCommand{
        public Refresh(String query, int perPage) {
            super(query, perPage, null);
        }
    }
}
