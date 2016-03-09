package com.worldventures.dreamtrips.modules.map.renderer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.support.annotation.DrawableRes;
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
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.view.custom.BadgeView;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchantType;
import com.worldventures.dreamtrips.modules.map.model.DtlClusterItem;

import butterknife.ButterKnife;
import rx.Observable;

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
        if (cluster.getDtlMerchantType() == DtlMerchantType.DINING) {
            pin.setImageResource(R.drawable.blue_pin_icon_big);
        } else {
            pin.setImageResource(R.drawable.offer_pin_icon);
        }
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(makeIcon()));
    }

    @Override
    protected void onBeforeClusterRendered(Cluster<DtlClusterItem> cluster, MarkerOptions markerOptions) {
        Observable.from(cluster.getItems())
                .filter(dtlClusterItem -> dtlClusterItem.getDtlMerchantType() == DtlMerchantType.OFFER)
                .count().subscribe(offersCount ->
                setupClusterRendering(cluster, markerOptions, offersCount > 0));
    }

    private void setupClusterRendering(Cluster<DtlClusterItem> cluster, MarkerOptions markerOptions,
                                       boolean hasOffers) {
        @DrawableRes final int clusterIconResId = hasOffers ?
                R.drawable.cluster_pin_icon : R.drawable.dinings_pin_icon;
        itemBadgeView.setVisibility(View.VISIBLE);
        itemBadgeView.setText(String.valueOf(cluster.getItems().size()));
        pin.setImageResource(clusterIconResId);
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
