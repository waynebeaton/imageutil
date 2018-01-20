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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.images.providers.ImageProvider;
import org.eclipse.jface.text.ITextSelection;

public class DotTextAdapterFactory implements IAdapterFactory {


	/**
	 * This method creates an adapter for the given object. Since
	 * the extension-point only specifies this factory for implementors
	 * of {@link IFile} adapted to {@link ImageProvider} no type
	 * checking is done.
	 */
	@Override
	@SuppressWarnings("rawtypes")
	public Object getAdapter(Object adaptableObject, Class adapterType) {
		// FIXME The graph ID can be far more interesting.
		Pattern graphPattern = Pattern.compile("(?:static\\s*)?(?:di)?graph\\s*(?:\\w*\\s*)?\\{.*\\}", Pattern.MULTILINE | Pattern.DOTALL);

		String text = ((ITextSelection) adaptableObject).getText();
		
		if (text == null) return null;
		if (text.isEmpty()) return null;
		
		Matcher matcher = graphPattern.matcher(text);
		if (matcher.find()) {
			return new DotImageProvider(matcher.group());
		}
		return null;
	}

	@Override
	@SuppressWarnings("rawtypes")
	public Class[] getAdapterList() {
		return new Class[] {ITextSelection.class};
	}

}
