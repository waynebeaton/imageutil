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
package org.eclipse.images.viewer;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.images.providers.ImageProvider;

public class IFileAdapterFactory implements IAdapterFactory {

	/**
	 * This method creates an adapter for the given object. Since
	 * the extension-point only specifies this factory for implementors
	 * of {@link IFile} adapted to {@link ImageProvider} no type
	 * checking is done.
	 */
	public Object getAdapter(Object adaptableObject, Class adapterType) {
		return new FileImageProvider((IFile) adaptableObject);
	}

	public Class[] getAdapterList() {
		return new Class[] {IFile.class};
	}

}
