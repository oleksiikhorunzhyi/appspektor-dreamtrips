package com.worldventures.dreamtrips.modules.common.view.custom.tagview;

import android.content.Context;
import android.support.annotation.NonNull;

import com.worldventures.dreamtrips.modules.common.model.User;

import java.util.ArrayList;

public class TagViewCreator {
    private PojoTag pojoTag;

    public TagViewCreator(@NonNull PojoTag pojoTag) {
        this.pojoTag = pojoTag;
    }

    public TagView build(Context context) {
        TagView tagView;
        if (pojoTag.taggedUser == null) {
            tagView = new NewTagView(context);
            tagView.setUserFriends(pojoTag.userFriends == null ? new ArrayList<User>() : pojoTag.userFriends);
        } else {
            tagView = new ExistsTagView(context);
            tagView.setTaggedUser(pojoTag.taggedUser);
            tagView.setTagCoordinates(pojoTag.leftTopPoint, pojoTag.tagCenterPoint, pojoTag.rightBottomPoint);
        }
        return tagView;
    }
}
