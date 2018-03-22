package com.generic.job.marketplace.entitybuilder;

public interface EntityBuilder<I, E> {

	E buildFresh(I inputResource);
	// default method for future implementations, if subclasses don't provide an
	// implementation
	// this method, they shouldn't call it.
	default E buildForUpdate(E existingEntity, I inputResource) {
		throw new UnsupportedOperationException();
	}
}
