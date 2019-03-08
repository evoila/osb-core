package de.evoila.cf.broker.repository;

import de.evoila.cf.broker.model.Platform;
import de.evoila.cf.broker.service.PlatformService;

/**
 * @author Christian Brinker, Johannes Hiemer.
 */
public interface PlatformRepository {

	void addPlatform(Platform platform, PlatformService platformService);

	PlatformService getPlatformService(Platform platform);

}