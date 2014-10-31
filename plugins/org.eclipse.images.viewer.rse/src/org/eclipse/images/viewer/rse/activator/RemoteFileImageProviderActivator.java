/*******************************************************************************
 * Copyright (c) 2006 The Eclipse Foundation.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    The Eclipse Foundation - initial API and implementation
 *******************************************************************************/
package org.eclipse.images.viewer.rse.activator;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.plugin.AbstractUIPlugin;

public class RemoteFileImageProviderActivator extends AbstractUIPlugin {
	private static final String PLUGIN_ID = "org.eclipse.images.viewer.files";
	
	private static RemoteFileImageProviderActivator plugin;
	
	public RemoteFileImageProviderActivator() {
		plugin = this;
	}
	
	public static RemoteFileImageProviderActivator getDefault() {
		return plugin;
	}

	public static void log(Exception e) {
		getDefault().getLog().log(new Status(IStatus.ERROR, PLUGIN_ID, 0, e.getMessage(), e));
	}

}
