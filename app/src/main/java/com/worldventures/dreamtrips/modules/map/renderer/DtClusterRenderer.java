package com.worldventures.dreamtrips.modules.map.renderer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.support.annotation.StringRes;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchantType;
import com.worldventures.dreamtrips.modules.map.model.ClusterType;
import com.worldventures.dreamtrips.modules.map.model.DtlClusterItem;

import java.util.List;

import butterknife.ButterKnife;
import rx.Observable;

public class DtClusterRenderer extends DefaultClusterRenderer<DtlClusterItem> {
    private final TextView clusterSizeView;
    private final ImageView pin;
    private final ViewGroup clusterView;

    public DtClusterRenderer(Context context, GoogleMap googleMap, ClusterManager<DtlClusterItem> clusterManager) {
        super(context, googleMap, clusterManager);

        pin = (ImageView) LayoutInflater.from(context).inflate(R.layout.pin_map, null);
        clusterView = (ViewGroup) LayoutInflater.from(context).inflate(R.layout.cluster_map, null);

        clusterSizeView = ButterKnife.findById(clusterView, R.id.cluster_size);
    }

    @Override
    protected void onBeforeClusterItemRendered(DtlClusterItem cluster, MarkerOptions markerOptions) {
        final int resourceId = cluster.getDtlMerchantType() == DtlMerchantType.DINING ? R.drawable.blue_pin_icon_big : R.drawable.offer_pin_icon;
        pin.setImageResource(resourceId);
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(makeIcon(pin)));
    }


    @Override
    protected void onBeforeClusterRendered(Cluster<DtlClusterItem> cluster, MarkerOptions markerOptions) {
        Observable.from(cluster.getItems())
                .distinct(DtlClusterItem::getDtlMerchantType).toList().subscribe(merchants ->
                setupClusterRendering(cluster, markerOptions, merchants));
    }

    private void setupClusterRendering(Cluster<DtlClusterItem> cluster, MarkerOptions markerOptions, List<DtlClusterItem> clusterItemtypes) {
        final int clusterIconResId = clusterItemtypes.size() == 2 ? ClusterType.COMBINE.asResource() : ClusterType.from(clusterItemtypes.get(0)).asResource();
        final String size = cluster.getItems().size() > 99 ? "99+" : String.valueOf(cluster.getItems().size());
        clusterSizeView.setText(size);
        clusterView.setBackground(ContextCompat.getDrawable(clusterView.getContext(), clusterIconResId));
        //
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(makeIcon(clusterView)));
    }

    @Override
    protected boolean shouldRenderAsCluster(Cluster cluster) {
        return cluster.getSize() > 3;
    }

    private Bitmap makeIcon(View icon) {
        if (icon.getMeasuredWidth() == 0 || icon.getMeasuredHeight() == 0)
            measureIcon(icon); // skip measuring if all icons is same size

        Bitmap bitmap = Bitmap.createBitmap(icon.getMeasuredWidth(), icon.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bitmap);
        icon.draw(c);
        return bitmap;
    }

    private void measureIcon(View icon) {
        int measureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        icon.measure(measureSpec, measureSpec);
        icon.layout(0, 0, icon.getMeasuredWidth(), icon.getMeasuredHeight());
    }
}
