/*******************************************************************************
 * Copyright (c) 2014 The Eclipse Foundation.
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
import org.eclipse.rse.files.ui.resources.SystemEditableRemoteFile;
import org.eclipse.rse.subsystems.files.core.subsystems.IRemoteFile;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Image;

public class RemoteFileImageProvider implements ImageProvider {

	private final IRemoteFile file;

	public RemoteFileImageProvider(IRemoteFile file) {
		this.file = file;
	}

	public Image getImage(Device device, IProgressMonitor monitor) {
		// Do some simple tests to avoid trying to preview something
		// that is not an image.
		if (!this.file.isFile()) return null;
		if (!this.file.isBinary()) return null;
		
		// If it's local, go for it.
		Object file = this.file.getFile();
		if (file instanceof File) return getImage((File)file, device);		
		
		//if (this.file.getLength() > somethreshold) return null;
		
		// Because a remote file can be very large, let's avoid trying to
		// load anything that doesn't look like an image.
		switch (this.file.getExtension().toUpperCase()) {
			case "BMP":
			case "ICO":
			case "JPEG":
			case "JPG":
			case "GIF":
			case "PNG":
			case "TIFF":
			case "TIF":
				break;
			default:
				return null;
		}
		
		SystemEditableRemoteFile remote = new SystemEditableRemoteFile(this.file);
		try {
			if (!remote.download(monitor)) return null;
		} catch (Exception e) {
			RemoteFileImageProviderActivator.log(e);
			return null;
		}
		
		if (monitor.isCanceled()) return null;
		
		File local = new File(remote.getLocalPath());
		return getImage(local, device);
	}
	
	Image getImage(File file, Device device) {
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
