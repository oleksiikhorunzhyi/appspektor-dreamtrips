package com.worldventures.dreamtrips.social.ui.video;

import android.content.Context;

import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSource;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

public final class DtDataSourceFactory implements DataSource.Factory {

   private final Context context;
   private final DataSource.Factory baseDataSourceFactory;

   public DtDataSourceFactory(Context context) {
      this.context = context;
      this.baseDataSourceFactory = new DefaultHttpDataSourceFactory(Util.getUserAgent(context,
            "dreamTrips"), null,
            DefaultHttpDataSource.DEFAULT_CONNECT_TIMEOUT_MILLIS,
            DefaultHttpDataSource.DEFAULT_READ_TIMEOUT_MILLIS, true);
   }

   @Override
   public DataSource createDataSource() {
      return new DefaultDataSource(context, null, baseDataSourceFactory.createDataSource());
   }
}
