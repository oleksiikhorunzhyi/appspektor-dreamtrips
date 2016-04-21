package com.worldventures.dreamtrips.modules.dtl.model.merchant.offer;

import com.worldventures.dreamtrips.modules.dtl.model.merchant.operational_hour.OperationDay;

import java.util.Date;
import java.util.List;

public class DtlOfferPerkData extends DtlOfferData {

    private String description;
    private String disclaimer;

    private Date startDate;
    private Date endDate;

    private List<OperationDay> operationDays;
    private List<DtlOfferMedia> images;

    public String getDescription() {
        return description;
    }

    public String getDisclaimer() {
        return disclaimer;
    }

    public Date getStartDdate() {
        return startDate;
    }

    public Date getEndDdate() {
        return endDate;
    }

    public List<DtlOfferMedia> getImages() {
        return images;
    }

    public List<OperationDay> getOperationDays() {
        return operationDays;
    }

    @Override
    public String getType() {
        return Offer.PERKS;
    }
}
