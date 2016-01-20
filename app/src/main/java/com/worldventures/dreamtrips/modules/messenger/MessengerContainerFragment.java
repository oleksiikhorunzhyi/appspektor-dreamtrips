package com.worldventures.dreamtrips.modules.messenger;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.messenger.ui.view.ConversationListScreenImpl;

public class MessengerContainerFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View screen = new ConversationListScreenImpl(getContext());
        screen.setId(android.R.id.primary);
        return screen;
    }
}
