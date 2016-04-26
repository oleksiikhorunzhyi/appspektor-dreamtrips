package com.worldventures.dreamtrips.modules.feed.view.custom.collage;

public class CollageItem {
    public final String url;
    public final int width;
    public final int height;

    public CollageItem(String url, int width, int height) {
        this.url = url;
        this.width = width;
        this.height = height;
    }

    public String url() {
        return url;
    }

    public int width() {
        return width;
    }

    public int height() {
        return height;
    }

    @Override
    public String toString() {
        return "CollageItem{" +
                "url='" + url + '\'' +
                ", width=" + width +
                ", height=" + height +
                '}';
    }
}
