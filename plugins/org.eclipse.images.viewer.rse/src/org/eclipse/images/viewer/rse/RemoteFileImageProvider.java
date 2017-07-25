/*******************************************************************************
 * Copyright (c) 2014, 2017 The Eclipse Foundation.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *   Wayne Beaton (The Eclipse Foundation) - initial API and implementation
 *******************************************************************************/
package org.eclipse.images.viewer.rse;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.images.providers.ImageProvider;
import org.eclipse.images.viewer.rse.activator.RemoteFileImageProviderActivator;
import org.eclipse.rse.services.clientserver.messages.SystemMessageException;
import org.eclipse.rse.subsystems.files.core.subsystems.IRemoteFile;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Image;

public class RemoteFileImageProvider extends ImageProvider {

	private final IRemoteFile file;

	public RemoteFileImageProvider(IRemoteFile file) {
		this.file = file;
	}

	public Image getImage(Device device, IProgressMonitor monitor) {
		// Do some simple tests to avoid trying to preview something
		// that is not an image.
		if (!file.isFile()) return null;
		if (!file.isBinary()) return null;
		
		// If it's local, go for it.
		if (file.getFile() instanceof File) 
			return loadImage((File)file.getFile(),device);		
		
		//if (this.file.getLength() > somethreshold) return null;
		
		// Because a remote file can be very large, let's avoid trying to
		// load anything that doesn't look like an image.
		if (!ImageProvider.isImageExtension(file.getExtension())) return null;
		
		File local = null;
		try {
			local = File.createTempFile("image", file.getExtension());
		} catch (IOException e) {
			return null;
		}
		
		try {
			file.getParentRemoteFileSubSystem().download(file, local.getAbsolutePath(), this.file.getEncoding(), monitor);
		} catch (SystemMessageException e) {
			return null;
		}

		if (monitor.isCanceled()) return null;
		
		Image image = loadImage(local, device);
		
		local.delete();
		
		return image;
	}
	

	protected Image loadImage(File file, Device device) {
		if (!file.exists()) return null;
		
		InputStream in = null;
		try {
			in = new FileInputStream(file);
			return new Image(device, in);
		} catch (SWTException e) {
			if (e.code != SWT.ERROR_UNSUPPORTED_FORMAT) 
				RemoteFileImageProviderActivator.log(e);
		} catch (Exception e) {
			RemoteFileImageProviderActivator.log(e);
		} finally {
			try {
				in.close();
			} catch (IOException e) {
			}
		}
		return null;
	}
	
	@Override
	public boolean equals(Object object) {
		if (object == null) return false;
		if (object.getClass() != this.getClass()) return false;
		return file.equals(((RemoteFileImageProvider)object).file);
	}
	
	@Override
	public int hashCode() {
		return file.hashCode();
	}
}
