package com.worldventures.dreamtrips.wallet.ui.common.adapter;


import android.view.ViewGroup;

public interface HolderTypeFactory {

   BaseHolder holder(ViewGroup parent, int viewType);
}
