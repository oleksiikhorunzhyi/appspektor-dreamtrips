package com.worldventures.dreamtrips.core.utils;

import android.net.Uri;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeController;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

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

    public static PipelineDraweeController provideFrescoResizingController(Uri uri,
                                                                           DraweeController oldController,
                                                                           int width, int height) {
        ImageRequest request = ImageRequestBuilder.newBuilderWithSource(uri)
                .setResizeOptions(new ResizeOptions(width, height))
                .setAutoRotateEnabled(true)
                .build();

        return (PipelineDraweeController) Fresco.newDraweeControllerBuilder()
                .setOldController(oldController)
                .setImageRequest(request)
                .build();
    }
}
