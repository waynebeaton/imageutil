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
package org.eclipse.images.providers;

import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Image;

public interface ImageProvider {

	/**
	 * This method returns an {@link Image}. The implementor must
	 * return an image that can be disposed() by the caller. The
	 * implementor is not given any notification that the dispose is
	 * occurring.
	 * 
	 * @param target The device the image will be displayed on.
	 * 
	 * @return {@link Image}
	 */
	Image getImage(Device target);
	
	/**
	 * This method is used to dispose an image obtained by the
	 * getImage(Device) method.
	 * 
	 * @param image the image that needs to be disposed of.
	 */
	void disposeImage(Image image);

}
