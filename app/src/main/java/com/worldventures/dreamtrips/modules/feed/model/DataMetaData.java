package com.worldventures.dreamtrips.modules.feed.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.serializers.CompatibleFieldSerializer;
import com.google.gson.annotations.SerializedName;
import com.innahema.collections.query.functions.Action1;
import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.modules.feed.model.feed.base.ParentFeedItem;

import java.util.ArrayList;

@DefaultSerializer(CompatibleFieldSerializer.class)
public class DataMetaData implements Parcelable {

    @SerializedName("data")
    ArrayList<ParentFeedItem> parentFeedItems;
    @SerializedName("metadata")
    MetaData metaData;

    public DataMetaData() {
    }

    protected DataMetaData(Parcel in) {
        metaData = in.readParcelable(MetaData.class.getClassLoader());
    }

    public static final Creator<DataMetaData> CREATOR = new Creator<DataMetaData>() {
        @Override
        public DataMetaData createFromParcel(Parcel in) {
            return new DataMetaData(in);
        }

        @Override
        public DataMetaData[] newArray(int size) {
            return new DataMetaData[size];
        }
    };

    public ArrayList<ParentFeedItem> getParentFeedItems() {
        return parentFeedItems;
    }

    public MetaData getMetaData() {
        return metaData;
    }

    public final void shareMetaDataWithChilds(){
        Queryable.from(parentFeedItems)
                .forEachR(arg -> Queryable.from(arg.getItems())
                        .forEachR((Action1<FeedItem<FeedEntity>>) arg1 -> arg1.setMetaData(metaData)));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(metaData, flags);
    }
}
