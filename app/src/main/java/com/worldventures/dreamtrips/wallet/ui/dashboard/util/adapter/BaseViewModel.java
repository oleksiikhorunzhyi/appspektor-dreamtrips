package com.worldventures.dreamtrips.wallet.ui.dashboard.util.adapter;


import android.os.Parcelable;

public abstract class BaseViewModel implements Parcelable{

    public abstract int type(HolderTypeFactory typeFactory);
}
