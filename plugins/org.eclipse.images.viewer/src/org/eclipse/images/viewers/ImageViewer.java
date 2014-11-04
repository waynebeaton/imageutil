/*******************************************************************************
 * Copyright (c) 2005 The Eclipse Foundation.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    The Eclipse Foundation - initial API and implementation
 *******************************************************************************/
package org.eclipse.images.viewers;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;

public class ImageViewer extends Canvas {

	/*
	 * Note that instances of this class do not manage the image.
	 * That is, the image will not be disposed() by this instance
	 * under any conditions. Users of this class must manage the image.
	 */
	private Image image;

	public ImageViewer(Composite parent, int style) {
		super(parent, style);
		initialize();
	}
	
	private void initialize() {
		addPaintListener(new org.eclipse.swt.events.PaintListener() {
			public void paintControl(org.eclipse.swt.events.PaintEvent e) {
				if (image == null) return;
				drawImage(e.gc);
			}
		});
	}
		
	private void drawImage(GC gc) {
		if (image == null) return;
		if (image.isDisposed()) return;
		Rectangle imageBounds = image.getBounds();
		Rectangle drawingBounds = getDrawingBounds();
		
		gc.drawImage(image, 0, 0, imageBounds.width, imageBounds.height, drawingBounds.x, drawingBounds.y, drawingBounds.width, drawingBounds.height);
	}
	
	private Rectangle getDrawingBounds() {
		Rectangle imageBounds = image.getBounds();
		Rectangle canvasBounds = getBounds();
		
		double hScale = (double)canvasBounds.width/imageBounds.width;
		double vScale = (double)canvasBounds.height/imageBounds.height;
		
		double scale = Math.min(1.0d, Math.min(hScale, vScale));
		
		int width = (int)(imageBounds.width * scale);
		int height = (int)(imageBounds.height * scale);
		
		int x = (canvasBounds.width - width) / 2;
		int y = (canvasBounds.height - height) / 2;
		
		return new Rectangle(x, y, width, height);
	}
	
	/**
	 * This method sets the image that the receiver is responsible for drawing.
	 * This class does not manage the image; it only displays it. Any image
	 * the receiver is currently displaying will simply be replaced by the
	 * value provided in the parameter. The caller is responsible for
	 * disposing the images.
	 * 
	 * @param image the Image to display.
	 */
	public void setImage(Image image) {
		this.image = image;
		redraw();
	}

}
