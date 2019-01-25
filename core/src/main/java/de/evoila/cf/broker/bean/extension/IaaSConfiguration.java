package de.evoila.cf.broker.bean.extension;

import de.evoila.cf.broker.bean.enums.IaasPlatform;

/**
 * @author Johannes Hiemer.
 */
public class IaaSConfiguration {

    private IaasPlatform platform;

    public IaasPlatform getPlatform() {
        return platform;
    }

    public void setPlatform(IaasPlatform platform) {
        this.platform = platform;
    }
}
