package de.evoila.cf.broker.model;

import java.util.Map;

public class Cost {

    private Map<String, Object> amount;

    private String unit;

    public Map<String, Object> getAmount() {
        return amount;
    }

    public void setAmount(Map<String, Object> amount) {
        this.amount = amount;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
}
