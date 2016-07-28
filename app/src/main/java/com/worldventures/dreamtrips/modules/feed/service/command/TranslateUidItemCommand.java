package com.worldventures.dreamtrips.modules.feed.service.command;

import com.messenger.api.UiErrorAction;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.modules.feed.model.TextualPost;
import com.worldventures.dreamtrips.modules.feed.model.TranslatableItem;
import com.worldventures.dreamtrips.modules.feed.model.comment.Comment;

import javax.inject.Inject;

import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import rx.schedulers.Schedulers;

public abstract class TranslateUidItemCommand<T extends TranslatableItem> extends Command<T> implements InjectableAction, UiErrorAction {

    @Inject  Janet janet;

    private T translatableItem;
    private String languageTo;

    public TranslateUidItemCommand(T translatableItem, String languageTo) {
        this.translatableItem = translatableItem;
        this.languageTo = languageTo;
    }

    @Override
    protected void run(CommandCallback<T> callback) throws Throwable {
        janet.createPipe(TranslateTextCachedCommand.class, Schedulers.io())
                .createObservableResult(new TranslateTextCachedCommand(translatableItem.getOriginalText(),
                        languageTo))
                .map(TranslateTextCachedCommand::getResult)
                .map(this::mapResult)
                .subscribe(callback::onSuccess, callback::onFail);
    }

    protected T mapResult(String translatedText) {
        translatableItem.setTranslation(translatedText);
        translatableItem.setTranslated(true);
        return translatableItem;
    }

    public static TranslateCommentCommand forComment(Comment comment, String languageTo) {
        return new TranslateCommentCommand(comment, languageTo);
    }

    public static TranslatePostCommand forPost(TextualPost textualPost, String languageTo) {
        return new TranslatePostCommand(textualPost, languageTo);
    }

    @CommandAction
    public static class TranslateCommentCommand extends TranslateUidItemCommand<Comment> {
        public TranslateCommentCommand(Comment translatableItem, String languageTo) {
            super(translatableItem, languageTo);
        }
    }

    @CommandAction
    public static class TranslatePostCommand extends TranslateUidItemCommand<TextualPost> {
        public TranslatePostCommand(TextualPost translatableItem, String languageTo) {
            super(translatableItem, languageTo);
        }
    }

    @Override
    public int getErrorMessage() {
        return R.string.error_fail_to_translate_text;
    }
}
