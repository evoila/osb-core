package de.evoila.cf.broker.bean.enums;

/**
 * @author Johannes Hiemer.
 */
public enum IaasPlatform {

    // Environments: openstack, vsphere, aws etc.
    BOSH_LITE("bosh-lite"),
    OPENSTACK("openstack"),
    VSPHERE("vsphere"),
    AWS("aws");

    private final String name;

    IaasPlatform(String s) {
        name = s;
    }

    public boolean equalsName(String otherName) {
        return name.equals(otherName);
    }

    public String toString() {
        return this.name;
    }

}
