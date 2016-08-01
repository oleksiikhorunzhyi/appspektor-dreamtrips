package com.worldventures.dreamtrips.wallet.ui.widget;


import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.worldventures.dreamtrips.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class BankCardWidget extends RelativeLayout {

    @InjectView(R.id.bankLabel)
    TextView bankLabel;
    @InjectView(R.id.connectedCardCount)
    TextView connectedCardCount;
    @InjectView(R.id.cardNumber)
    TextView cardNumber;
    //// TODO: 8/1/16 rename this naming
    @InjectView(R.id.someStrangeInfo)
    TextView someStrangeInfo;
    @InjectView(R.id.typeIcon)
    ImageView cardTypeIcon;
    @InjectView(R.id.expireDate)
    TextView expireDate;

    public BankCardWidget(Context context) {
        super(context);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.inject(this);
    }

    public BankCardWidget(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BankCardWidget(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

}
