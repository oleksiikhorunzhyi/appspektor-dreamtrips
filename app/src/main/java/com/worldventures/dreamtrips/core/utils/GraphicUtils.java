package com.worldventures.dreamtrips.core.utils;

import android.net.Uri;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeController;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

public class GraphicUtils {

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
