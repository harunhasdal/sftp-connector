package com.github.harunhasdal.livecycle.sftp;

import java.util.logging.Level;

import com.adobe.idp.dsc.component.ComponentContext;
import com.adobe.idp.dsc.component.LifeCycle;
import com.adobe.logging.AdobeLogger;
public class LifeCycleImpl implements LifeCycle{
	private static final AdobeLogger logger = AdobeLogger.getAdobeLogger(LifeCycleImpl.class);
	private ComponentContext m_ctx;
	public void setComponentContext(ComponentContext aCtx) {
		if (logger.isLoggable(Level.FINE)) {
			logger.log(Level.FINE, "setComponentContext: " + aCtx.getComponent().getComponentId());
		}
		m_ctx = aCtx;

	}
	public void onStart() {
		logger.log(Level.INFO, "Called onStart: " + m_ctx.getComponent().getComponentId());
	}
	public void onStop() {
		logger.log(Level.INFO, "Called onStop: " + m_ctx.getComponent().getComponentId());
	}
}
