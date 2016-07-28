package com.worldventures.dreamtrips.modules.feed.service.command;

import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.modules.feed.model.comment.Comment;
import com.worldventures.dreamtrips.modules.feed.service.api.GetCommentsHttpAction;

import java.util.List;

import javax.inject.Inject;

import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import rx.schedulers.Schedulers;

@CommandAction
public class GetCommentsCommand extends Command<List<Comment>> implements InjectableAction {

    public static final int LIMIT = 10;

    @Inject Janet janet;

    private final String itemUid;
    private final int page;

    public GetCommentsCommand(String itemUid, int page) {
        this.itemUid = itemUid;
        this.page = page;
    }

    @Override
    protected void run(CommandCallback<List<Comment>> callback) throws Throwable {
        janet.createPipe(GetCommentsHttpAction.class, Schedulers.io())
                .createObservableResult(new GetCommentsHttpAction(itemUid, page, LIMIT))
                .map(GetCommentsHttpAction::response)
                .subscribe(callback::onSuccess, callback::onFail);
    }
}
