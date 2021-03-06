/*******************************************************************************
 * Copyright (c) 2005, 2017 The Eclipse Foundation.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Wayne Beaton (The Eclipse Foundation) - initial API and implementation
 *******************************************************************************/
package org.eclipse.images.views;

import org.eclipse.core.runtime.Adapters;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.images.providers.ImageChangeListener;
import org.eclipse.images.providers.ImageProvider;
import org.eclipse.images.viewers.ImageViewer;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.modelio.diagram.editor.handlers.PngTransfer;

public class ImageView extends ViewPart {
	private ImageViewer viewer;
	private Job updateJob;
	private ImageProvider provider;
	private Image image;

	private Clipboard clipboard;

	/*
	 * The selectionListener listens for changes in the workbench's
	 * selection service.
	 */
	private ISelectionListener selectionListener = new ISelectionListener() {
		@Override
		public void selectionChanged(IWorkbenchPart part, ISelection selection) {
			handleSelection(selection);
		}
	};
	private ImageChangeListener imageChangeListener = new ImageChangeListener() {
		@Override
		public void imageChanged(ImageProvider imageProvider) {
			if (imageProvider != ImageView.this.provider) return;
			updateImage(imageProvider);
		}		
	};

	private void handleSelection(ISelection selection) {
		if (selection == null) return;
		if (selection instanceof IStructuredSelection) {
			handleSelection((IStructuredSelection) selection);
		} else if (selection instanceof ITextSelection) {
			handleSelection((Object)selection);
		}
	}

	private void handleSelection(IStructuredSelection selection) {
		if (selection.size() == 0) return;
		handleSelection(selection.getFirstElement());
	}

	private void handleSelection(Object object) {
		handleSelection(Adapters.adapt(object, ImageProvider.class));
	}

	private void handleSelection(ImageProvider provider) {
		if (provider == null) return;
		setImageProvider(provider);
	}

	@Override
	public void createPartControl(Composite parent) {
		parent.setLayout(new FillLayout());
		viewer = new ImageViewer(parent, SWT.NONE);
		clipboard = new Clipboard(parent.getDisplay());

		fillLocalToolBar(getViewSite().getActionBars().getToolBarManager());
		
		getSelectionService().addPostSelectionListener(selectionListener);

		handleSelection(getSelectionService().getSelection());
	}
	
	private void fillLocalToolBar(IToolBarManager manager) {
		Action copyAction = new Action() {
			public void run() {
				Display display = viewer.getDisplay();
				if (provider != null) {
					Image image = provider.getImage(display, null);
					if (image != null) {
						clipboard.setContents(new Object[] {image.getImageData()}, 
								new Transfer[] {PngTransfer.getInstance()});
						image.dispose();
					}
				}
			}
		};
		copyAction.setText("Copy");
		copyAction.setToolTipText("Copy image");
		copyAction.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().
				getImageDescriptor(ISharedImages.IMG_TOOL_COPY));
		
		manager.add(copyAction);
	}

	protected void setImageProvider(ImageProvider provider) {
		if (provider == null) return;
		if (provider.equals(this.provider)) return;
		
		if (this.provider != null) this.provider.dispose();
		
		this.provider = provider;
		provider.init();

		updateImage(provider);
	
		provider.addImageChangeListener(imageChangeListener);
	}
	
	protected void updateImage(ImageProvider provider) {
		if (updateJob != null) updateJob.cancel();

		updateJob = new Job("Load image") {
	        @Override
			public IStatus run(IProgressMonitor monitor) {
	        	if (viewer != null && !viewer.isDisposed()) {
		        	Image image = provider.getImage(viewer.getDisplay(), monitor);
	        		if (monitor.isCanceled()) {
	    	        	if (image != null) image.dispose();
	        		} else {
			        	viewer.getDisplay().syncExec(new Runnable() {
							@Override
							public void run() {
								setImage(image);
							}
			        	});
		        	}
	        	}
        		updateJob = null;
	        	return Status.OK_STATUS;
	        }
	    };

		updateJob.schedule(0);
	}

	/**
	 * Set the image into the viewer. If have previously set a value into
	 * the viewer, then dispose that image. This method must be called on
	 * the UI thread.
	 * 
	 * @param image
	 */
	private void setImage(Image image) {
		if (image == null) return;
		viewer.setImage(image);
		
		// Dispose the current image only after giving the viewer the new one.
		if (this.image != null) this.image.dispose();
		
		this.image = image;
	}

	@Override
	public void dispose() {
		super.dispose();
		clipboard.dispose();
		getSelectionService().removePostSelectionListener(selectionListener);
		
		if (provider != null) provider.dispose();
		if (image != null) image.dispose();
		
		provider = null;
		image = null;
	}

	@Override
	public void setFocus() {
		viewer.setFocus();
	}

	private ISelectionService getSelectionService() {
		return getSite().getWorkbenchWindow().getSelectionService();
	}
}