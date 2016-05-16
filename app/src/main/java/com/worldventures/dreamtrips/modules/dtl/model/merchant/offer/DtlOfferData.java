package com.worldventures.dreamtrips.modules.dtl.model.merchant.offer;

import com.worldventures.dreamtrips.modules.dtl.model.merchant.operational_hour.OperationDay;

import java.util.Date;
import java.util.List;

public abstract class DtlOfferData implements Comparable<DtlOfferData> {

    private String title;
    private String description;
    private String disclaimer;

    private Date startDate;
    private Date endDate;

    private List<OperationDay> operationDays;
    private List<DtlOfferMedia> images;

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getDisclaimer() {
        return disclaimer;
    }

    public Date getStartDdate() {
        return startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public List<DtlOfferMedia> getImages() {
        return images;
    }

    public List<OperationDay> getOperationDays() {
        return operationDays;
    }

    public abstract String getType(); // TODO move to enum??

    @Override
    public int compareTo(DtlOfferData another) {
        return getType().equals(another.getType()) ? 0 : getType().equals(Offer.POINT_REWARD) ? -1 : 1;
    }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DtlOfferData that = (DtlOfferData) o;

        if (title != null ? !title.equals(that.title) : that.title != null) return false;
        if (description != null ? !description.equals(that.description) : that.description != null)
            return false;
        if (disclaimer != null ? !disclaimer.equals(that.disclaimer) : that.disclaimer != null)
            return false;
        if (startDate != null ? !startDate.equals(that.startDate) : that.startDate != null)
            return false;
        if (endDate != null ? !endDate.equals(that.endDate) : that.endDate != null) return false;
        if (operationDays != null ? !operationDays.equals(that.operationDays) : that.operationDays != null)
            return false;
        return images != null ? images.equals(that.images) : that.images == null;
    }

    @Override public int hashCode() {
        int result = title != null ? title.hashCode() : 0;
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (disclaimer != null ? disclaimer.hashCode() : 0);
        result = 31 * result + (startDate != null ? startDate.hashCode() : 0);
        result = 31 * result + (endDate != null ? endDate.hashCode() : 0);
        result = 31 * result + (operationDays != null ? operationDays.hashCode() : 0);
        return result;
    }
}
