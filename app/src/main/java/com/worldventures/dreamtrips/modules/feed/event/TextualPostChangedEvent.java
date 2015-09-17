package com.worldventures.dreamtrips.modules.feed.event;

import com.worldventures.dreamtrips.modules.feed.model.TextualPost;

public class TextualPostChangedEvent {

    TextualPost textualPost;

    public TextualPostChangedEvent(TextualPost textualPost) {
        this.textualPost = textualPost;
    }

    public TextualPost getTextualPost() {
        return textualPost;
    }
}
