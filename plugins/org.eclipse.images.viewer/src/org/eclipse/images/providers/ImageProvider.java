/*******************************************************************************
 * Copyright (c) 2006, 2017 The Eclipse Foundation.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    The Eclipse Foundation - initial API and implementation
 *******************************************************************************/
package org.eclipse.images.providers;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Image;

public abstract class ImageProvider {
	ListenerList<ImageChangeListener> listeners = new ListenerList<>();

	public static boolean isImageExtension(String extension) {
		if (extension == null) return false;
		switch (extension.toUpperCase()) {
			case "BMP":
			case "ICO":
			case "JPEG":
			case "JPG":
			case "GIF":
			case "PNG":
			case "TIFF":
			case "TIF":
				return true;
			default:
				return false;
		}
	}
	
	public void init() {}

	public void dispose() {}
	
	/**
	 * This method answers an {@link Image}. The image will be managed by
	 * the consumer; that is, once the image is provided, the instance
	 * must assume that it may be disposed by the consumer at any time.
	 * 
	 * Before returning, implementors should check to see if the monitor
	 * has received a request to cancel; in that event, the implementor must
	 * clean up any allocated resources and return <code>null</code>.
	 *
	 * 
	 * @param device The device the image will be displayed on.
	 * @param monitor The progress monitor (never <code>null</code>).
	 * 
	 * @return {@link Image}
	 */
	abstract public Image getImage(Device device, IProgressMonitor monitor);
	
	public void addImageChangeListener(ImageChangeListener listener) {
		listeners.add(listener);
	}
	
	public void removeImageChangeListener(ImageChangeListener listener) {
		listeners.remove(listener);
	}
	
	protected void sendImageChangeNotification() {
		listeners.forEach(listener -> listener.imageChanged(this));
	}
}
