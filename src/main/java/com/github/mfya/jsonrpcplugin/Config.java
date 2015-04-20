package com.github.mfya.jsonrpcplugin;

import net.pms.PMS;

class Config {
	int refreshIntervalSec;
	String serviceUrl;
	String rootFolderName;

	Config() {
		refresh();
	}

	public void refresh() {
		refreshIntervalSec = getProperty("jsonrpcplugin.refresh_secs", 5);
		serviceUrl = getProperty("jsonrpcplugin.service_url",
				"http://127.0.0.1:4000/RemotePluginService");
		rootFolderName = getProperty("jsonrpcplugin.root_folder_name", "Remote");
	}

	static <T> T getProperty(String property, T defaultval) {
		@SuppressWarnings("unchecked")
		T val = (T) PMS.getConfiguration().getCustomProperty(property);
		return (val != null) ? val : defaultval;
	}

	public int getRefreshIntervalSec() {
		return refreshIntervalSec;
	}

	public void setRefreshIntervalSec(int refreshIntervalSec) {
		this.refreshIntervalSec = refreshIntervalSec;
	}

	public String getServiceUrl() {
		return serviceUrl;
	}

	public void setServiceUrl(String serviceUrl) {
		this.serviceUrl = serviceUrl;
	}

	public String getRootFolderName() {
		return rootFolderName;
	}

	public void setRootFolderName(String rootFolderName) {
		this.rootFolderName = rootFolderName;
	}
}
