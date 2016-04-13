package com.worldventures.dreamtrips.modules.feed.api;

import android.os.Environment;

import com.innahema.collections.query.queriables.Queryable;
import com.octo.android.robospice.request.SpiceRequest;
import com.worldventures.dreamtrips.modules.common.model.PhotoGalleryModel;
import com.worldventures.dreamtrips.modules.common.view.util.DrawableUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SuggestedPhotosRequest extends SpiceRequest<ArrayList<PhotoGalleryModel>> {

    public static final int SUGGESTED_PHOTOS_COUNT = 15;

    public SuggestedPhotosRequest() {
        super((Class<ArrayList<PhotoGalleryModel>>) new ArrayList<PhotoGalleryModel>().getClass());
    }

    @Override
    public ArrayList<PhotoGalleryModel> loadDataFromNetwork() throws Exception {
        return getSuggestedPhotos();
    }

    private ArrayList<PhotoGalleryModel> getSuggestedPhotos() {
        ArrayList<PhotoGalleryModel> suggestedList = new ArrayList<>();
        Queryable.from(getListFiles(new File(Environment.getExternalStoragePublicDirectory
                (Environment.DIRECTORY_DCIM).getAbsolutePath())))
                .filter(DrawableUtil::isFileImage)
                .sort((lhs, rhs) -> lhs.lastModified() > rhs.lastModified() ? -1 : (lhs.lastModified() < rhs.lastModified() ? 1 : 0))
                .take(SUGGESTED_PHOTOS_COUNT)
                .forEachR(file -> suggestedList.add(new PhotoGalleryModel(file.getPath(), file.lastModified())));
        return suggestedList;
    }

    private List<File> getListFiles(File parentDir) {
        ArrayList<File> inFiles = new ArrayList<>();
        File[] files = parentDir.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                if (file.getName().startsWith(".")) continue;
                //
                inFiles.addAll(getListFiles(file));
            } else {
                inFiles.add(file);
            }
        }
        return inFiles;
    }
}
