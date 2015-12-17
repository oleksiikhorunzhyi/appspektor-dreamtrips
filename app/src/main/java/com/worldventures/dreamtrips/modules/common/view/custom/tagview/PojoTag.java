package com.worldventures.dreamtrips.modules.common.view.custom.tagview;

import android.graphics.Point;

import com.worldventures.dreamtrips.modules.common.model.User;

import java.util.ArrayList;
import java.util.List;

public class PojoTag {
    public final User taggedUser;
    public final List<User> userFriends;
    public final Point leftTopPoint;
    public final Point tagCenterPoint;
    public final Point rightBottomPoint;

    public PojoTag(User taggedUser, List<User> userFriends, Point leftTopPoint, Point tagCenterPoint, Point rightBottomPoint) {
        this.taggedUser = taggedUser;
        this.userFriends = userFriends;
        this.leftTopPoint = leftTopPoint;
        this.tagCenterPoint = tagCenterPoint;
        this.rightBottomPoint = rightBottomPoint;
    }

    public static class PojoTagBuilder {
        private User taggedUser;
        private List<User> userFriends = new ArrayList<>();
        private Point leftTopPoint = new Point(-1, -1);
        private Point tagCenter = new Point(-1, -1);
        private Point rightBottom = new Point(-1, -1);

        public PojoTagBuilder setTaggedUser(User taggedUser) {
            this.taggedUser = taggedUser;
            return this;
        }

        public PojoTagBuilder setUserFriends(List<User> userFriends) {
            this.userFriends = userFriends;
            return this;
        }

        public PojoTagBuilder setLeftTopPoint(Point leftTopPoint) {
            this.leftTopPoint = leftTopPoint;
            return this;
        }

        public PojoTagBuilder setTagCenter(Point tagCenter) {
            this.tagCenter = tagCenter;
            return this;
        }

        public PojoTagBuilder setRightBottom(Point rightBottom) {
            this.rightBottom = rightBottom;
            return this;
        }

        public PojoTag build() {
            return new PojoTag(taggedUser, userFriends, leftTopPoint, tagCenter, rightBottom);
        }
    }
}