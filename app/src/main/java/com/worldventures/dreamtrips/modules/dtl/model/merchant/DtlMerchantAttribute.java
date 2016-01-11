package com.worldventures.dreamtrips.modules.dtl.model.merchant;

public class DtlMerchantAttribute {

    private String name;

    public DtlMerchantAttribute() {
    }

    public DtlMerchantAttribute(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DtlMerchantAttribute that = (DtlMerchantAttribute) o;

        return !(name != null ? !name.equals(that.name) : that.name != null);

    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }

    @Override
    public String toString() {
        return name;
    }
}
