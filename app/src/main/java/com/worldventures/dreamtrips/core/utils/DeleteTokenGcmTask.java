package com.worldventures.dreamtrips.core.utils;

import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.Nullable;

import com.google.android.gms.iid.InstanceID;

import java.io.IOException;

import timber.log.Timber;

public class DeleteTokenGcmTask extends AsyncTask<Void, Void, Boolean>{

    Context context;
    LogoutListener listener;

    public DeleteTokenGcmTask(Context context, @Nullable LogoutListener listener) {
        this.context = context;
        this.listener = listener;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        try {
            InstanceID.getInstance(context).deleteInstanceID();
            return true;
        } catch (IOException e) {
            Timber.i(e, "Failed to delete token in GCM");
            return false;
        }
    }

    @Override
    protected void onPostExecute(Boolean removeGcmTokenSucceed) {
        super.onPostExecute(removeGcmTokenSucceed);
        if (listener != null) listener.logoutCallBack(this, removeGcmTokenSucceed);
    }

    public interface LogoutListener{
        void logoutCallBack(DeleteTokenGcmTask task, boolean removeGcmTokenSucceed);
    }

}
