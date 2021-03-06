/*
 * Copyright 2005-2010 Kirill Grouchnikov, based on work by
 * Sun Microsystems, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */
package org.pushingpixels.substance.swingx;

import java.awt.*;
import java.awt.image.BufferedImage;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;

import org.jdesktop.swingx.JXStatusBar;
import org.jdesktop.swingx.plaf.basic.BasicStatusBarUI;
import org.pushingpixels.substance.api.*;
import org.pushingpixels.substance.internal.painter.DecorationPainterUtils;
import org.pushingpixels.substance.internal.painter.SeparatorPainterUtils;
import org.pushingpixels.substance.internal.utils.*;

/**
 * Substance-consistent UI delegate for {@link JXStatusBar}.
 * 
 * @author Kirill Grouchnikov
 */
public class SubstanceStatusBarUI extends BasicStatusBarUI {
	/**
	 * Background delegate.
	 */
	private SubstanceSwingxFillBackgroundDelegate bgDelegate;

	public static ComponentUI createUI(JComponent comp) {
		SubstanceCoreUtilities.testComponentCreationThreadingViolation(comp);
		return new SubstanceStatusBarUI();
	}

	/**
	 * Creates a new UI delegate.
	 */
	public SubstanceStatusBarUI() {
		super();
		this.bgDelegate = new SubstanceSwingxFillBackgroundDelegate();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.jdesktop.swingx.plaf.basic.BasicStatusBarUI#installDefaults(org.jdesktop
	 * .swingx.JXStatusBar)
	 */
	@Override
	protected void installDefaults(JXStatusBar sb) {
		super.installDefaults(sb);
		SubstanceLookAndFeel.setDecorationType(sb, DecorationAreaType.FOOTER);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.jdesktop.swingx.plaf.basic.BasicStatusBarUI#uninstallDefaults(org
	 * .jdesktop.swingx.JXStatusBar)
	 */
	@Override
	protected void uninstallDefaults(JXStatusBar sb) {
		DecorationPainterUtils.clearDecorationType(sb);
		super.uninstallDefaults(sb);
	}

	@Override
	protected void paintBackground(Graphics2D g, JXStatusBar bar) {
		this.bgDelegate.paint(bar, g, true);

		JRootPane rootPane = SwingUtilities.getRootPane(bar);
		Window window = SwingUtilities.getWindowAncestor(bar);
		boolean isResizable = false;
		if (window instanceof JFrame) {
			JFrame frame = (JFrame) window;
			isResizable = frame.isResizable()
					&& (frame.getExtendedState() != JFrame.MAXIMIZED_BOTH);
		}
		if (window instanceof JDialog) {
			isResizable = ((JDialog) window).isResizable();
		}
		boolean hasResizeGrip = SubstanceCoreUtilities
				.toShowExtraWidgets(rootPane)
				&& isResizable;
		if (hasResizeGrip) {
			int dim = bar.getHeight() * 2 / 3;
			SubstanceColorScheme scheme = SubstanceColorSchemeUtilities
					.getColorScheme(bar, ColorSchemeAssociationKind.SEPARATOR,
							ComponentState.ENABLED);
			BufferedImage resizeImage = SubstanceImageCreator
					.getResizeGripImage(bar, scheme, dim, false);
			g.drawImage(resizeImage, bar.getWidth() - dim, bar.getHeight()
					- dim, null);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.jdesktop.swingx.plaf.basic.BasicStatusBarUI#paintSeparator(java.awt
	 * .Graphics2D, org.jdesktop.swingx.JXStatusBar, int, int, int, int)
	 */
	@Override
	protected void paintSeparator(Graphics2D g, JXStatusBar bar, int x, int y,
			int w, int h) {
		g.translate(x, y);
		SeparatorPainterUtils.paintSeparator(bar, g, w, h, JSlider.VERTICAL,
				true, 4);
		g.translate(-x, -y);
	}

	@Override
	protected int getSeparatorWidth() {
		return SubstanceSizeUtils.getAdjustedSize(SubstanceSizeUtils
				.getComponentFontSize(this.statusBar), super
				.getSeparatorWidth(), 2, 1, false);
	}

	@Override
	protected Insets getSeparatorInsets(Insets insets) {
		Insets result = super.getSeparatorInsets(insets);
		int totalSeparatorWidth = this.getSeparatorWidth();
		int componentFontSize = SubstanceSizeUtils
				.getComponentFontSize(this.statusBar);
		int lineSeparatorWidth = (int) (2 * SubstanceSizeUtils
				.getBorderStrokeWidth(componentFontSize));
		int widthLeft = (totalSeparatorWidth - lineSeparatorWidth - result.left - result.right);
		if (widthLeft > 0) {
			result.left += widthLeft / 2;
			result.right += widthLeft / 2;
		}
		result.top = SubstanceSizeUtils.getAdjustedSize(componentFontSize,
				result.top, 4, 1, false);
		result.bottom = SubstanceSizeUtils.getAdjustedSize(componentFontSize,
				result.bottom, 4, 1, false);
		return result;
	}
}
