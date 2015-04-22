package com.faris.fakeadmin.api;

public interface Handler {

	/**
	 * Get the name of the plugin this code is in.
	 *
	 * @return The name of the plugin this code is in.
	 */
	public String getPluginName();

	/**
	 * Get the value of whatever this instance is handling.
	 *
	 * @param parameters - The parameters passed through
	 */
	public Object getValue(Object... parameters);

}
