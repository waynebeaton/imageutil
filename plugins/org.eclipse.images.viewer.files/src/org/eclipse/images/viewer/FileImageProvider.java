/*******************************************************************************
 * Copyright (c) 2006, 2017 The Eclipse Foundation.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Wayne Beaton (The Eclipse Foundation) - initial API and implementation
 *******************************************************************************/
package org.eclipse.images.viewer;

import java.io.InputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.images.providers.ImageProvider;
import org.eclipse.images.viewer.files.activator.FileImageProviderActivator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Image;

public class FileImageProvider extends ImageProvider {

	private final IFile file;
	private IResourceChangeListener resourceChangeListener;

	public FileImageProvider(IFile file) {
		this.file = file;
	}
	
	@Override
	public void init() {
		/*
		 * Create a resource change listener that listens for workspace
		 * changes. Walk through the changes (deltas); if any of them
		 * are the file held by the receiver, send a change notification
		 * to the receiver's listeners.
		 */
		resourceChangeListener = new IResourceChangeListener() {
			@Override
			public void resourceChanged(IResourceChangeEvent event) {
				try {
					event.getDelta().accept(new IResourceDeltaVisitor() {
						@Override
						public boolean visit(IResourceDelta delta) throws CoreException {
							if (delta.getResource().equals(FileImageProvider.this.file)) {
								sendImageChangeNotification();
								return false;
							}
							return true;
						}
						
					});
				} catch (CoreException e) {
					FileImageProviderActivator.log(e);
				}
			}
		};
		file.getWorkspace().addResourceChangeListener(resourceChangeListener, IResourceChangeEvent.POST_CHANGE);
	}
	
	@Override
	public void dispose() {
		file.getWorkspace().removeResourceChangeListener(resourceChangeListener);
	}
	
	@Override
	public Image getImage(Device device, IProgressMonitor progress) {
		try (InputStream in = file.getContents()) {
			return new Image(device, in);
		} catch (SWTException e) {
			if (e.code != SWT.ERROR_UNSUPPORTED_FORMAT) 
				FileImageProviderActivator.log(e);
		} catch (Exception e) {
			FileImageProviderActivator.log(e);
		}
		return null;
	}
	
	@Override
	public boolean equals(Object object) {
		if (object == null) return false;
		if (object.getClass() != this.getClass()) return false;
		return file.equals(((FileImageProvider)object).file);
	}
	
	@Override
	public int hashCode() {
		return file.hashCode();
	}
}
