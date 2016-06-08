package com.worldventures.dreamtrips.modules.dtl_flow.parts.master_toolbar;

import android.content.Context;
import android.util.AttributeSet;

import com.worldventures.dreamtrips.modules.dtl_flow.DtlLayout;

public class MasterToolbarScreenImpl
        extends DtlLayout<MasterToolbarScreen, MasterToolbarPresenter, MasterToolbarPath>
        implements MasterToolbarScreen {

    public MasterToolbarScreenImpl(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public MasterToolbarPresenter createPresenter() {
        return new MasterToolbarPresenterImpl(getContext());
    }
}
