package de.evoila.cf.broker.model;

import java.io.Serializable;

/**
 * @author Christian Brinker.
 */
public interface BaseEntity<ID extends Serializable> {
	
	ID getId();
}
