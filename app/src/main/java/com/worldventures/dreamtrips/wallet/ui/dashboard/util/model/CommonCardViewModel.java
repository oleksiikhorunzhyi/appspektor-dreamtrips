package com.worldventures.dreamtrips.wallet.ui.dashboard.util.model;


import android.databinding.BindingAdapter;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.wallet.ui.dashboard.util.adapter.BaseViewModel;
import com.worldventures.dreamtrips.wallet.ui.dashboard.util.adapter.HolderTypeFactory;

public class CommonCardViewModel extends BaseViewModel {

    private String cardName;
    private String cardType;
    private boolean defaultCard;
    private String cardLastDigitsShort;
    private String cardHolderName;
    private String cardLastDigitsLong;
    private String goodThrough;
    private int cardBackGround;

    public CommonCardViewModel(String cardName, String cardType, boolean defaultCard, String cardLastDigitsShort,
                               String cardHolderName, String cardLastDigitsLong, String goodThrough) {
        this.cardName = cardName;
        this.cardType = cardType;
        this.defaultCard = defaultCard;
        this.cardLastDigitsShort = cardLastDigitsShort;
        this.cardHolderName = cardHolderName;
        this.cardLastDigitsLong = cardLastDigitsLong;
        this.goodThrough = goodThrough;
    }

    protected CommonCardViewModel(Parcel in) {
        cardName = in.readString();
        cardType = in.readString();
        defaultCard = in.readByte() != 0;
        cardLastDigitsShort = in.readString();
        cardHolderName = in.readString();
        cardLastDigitsLong = in.readString();
        goodThrough = in.readString();
        cardBackGround = in.readInt();
    }

    public static final Parcelable.Creator<CommonCardViewModel> CREATOR = new Parcelable.Creator<CommonCardViewModel>() {
        @Override
        public CommonCardViewModel createFromParcel(Parcel in) {
            return new CommonCardViewModel(in);
        }

        @Override
        public CommonCardViewModel[] newArray(int size) {
            return new CommonCardViewModel[size];
        }
    };

    public String getCardName() {
        return cardName;
    }

    public String getCardType() {
        return cardType;
    }

    public boolean isDefaultCard() {
        return defaultCard;
    }

    public String getCardLastDigitsShort() {
        return cardLastDigitsShort;
    }

    public String getCardHolderName() {
        return cardHolderName;
    }

    public String getCardLastDigitsLong() {
        return cardLastDigitsLong;
    }

    public String getGoodThrough() {
        return goodThrough;
    }

    @BindingAdapter({"bind:cardBackground"})
    public static void getCardBackground(View view, boolean defaultBackGroound) {
        view.setBackgroundResource(R.drawable.card_blue_bcgd);
    }

    @Override
    public int type(HolderTypeFactory typeFactory) {
        return typeFactory.type(this);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(cardName);
        dest.writeString(cardType);
        dest.writeByte((byte) (defaultCard ? 1 : 0));
        dest.writeString(cardLastDigitsShort);
        dest.writeString(cardHolderName);
        dest.writeString(cardLastDigitsLong);
        dest.writeString(goodThrough);
        dest.writeInt(cardBackGround);
    }
}
