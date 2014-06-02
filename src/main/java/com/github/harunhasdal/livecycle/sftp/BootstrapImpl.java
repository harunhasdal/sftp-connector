package com.github.harunhasdal.livecycle.sftp;

import java.util.logging.Level;

import com.adobe.idp.dsc.component.Bootstrap;
import com.adobe.idp.dsc.component.BootstrapContext;
import com.adobe.logging.AdobeLogger;
public class BootstrapImpl implements Bootstrap{
	private static final AdobeLogger logger = AdobeLogger.getAdobeLogger(BootstrapImpl.class);
	private BootstrapContext m_ctx;
	public void setBootstrapContext(BootstrapContext aCtx) {
		logger.log(Level.INFO, "Set bootstrap context: " + aCtx.getComponent().getComponentId());
		m_ctx = aCtx;
	}
	public void onUnInstall() {
		logger.log(Level.INFO, "Called onUnInstall: " + m_ctx.getComponent().getComponentId());
	}
	public void onInstall() {
		logger.log(Level.INFO, "Called onInstall: " + m_ctx.getComponent().getComponentId());
	}
}
