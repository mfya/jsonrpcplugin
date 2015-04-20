package com.github.mfya.jsonrpcplugin;

import java.util.List;

import net.pms.external.URLResolver.URLResult;
import net.pms.formats.Format;

public interface RemotePluginService {

	static class ResourceItem {
		public List<String> path;
		public String url;
		public String thumburl;
		public int type = Format.UNKNOWN;
	}

	static class FolderContents {
		public String updateId;
		public List<ResourceItem> items;
	}

	boolean isFolderRefreshNeeded(String lastUpdateId) throws Throwable;

	FolderContents getFolderContents() throws Throwable;

	URLResult urlResolve(String url) throws Throwable;

	List<String> finalizeTranscoderArgs(String filename, List<String> path,
			int type, String pipe, String player, String renderer,
			List<String> cmdList) throws Throwable;
}
