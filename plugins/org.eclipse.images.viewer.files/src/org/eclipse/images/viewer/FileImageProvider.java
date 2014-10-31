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

import java.io.IOException;
import java.io.InputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.images.providers.ImageProvider;
import org.eclipse.images.viewer.files.activator.FileImageProviderActivator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Image;

public class FileImageProvider implements ImageProvider {

	private final IFile file;

	public FileImageProvider(IFile file) {
		this.file = file;
	}

	public Image getImage(Device device) {
		/*
		 * TODO Do we need to be smarter? It might make sense to check the file
		 * extension to see if it's worth attempting to extract an image from
		 * the file. For now, performance seems adequate and there does not seem
		 * to be any bizarre side-effects.
		 */
		InputStream in = null;
		try {
			in = file.getContents();
			return new Image(device, in);
		} catch (SWTException e) {
			if (e.code != SWT.ERROR_UNSUPPORTED_FORMAT) 
				log(e);
		} catch (Exception e) {
			log(e);
		} finally {
			try {
				in.close();
			} catch (IOException e) {
			}
		}
		return null;
	}

	public void disposeImage(Image image) {
		image.dispose();
	}
	
	private void log(Exception e) {
		FileImageProviderActivator.log(e);
	}


}
