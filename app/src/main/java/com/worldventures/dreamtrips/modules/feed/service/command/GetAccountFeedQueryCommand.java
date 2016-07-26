package com.worldventures.dreamtrips.modules.feed.service.command;

import com.innahema.collections.query.queriables.Queryable;
import com.messenger.api.UiErrorAction;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.core.rx.composer.ListFilter;
import com.worldventures.dreamtrips.core.rx.composer.ListMapper;
import com.worldventures.dreamtrips.modules.feed.api.GetAccountFeedsQueryHttpAction;
import com.worldventures.dreamtrips.modules.feed.model.FeedEntity;
import com.worldventures.dreamtrips.modules.feed.model.FeedItem;
import com.worldventures.dreamtrips.modules.feed.model.feed.base.ParentFeedItem;

import java.util.List;

import javax.inject.Inject;

import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import rx.schedulers.Schedulers;

public class GetAccountFeedQueryCommand extends Command<List<FeedItem<FeedEntity>>> implements InjectableAction, UiErrorAction {

    private static final int LIMIT = 20;

    @Inject Janet janet;
    @Inject SnappyRepository snappyRepository;

    private String circleId;
    private String before;

    public GetAccountFeedQueryCommand(String circleId, String before) {
        this.circleId = circleId;
        this.before = before;
    }

    @Override
    protected void run(CommandCallback<List<FeedItem<FeedEntity>>> callback) throws Throwable {
        janet.createPipe(GetAccountFeedsQueryHttpAction.class, Schedulers.io())
                .createObservableResult(new GetAccountFeedsQueryHttpAction(circleId, LIMIT, before))
                .map(GetAccountFeedsQueryHttpAction::getResponseItems)
                .compose(new ListFilter<>(ParentFeedItem::isSingle))
                .compose(new ListMapper<>(parentFeedItem -> parentFeedItem.getItems().get(0)))
                .doOnNext(this::mixCachedTranslations)
                .subscribe(callback::onSuccess, callback::onFail);
    }

    private void mixCachedTranslations(List<FeedItem<FeedEntity>> feedItems) {
        Queryable.from(feedItems)
                .map(FeedItem::getItem)
                .forEachR(feedEntity -> feedEntity.setTranslation(snappyRepository.getTranslation(feedEntity.getUid())));
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
