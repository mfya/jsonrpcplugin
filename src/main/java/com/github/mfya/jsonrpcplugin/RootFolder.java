package com.github.mfya.jsonrpcplugin;

import java.util.List;

import net.pms.dlna.DLNAResource;
import net.pms.dlna.WebStream;
import net.pms.dlna.virtual.VirtualFolder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.mfya.jsonrpcplugin.RemotePluginService.FolderContents;
import com.github.mfya.jsonrpcplugin.RemotePluginService.ResourceItem;

public class RootFolder extends VirtualFolder {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(RootFolder.class);

	Config config;
	RemotePluginService service;
	String currentUpdateId;

	public RootFolder(Config config, RemotePluginService service) {
		super(config.getRootFolderName(), null);
		this.config = config;
		this.service = service;
	}

	void updateContents() throws Throwable {
		FolderContents contents = service.getFolderContents();
		for (ResourceItem item : contents.items) {
			int lastIndex = item.path.size() - 1;
			DLNAResource child = new WebStream(item.path.get(lastIndex),
					item.url, item.thumburl, item.type);
			DLNAResource folder = getSubFolder(this,
					item.path.subList(0, lastIndex));
			folder.addChild(child);
		}

		currentUpdateId = contents.updateId;
		setLastModified(System.currentTimeMillis());
	}

	static DLNAResource getSubFolder(DLNAResource node, List<String> folders) {
		for (String name : folders) {
			DLNAResource folder = node.searchByName(name);
			if (folder == null) {
				folder = new VirtualFolder(name, null);
				node.addChild(folder);
			}
			node = folder;
		}
		return node;
	}

	@Override
	protected void resolveOnce() {
		doRefreshChildren();
	}

	@Override
	public boolean isRefreshNeeded() {
		// This method is called multiple times in a row, so we keep a delay
		// between multiple queries to the remote service.
		if (System.currentTimeMillis() - getLastModified() < config
				.getRefreshIntervalSec()) {
			return false;
		}
		try {
			return service.isFolderRefreshNeeded(currentUpdateId);
		} catch (Throwable e) {
			// If there is any exception try to refresh anyways, so the folder
			// contents and the lastModified timestamp are modified accordingly.
			LOGGER.error("Error in service.isFolderRefreshNeeded", e);
			return true;
		}
	}

	@Override
	public void doRefreshChildren() {
		try {
			getChildren().clear();
			updateContents();
		} catch (Throwable e) {
			LOGGER.error("Error in refreshing folder contents", e);
			addChild(new VirtualFolder("Error: "
					+ e.getClass().getSimpleName()
					+ (e.getCause() != null ? "("
							+ e.getCause().getClass().getSimpleName() + ")"
							: ""), null));
			setLastModified(System.currentTimeMillis());
		}
	}
}
