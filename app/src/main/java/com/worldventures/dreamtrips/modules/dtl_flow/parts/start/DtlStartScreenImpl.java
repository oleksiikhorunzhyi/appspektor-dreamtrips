package com.worldventures.dreamtrips.modules.dtl_flow.parts.start;

import android.content.Context;
import android.util.AttributeSet;

import com.worldventures.dreamtrips.modules.dtl_flow.FlowLayout;

public class DtlStartScreenImpl extends FlowLayout<DtlStartScreen, DtlStartPresenter, DtlStartPath>
        implements DtlStartScreen {

    public DtlStartScreenImpl(Context context) {
        super(context);
    }

    public DtlStartScreenImpl(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public DtlStartPresenter createPresenter() {
        return new DtlStartPresenterImpl();
    }
}
