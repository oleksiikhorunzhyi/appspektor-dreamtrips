package com.worldventures.dreamtrips.modules.tripsimages.model;

import com.worldventures.dreamtrips.modules.common.model.User;

public class PhotoTag {

    private int targetUserId;
    private TagPosition position;
    private User user;

    public PhotoTag() {

    }

    public PhotoTag(int targetUserId, TagPosition position) {
        this.targetUserId = targetUserId;
        this.position = position;
    }

    public int getTargetUserId() {
        return targetUserId;
    }

    public TagPosition getPosition() {
        return position;
    }

    public User getUser() {
        return user;
    }

    public static class TagPosition {

        private Position topLeft;
        private Position bottomRight;

        public TagPosition() {

        }

        public TagPosition(float topLeftX, float topLeftY, float bottomRightX, float bottomRightY) {
            topLeft = new Position(topLeftX, topLeftY);
            bottomRight = new Position(bottomRightX, bottomRightY);
        }

        public Position getTopLeft() {
            return topLeft;
        }

        public Position getBottomRight() {
            return bottomRight;
        }
    }

    public static class Position {

        private float x;
        private float y;

        public Position() {

        }

        public Position(float x, float y) {
            this.x = x;
            this.y = y;
        }

        public float getX() {
            return x;
        }

        public float getY() {
            return y;
        }
    }
}
