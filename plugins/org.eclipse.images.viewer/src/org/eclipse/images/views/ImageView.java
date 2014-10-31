/*******************************************************************************
 * Copyright (c) 2005, 2014 The Eclipse Foundation.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Wayne Beaton (The Eclipse Foundation) - initial API and implementation
 *******************************************************************************/
package org.eclipse.images.views;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.Platform;
import org.eclipse.images.providers.ImageProvider;
import org.eclipse.images.viewers.ImageViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.ViewPart;

public class ImageView extends ViewPart {
	ImageViewer viewer;
	
	private ImageProvider provider;
	Image image;
	
	/*
	 * The selectionListener listens for changes in the workbench's
	 * selection service. 
	 */
	private ISelectionListener selectionListener = new ISelectionListener() {
		public void selectionChanged(IWorkbenchPart part, ISelection selection) {
			handleSelection(selection);
		}
	};
	
	private void handleSelection(ISelection selection) {
		if (selection == null) return;
		if (selection instanceof IStructuredSelection) {
			handleSelection((IStructuredSelection) selection);
		}
	}

	private void handleSelection(IStructuredSelection selection) {
		if (selection.size() == 0) return;
		handleSelection(selection.getFirstElement());
	}
	
	private void handleSelection(Object object) {
		ImageProvider provider = null;
		
		// First, if the object is adaptable, ask it to get an adapter.
		if (object instanceof IAdaptable)
			provider = (ImageProvider)((IAdaptable)object).getAdapter(ImageProvider.class);
		
		// If we haven't found an adapter yet, try asking the AdapterManager.
		if (provider == null)
			provider = (ImageProvider)Platform.getAdapterManager().loadAdapter(object, ImageProvider.class.getName());
		
		handleSelection(provider);
	}

	private void handleSelection(ImageProvider provider) {
		if (provider == null) return;
		setImageProvider(provider);
	}		
	
	public void createPartControl(Composite parent) {
		parent.setLayout(new FillLayout());
		viewer = new ImageViewer(parent, SWT.NONE);
		getSelectionService().addPostSelectionListener(selectionListener);
		handleSelection(getSelectionService().getSelection());
	}

	protected void setImageProvider(ImageProvider provider) {
		if (provider == null) return;
		Image image = provider.getImage(viewer.getDisplay());
		if (image != null) {
			viewer.setImage(image);
		}
		disposeImage();
		this.provider = provider;
		this.image = image;
	}

	public void dispose() {
		super.dispose();
		getSelectionService().removeSelectionListener(selectionListener);
		disposeImage();
	}

	private void disposeImage() {
		if (provider == null) return;
		if (image == null) return;
		provider.disposeImage(image);
		provider = null;
		image = null;
	}

	public void setFocus() {
		viewer.setFocus();
	}	

	private ISelectionService getSelectionService() {
		return getSite().getWorkbenchWindow().getSelectionService();
	}
}