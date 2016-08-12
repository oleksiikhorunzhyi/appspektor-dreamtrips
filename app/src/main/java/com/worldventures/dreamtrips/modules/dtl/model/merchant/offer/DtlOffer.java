package com.worldventures.dreamtrips.modules.dtl.model.merchant.offer;

import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.serializers.CompatibleFieldSerializer;
import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchantMedia;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.operational_hour.OperationDay;

import java.util.Comparator;
import java.util.Date;
import java.util.List;

@DefaultSerializer(CompatibleFieldSerializer.class)
public abstract class DtlOffer implements Comparable<DtlOffer> {

    private String title;
    private String description;
    private String disclaimer;

    private Date startDate;
    private Date endDate;

    private List<OperationDay> operationDays;
    private List<DtlMerchantMedia> images;

    public DtlOffer() {
    }

    public DtlOffer(com.worldventures.dreamtrips.api.dtl.merchats.model.Offer offer) {
        title = offer.offerData().title();
        description = offer.offerData().description();
        disclaimer = offer.offerData().disclaimer();
        startDate = offer.offerData().startDate();
        endDate = offer.offerData().endDate();
        operationDays = Queryable.from(offer.offerData().operationDays())
                .map(OperationDay::new).toList();
        images = Queryable.from(offer.offerData().images()).map(DtlMerchantMedia::new).toList();
    }

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

    public List<DtlMerchantMedia> getImages() {
        return images;
    }

    public List<OperationDay> getOperationDays() {
        return operationDays;
    }

    public abstract Type getType();

    public boolean isPerk() {
        return getType() == Type.PERK;
    }

    public boolean isPoint() {
        return getType() == Type.POINTS;
    }

    @Override
    public int compareTo(DtlOffer another) {
        return getType() == another.getType() ? 0 : isPoint() ? -1 : 1;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DtlOffer that = (DtlOffer) o;

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
        if (getType() != that.getType()) return false;
        return images != null ? images.equals(that.images) : that.images == null;
    }

    @Override
    public int hashCode() {
        int result = title != null ? title.hashCode() : 0;
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (disclaimer != null ? disclaimer.hashCode() : 0);
        result = 31 * result + (startDate != null ? startDate.hashCode() : 0);
        result = 31 * result + (endDate != null ? endDate.hashCode() : 0);
        result = 31 * result + (operationDays != null ? operationDays.hashCode() : 0);
        return result;
    }

    public static final Comparator<DtlOffer> END_DATE_COMPARATOR = (lhs, rhs) -> {
        if (lhs.compareTo(rhs) != 0) return lhs.compareTo(rhs);
        if (lhs.getEndDate() == null && rhs.getEndDate() == null) return 0;
        if (lhs.getEndDate() == null && rhs.getEndDate() != null) return 1;
        if (rhs.getEndDate() == null && lhs.getEndDate() != null) return -1;
        return lhs.getEndDate().compareTo(rhs.getEndDate());
    };

    public enum Type {

        PERK("perk"), POINTS("points"), UNKNOWN("unknown");

        private final String type;

        Type(String type) {
            this.type = type;
        }

        public String value() {
            return type;
        }

        public static Type of(final String type) {
            final Type entry = Queryable.from(values())
                    .firstOrDefault(typed -> typed.value().equalsIgnoreCase(type));
            return entry != null ? entry : UNKNOWN;
        }
    }
}
