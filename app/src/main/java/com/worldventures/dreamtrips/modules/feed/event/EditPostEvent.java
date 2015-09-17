package com.worldventures.dreamtrips.modules.feed.event;

import com.worldventures.dreamtrips.modules.feed.model.TextualPost;

public class EditPostEvent {

    public TextualPost textualPost;

    public EditPostEvent(TextualPost textualPost) {
        this.textualPost = textualPost;
    }

    public TextualPost getTextualPost() {
        return textualPost;
    }
}
