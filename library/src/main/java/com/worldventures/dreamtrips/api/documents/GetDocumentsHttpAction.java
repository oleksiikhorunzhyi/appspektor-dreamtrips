package com.worldventures.dreamtrips.api.documents;

import com.worldventures.dreamtrips.api.api_common.PaginatedHttpAction;
import com.worldventures.dreamtrips.api.documents.model.Document;

import java.util.List;

import io.techery.janet.http.annotations.HttpAction;
import io.techery.janet.http.annotations.Response;

@HttpAction("/api/documents")
public class GetDocumentsHttpAction extends PaginatedHttpAction {

    @Response
    List<Document> docs;

    public List<Document> response() {
        return docs;
    }

    public GetDocumentsHttpAction(int page, int perPage) {
        super(page, perPage);
    }

    public GetDocumentsHttpAction() {
        super(1,15);
    }
}
