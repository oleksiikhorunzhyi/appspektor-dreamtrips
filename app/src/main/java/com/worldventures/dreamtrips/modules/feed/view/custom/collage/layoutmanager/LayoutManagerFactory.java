package com.worldventures.dreamtrips.modules.feed.view.custom.collage.layoutmanager;

public class LayoutManagerFactory {
    public static LayoutManager getManager(int count) {
        switch (count) {
            case 1:
                return new LayoutManagerSingle();
            case 2:
                return new LayoutManagerTwo();
            case 3:
                return new LayoutManagerThree();
            case 4:
                return new LayoutManagerFour();
            case 5:
            default:
                return new LayoutManagerMany();
        }
    }
}
