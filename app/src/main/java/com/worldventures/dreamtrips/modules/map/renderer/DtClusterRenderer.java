package com.worldventures.dreamtrips.modules.map.renderer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.view.custom.BadgeView;
import com.worldventures.dreamtrips.modules.dtl.model.DtlPlaceType;
import com.worldventures.dreamtrips.modules.map.model.DtlClusterItem;

import butterknife.ButterKnife;

public class DtClusterRenderer extends DefaultClusterRenderer<DtlClusterItem> {
    private final BadgeView itemBadgeView;
    private final ImageView pin;
    private final ViewGroup itemView;

    public DtClusterRenderer(Context context, GoogleMap googleMap, ClusterManager<DtlClusterItem> clusterManager) {
        super(context, googleMap, clusterManager);

        itemView = (ViewGroup) LayoutInflater.from(context).inflate(R.layout.pin_map, (ViewGroup) null);

        itemBadgeView = ButterKnife.findById(itemView, R.id.badge);
        itemBadgeView.setBadgeBackgroundColor(context.getResources().getColor(R.color.dtl_badge_color));

        pin = ButterKnife.findById(itemView, R.id.pin);
    }

    @Override
    protected void onBeforeClusterItemRendered(DtlClusterItem cluster, MarkerOptions markerOptions) {
        itemBadgeView.setVisibility(View.GONE);
        if (cluster.getDtlPlaceType() == DtlPlaceType.DINING) {
            pin.setImageResource(R.drawable.blue_pin_icon_big);
        } else {
            pin.setImageResource(R.drawable.offer_pin_icon);
        }
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(makeIcon()));
    }

    @Override
    protected void onBeforeClusterRendered(Cluster<DtlClusterItem> cluster, MarkerOptions markerOptions) {
        itemBadgeView.setVisibility(View.VISIBLE);
        itemBadgeView.setText(String.valueOf(cluster.getItems().size()));
        int offersCount = Queryable.from(cluster.getItems()).count(element ->
                element.getDtlPlaceType() == DtlPlaceType.DINING);
        int dinningCount = Queryable.from(cluster.getItems()).count(element ->
                element.getDtlPlaceType() == DtlPlaceType.OFFER);

        if (offersCount > dinningCount) {
            pin.setImageResource(R.drawable.offers_pin_icon);
        } else {
            pin.setImageResource(R.drawable.dinings_pin_icon);
        }

        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(makeIcon()));
    }

    @Override
    protected boolean shouldRenderAsCluster(Cluster cluster) {
        return cluster.getSize() > 1;
    }

    private Bitmap makeIcon() {
        int measureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        itemView.measure(measureSpec, measureSpec);
        int measuredWidth = itemView.getMeasuredWidth();
        int measuredHeight = itemView.getMeasuredHeight();
        itemView.layout(0, 0, measuredWidth, measuredHeight);

        Bitmap b = Bitmap.createBitmap(measuredWidth, measuredHeight, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        itemView.draw(c);
        return b;
    }
}
