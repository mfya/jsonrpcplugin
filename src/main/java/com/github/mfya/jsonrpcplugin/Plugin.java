package com.github.mfya.jsonrpcplugin;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.swing.JComponent;

import net.pms.dlna.DLNAMediaInfo;
import net.pms.dlna.DLNAResource;
import net.pms.encoders.Player;
import net.pms.external.AdditionalFoldersAtRoot;
import net.pms.external.FinalizeTranscoderArgsListener;
import net.pms.external.URLResolver;
import net.pms.io.OutputParams;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.googlecode.jsonrpc4j.JsonRpcHttpClient;
import com.googlecode.jsonrpc4j.ProxyUtil;

public class Plugin implements AdditionalFoldersAtRoot, URLResolver,
		FinalizeTranscoderArgsListener {
	private static final Logger LOGGER = LoggerFactory.getLogger(Plugin.class);

	Config config;
	JsonRpcHttpClient client;
	RemotePluginService service;
	RootFolder rootFolder;

	public Plugin() throws MalformedURLException {
		config = new Config();

		// If the service url is invalid, the plugin cannot be initialized.
		// Not sure what UMS does, if the constructor throws, but it makes no
		// sense to keep an unusable plugin instance around.
		URL serviceUrl = null;
		try {
			serviceUrl = new URL(config.getServiceUrl());
		} catch (MalformedURLException e) {
			LOGGER.error(
					"Invalid remote service url. Fix it or uninstall the plugin.",
					e);
			throw e;
		}

		client = new JsonRpcHttpClient(serviceUrl);
		service = ProxyUtil.createClientProxy(
				RemotePluginService.class.getClassLoader(),
				RemotePluginService.class, client);
		rootFolder = new RootFolder(config, service);
	}

	public JComponent config() {
		return null;
	}

	public String name() {
		return "JSON-RPC Plugin";
	}

	public void shutdown() {
	}

	// AdditionalFoldersAtRoot implementation
	public Iterator<DLNAResource> getChildren() {
		return Arrays.<DLNAResource> asList(rootFolder).iterator();
	}

	// URLResolver implementation
	public URLResult urlResolve(String url) {
		try {
			return service.urlResolve(url);
		} catch (Throwable e) {
			LOGGER.error("Error in service.urlResolve", e);
			return null;
		}
	}

	// FinalizeTranscoderArgsListener implementation
	public List<String> finalizeTranscoderArgs(Player player, String filename,
			DLNAResource dlna, DLNAMediaInfo media, OutputParams params,
			List<String> cmdList) {
		try {
			return service.finalizeTranscoderArgs(filename,
					getResourcePath(dlna), dlna.getType(),
					params.input_pipes[0].getInputPipe(), player.name(),
					params.mediaRenderer.getRendererName(), cmdList);
		} catch (Throwable e) {
			LOGGER.error("Error in service.finalizeTranscoderArgs", e);
			return cmdList;
		}
	}

	public static List<String> getResourcePath(DLNAResource node) {
		List<String> path = new ArrayList<String>();
		while (node.getParent() != null) {
			path.add(0, node.getName());
			node = node.getParent();
		}
		return path;
	}
}
