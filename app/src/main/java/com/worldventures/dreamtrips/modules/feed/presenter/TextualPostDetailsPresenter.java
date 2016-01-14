package com.worldventures.dreamtrips.modules.feed.presenter;

import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.feed.api.DeletePostCommand;
import com.worldventures.dreamtrips.modules.feed.event.FeedEntityChangedEvent;
import com.worldventures.dreamtrips.modules.feed.event.FeedEntityDeletedEvent;
import com.worldventures.dreamtrips.modules.feed.model.TextualPost;

public class TextualPostDetailsPresenter extends Presenter<TextualPostDetailsPresenter.View> {

    private TextualPost textualPost;

    public TextualPostDetailsPresenter(TextualPost args) {
        this.textualPost = args;
    }

    @Override
    public void takeView(View view) {
        super.takeView(view);
        view.setupView(textualPost);
    }

    public void onEvent(FeedEntityChangedEvent event) {
        if (!event.getFeedEntity().getUid().equals(textualPost.getUid())) return;
        //
        textualPost = (TextualPost) event.getFeedEntity();
        view.setupView(textualPost);
    }

    public void onDelete() {
        doRequest(new DeletePostCommand(textualPost.getUid()), aVoid -> itemDeleted());
    }

    private void itemDeleted() {
        eventBus.post(new FeedEntityDeletedEvent(textualPost));
        view.back();
    }

    public void onEdit() {
        view.moveToEdit(textualPost);
    }

    public interface View extends Presenter.View {

        void moveToEdit(TextualPost textualPost);

        void setupView(TextualPost textualPost);

        void back();
    }
}
