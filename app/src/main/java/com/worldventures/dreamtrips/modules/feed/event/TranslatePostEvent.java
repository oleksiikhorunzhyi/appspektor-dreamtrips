package com.worldventures.dreamtrips.modules.feed.event;

import com.worldventures.dreamtrips.modules.feed.model.FeedEntity;
import com.worldventures.dreamtrips.modules.feed.model.TextualPost;

public class TranslatePostEvent {

    public TextualPost textualPost;

    public TranslatePostEvent(TextualPost feedItem) {
        this.textualPost = feedItem;
    }

    public TextualPost getTextualPost() {
        return textualPost;
    }
}
