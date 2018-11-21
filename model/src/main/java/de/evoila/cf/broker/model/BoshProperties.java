package de.evoila.cf.broker.model;

import java.util.LinkedList;
import java.util.List;

public class BoshProperties {

    private int instanceNumber;
    private List<String> createErrands;
    private List<String> updateErrands;

    public BoshProperties() {
        createErrands = new LinkedList<>();
        updateErrands = new LinkedList<>();
    }

    public BoshProperties(int instanceNumber, List<String> createErrands, List<String> updateErrands) {
        this.instanceNumber = instanceNumber;
        this.createErrands = createErrands;
        this.updateErrands = updateErrands;
    }

    public int getInstanceNumber() {
        return instanceNumber;
    }

    public void setInstanceNumber(int instanceNumber) {
        this.instanceNumber = instanceNumber;
    }

    public List<String> getCreateErrands() {
        return createErrands;
    }

    public void setCreateErrands(List<String> createErrands) {
        this.createErrands = createErrands;
    }

    public List<String> getUpdateErrands() {
        return updateErrands;
    }

    public void setUpdateErrands(List<String> updateErrands) {
        this.updateErrands = updateErrands;
    }
}
