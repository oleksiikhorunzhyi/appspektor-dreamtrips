package com.worldventures.dreamtrips.core.utils;

import android.net.Uri;
import android.support.annotation.Nullable;
import android.text.*;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeController;
import com.facebook.drawee.backends.pipeline.PipelineDraweeControllerBuilder;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

import static android.text.TextUtils.*;

public class GraphicUtils {

    // this is the biggest size that Fresco's SimpleDraweeView can handle
    private static final int DEFAULT_DRESCO_MAX_IMAGE_SIZE = 4096;

    public static PipelineDraweeController provideFrescoResizingController(Uri uri, DraweeController oldController) {
        return provideFrescoResizingController(uri, oldController,
                DEFAULT_DRESCO_MAX_IMAGE_SIZE, DEFAULT_DRESCO_MAX_IMAGE_SIZE);
    }

    public static PipelineDraweeController provideFrescoResizingController(Uri uri,
                                                                           DraweeController oldController,
                                                                           int size) {
        return provideFrescoResizingController(uri, oldController, size, size);
    }

    public static PipelineDraweeControllerBuilder provideFrescoResizingControllerBuilder(Uri uri,
                                                                                         DraweeController oldController,
                                                                                         int width, int height) {
        ImageRequest request = ImageRequestBuilder.newBuilderWithSource(uri)
                .setResizeOptions(new ResizeOptions(width, height))
                .setAutoRotateEnabled(true)
                .build();

        return Fresco.newDraweeControllerBuilder()
                .setOldController(oldController)
                .setImageRequest(request);
    }


    public static PipelineDraweeController provideFrescoResizingController(Uri uri,
                                                                           DraweeController oldController,
                                                                           int width, int height) {
        return (PipelineDraweeController)
                provideFrescoResizingControllerBuilder(uri, oldController, width, height)
                        .build();
    }

    public static PipelineDraweeControllerBuilder provideFrescoResizingControllerBuilder(@Nullable Uri uri, @Nullable Uri localUri,
                                                                                         DraweeController oldController,
                                                                                         int width, int height) {
        return Fresco.newDraweeControllerBuilder()
                .setOldController(oldController)
                .setImageRequest(createResizeImageRequest(uri, width, height))
                .setLowResImageRequest(createResizeImageRequest(localUri, width, height));
    }

    public static PipelineDraweeControllerBuilder provideFrescoResizingControllerBuilder(@Nullable String strUri, @Nullable String strLocalUri,
                                                                                         DraweeController oldController,
                                                                                         int width, int height) {
        return provideFrescoResizingControllerBuilder(parseUri(strUri), parseUri(strLocalUri), oldController, width, height);
    }

    @Nullable
    private static Uri parseUri(@Nullable String uri) {
        return isEmpty(uri) ? null : Uri.parse(uri);
    }

    @Nullable
    public static ImageRequest createResizeImageRequest(@Nullable Uri uri, int width, int height) {
        return uri == null ? null : ImageRequestBuilder.newBuilderWithSource(uri)
                .setResizeOptions(new ResizeOptions(width, height))
                .setAutoRotateEnabled(true)
                .build();
    }
}
