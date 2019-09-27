package de.evoila.cf.broker.model.catalog.plan;

import java.util.Map;
import java.util.Objects;

public class Cost {

    private Map<String, Float> amount;
    private String unit;

    public Cost() {
    }

    public Cost(Map<String, Float> amount, String unit) {
        this.amount = amount;
        this.unit = unit;
    }

    public Map<String, Float> getAmount() {
        return amount;
    }

    public void setAmount(Map<String, Float> amount) {
        this.amount = amount;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Cost cost = (Cost) o;
        return amount.equals(cost.amount) &&
               unit.equals(cost.unit);
    }

    @Override
    public int hashCode() {
        return Objects.hash(amount, unit);
    }

}
