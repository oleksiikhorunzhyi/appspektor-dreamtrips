package com.worldventures.dreamtrips.modules.feed.service;

import com.worldventures.dreamtrips.modules.feed.service.command.TranslateUidItemCommand;

import javax.inject.Inject;

import io.techery.janet.ActionPipe;
import io.techery.janet.Janet;
import rx.schedulers.Schedulers;

public class TranslationFeedInteractor {

    private final ActionPipe<TranslateUidItemCommand.TranslateCommentCommand> translateCommentPipe;
    private final ActionPipe<TranslateUidItemCommand.TranslatePostCommand> translatePostPipe;

    @Inject
    public TranslationFeedInteractor(Janet janet) {
        translateCommentPipe = janet.createPipe(TranslateUidItemCommand.TranslateCommentCommand.class, Schedulers.io());
        translatePostPipe = janet.createPipe(TranslateUidItemCommand.TranslatePostCommand.class, Schedulers.io());
    }

    public ActionPipe<TranslateUidItemCommand.TranslateCommentCommand> translateCommentPipe() {
        return translateCommentPipe;
    }

    public ActionPipe<TranslateUidItemCommand.TranslatePostCommand> translatePostPipe() {
        return translatePostPipe;
    }
}
