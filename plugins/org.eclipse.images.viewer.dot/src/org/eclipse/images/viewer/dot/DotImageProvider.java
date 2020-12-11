/*******************************************************************************
 * Copyright (c) 2016 The Eclipse Foundation.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Wayne Beaton (The Eclipse Foundation) - initial API and implementation
 *******************************************************************************/
package org.eclipse.images.viewer.dot;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.images.providers.ImageProvider;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Image;

public class DotImageProvider extends ImageProvider {

	private String source;

	public DotImageProvider(String source) {
		this.source = source;
	}

	@Override
	public Image getImage(Device device, IProgressMonitor progress) {
		// TODO What if graphviz dot is not installed?

		try {
			Process dot = Runtime.getRuntime().exec("dot -Tpng");
			try (Writer writer = new OutputStreamWriter(dot.getOutputStream())) {
				writer.append(source);
			}
			try (InputStream input = dot.getInputStream()) {
				return new Image(device, input);
			}
		} catch (IOException|SWTException e) {
			// TODO Log this
		}

		return null;
	}

	@Override
	public boolean equals(Object object) {
		if (object == null) return false;
		if (object.getClass() != this.getClass()) return false;
		return source.equals(((DotImageProvider)object).source);
	}

	@Override
	public int hashCode() {
		return source.hashCode();
	}
}
