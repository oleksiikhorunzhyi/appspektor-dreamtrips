package com.worldventures.dreamtrips.modules.common.presenter.delegate;

import android.content.Context;

import com.innahema.collections.query.queriables.Queryable;
import com.messenger.storage.dao.AttachmentDAO;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.core.utils.FileUtils;
import com.worldventures.dreamtrips.modules.tripsimages.view.custom.PickImageDelegate;

import java.io.File;
import java.io.IOException;
import java.util.List;

import timber.log.Timber;

public class ClearDirectoryDelegate {

    private final Context context;
    private final AttachmentDAO attachmentDAO;
    private final SnappyRepository snappyRepository;

    public ClearDirectoryDelegate(Context context, AttachmentDAO attachmentDAO, SnappyRepository snappyRepository) {
        this.context = context;
        this.attachmentDAO = attachmentDAO;
        this.snappyRepository = snappyRepository;
    }

    public void clearTemporaryDirectory(){
        attachmentDAO.getErrorAtachments().subscribe(dataAttachments -> {
            List<String> exceptFilePaths = Queryable.from(dataAttachments).map(elem -> elem.getUrl()).toList();

            snappyRepository.removeAllUploadTasks();
            File directory = new File(com.kbeanie.imagechooser.api.FileUtils.getDirectory(PickImageDelegate.FOLDERNAME));
            if (!directory.exists()) return;
            try {
                FileUtils.cleanDirectory(context, directory, exceptFilePaths);
            } catch (IOException e) {
                Timber.e(e, "Problem with remove temp image directory");
            }
        });
    }

}
