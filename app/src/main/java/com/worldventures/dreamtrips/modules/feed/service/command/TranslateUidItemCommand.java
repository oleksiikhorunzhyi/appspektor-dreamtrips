package com.worldventures.dreamtrips.modules.feed.service.command;

import com.messenger.api.TranslationInteractor;
import com.worldventures.dreamtrips.api.messenger.TranslateTextHttpAction;
import com.worldventures.dreamtrips.api.messenger.model.request.ImmutableTranslateTextBody;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.modules.feed.model.TextualPost;
import com.worldventures.dreamtrips.modules.feed.model.UidItem;
import com.worldventures.dreamtrips.modules.feed.model.comment.Comment;

import javax.inject.Inject;

import io.techery.janet.Command;
import io.techery.janet.command.annotations.CommandAction;

public abstract class TranslateUidItemCommand<T extends UidItem> extends Command<T> implements InjectableAction {

    @Inject TranslationInteractor translationInteractor;
    @Inject SnappyRepository snappyRepository;

    private UidItem uidItem;
    private String text;
    private String languageTo;

    public TranslateUidItemCommand(UidItem uidItem, String text, String languageTo) {
        this.uidItem = uidItem;
        this.text = text;
        this.languageTo = languageTo;
    }

    @Override
    protected void run(CommandCallback<T> callback) throws Throwable {
        translationInteractor.translatePipe()
                .createObservableResult(new TranslateTextHttpAction(ImmutableTranslateTextBody
                        .builder()
                        .text(text)
                        .toLanguage(languageTo).build()))
                .map(TranslateTextHttpAction::getTranslatedText)
                .doOnNext(translatedText -> snappyRepository.saveTranslation(uidItem.getUid(), translatedText))
                .map(this::mapResult)
                .subscribe(callback::onSuccess, callback::onFail);
    }

    protected abstract T mapResult(String translatedText);

    public static TranslateCommentCommand forComment(Comment comment, String languageTo) {
        return new TranslateCommentCommand(comment, languageTo);
    }

    public static TranslatePostCommand forPost(TextualPost textualPost, String languageTo) {
        return new TranslatePostCommand(textualPost, languageTo);
    }

    @CommandAction
    public static class TranslateCommentCommand extends TranslateUidItemCommand<Comment> {

        private Comment comment;

        private TranslateCommentCommand(Comment uidItem, String languageTo) {
            super(uidItem, uidItem.getMessage(), languageTo);
            this.comment = uidItem;
        }

        @Override
        protected Comment mapResult(String translatedText) {
            comment.setTranslation(translatedText);
            comment.setTranslated(true);
            return comment;
        }
    }

    @CommandAction
    public static class TranslatePostCommand extends TranslateUidItemCommand<TextualPost> {

        private TextualPost textualPost;

        private TranslatePostCommand(TextualPost textualPost, String languageTo) {
            super(textualPost, textualPost.getDescription(), languageTo);
            this.textualPost = textualPost;
        }

        @Override
        protected TextualPost mapResult(String translatedText) {
            textualPost.setTranslation(translatedText);
            textualPost.setTranslated(true);
            return textualPost;
        }
    }

}
