package com.worldventures.dreamtrips.modules.dtl.model;

import android.content.res.Resources;
import android.text.Html;
import android.text.Spanned;

import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.serializers.CompatibleFieldSerializer;
import com.worldventures.dreamtrips.R;

@DefaultSerializer(CompatibleFieldSerializer.class)
public class DtlTransactionResult {
    double earnedPoints;
    double total;

    public Spanned generateSuccessMessage(Resources resources) {
        return Html.fromHtml(resources.getString(R.string.dtl_success, (int) earnedPoints, (int) total));
    }
}
