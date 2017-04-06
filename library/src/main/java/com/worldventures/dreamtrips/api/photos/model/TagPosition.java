package com.worldventures.dreamtrips.api.photos.model;

import com.google.gson.annotations.SerializedName;

import org.immutables.gson.Gson;
import org.immutables.value.Value;

@Gson.TypeAdapters
@Value.Immutable
public interface TagPosition {

    @SerializedName("top_left")
    Point topLeft();

    @SerializedName("bottom_right")
    Point bottomRight();

    class Point {
        @SerializedName("x")
        public final double x;
        @SerializedName("y")
        public final double y;

        public Point(double x, double y) {
            this.x = x;
            this.y = y;
        }
    }

}
