package com.generic.job.marketplace.datasource.h2;

public final class FunctionAliases {

	private FunctionAliases() {

	}

	/**
	 * This method is a dummy for refreshing the materialized view in H2, in an
	 * actual persistent database like POSTGRES, the materialized view should be
	 * refreshed concurrently so that it doesn't block incoming read requests to
	 * the view. Example in SQL:
	 * <p>
	 * CREATE OR REPLACE FUNCTION jobmarket.refresh_materialized_view() returns
	 * void language plpgsql as
	 * <p>
	 * $$ begin REFRESH MATERIALIZED VIEW CONCURRENTLY jobmarket.bid_view; end; $$;
	 * </p>
	 * </p>
	 * 
	 */
	public static void refreshMaterializedView() {
		// Dummy implementation for H2
	}
}
