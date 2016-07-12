package com.worldventures.dreamtrips.modules.dtl.model.merchant.operational_hour;

import java.util.List;

public class OperationDay {

    DayOfWeek dayOfWeek;
    List<OperationHours> operationHours;

    public OperationDay() {
    }

    public boolean isHaveOperationHours() {
        return operationHours != null && !operationHours.isEmpty();
    }

    public DayOfWeek getDayOfWeek() {
        return dayOfWeek;
    }

    public List<OperationHours> getOperationHours() {
        return operationHours;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OperationDay that = (OperationDay) o;

        return dayOfWeek == that.dayOfWeek;
    }

    @Override
    public int hashCode() {
        return dayOfWeek != null ? dayOfWeek.hashCode() : 0;
    }

}
