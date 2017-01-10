package com.worldventures.dreamtrips.modules.common.view.fragment;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;

import com.worldventures.dreamtrips.modules.common.presenter.ComponentPresenter;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;

public abstract class BaseFragmentWithArgs<PM extends Presenter, P extends Parcelable> extends BaseFragment<PM> {

   public void setArgs(P data) {
      Bundle bundle = new Bundle();
      bundle.putParcelable(ComponentPresenter.EXTRA_DATA, data);
      setArguments(bundle);
   }

   @Nullable
   public P getArgs() {
      if (getArguments() == null) {
         return null;
      }
      return getArguments().getParcelable(ComponentPresenter.EXTRA_DATA);
   }
}
