package com.worldventures.dreamtrips.core.navigation;

public class BackStackDelegate {

    private BackPressedListener listener;

    public boolean onBackPressed() {
        return listener != null && listener.onBackPressed();
    }

    public void setListener(BackPressedListener listener) {
        this.listener = listener;
    }

    public interface BackPressedListener {
        boolean onBackPressed();
    }
}
