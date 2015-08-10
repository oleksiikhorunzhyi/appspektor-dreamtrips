package com.worldventures.dreamtrips.modules.feed.model;

import com.worldventures.dreamtrips.modules.common.model.User;

import java.util.Date;

public class FeedPostEventModel extends BaseFeedModel<TextualPost> {

    public static FeedPostEventModel create(User user, TextualPost textualPost) {
        FeedPostEventModel eventModel = new FeedPostEventModel();
        eventModel.entities = new TextualPost[]{textualPost};
        eventModel.users = new User[]{user};
        eventModel.action = Action.ADD;
        eventModel.type = Type.POST;
        eventModel.postedAt = new Date();
        return eventModel;
    }
}
