package com.cti.vpx.controls.hex;


import java.util.EventListener;

/**
 * An object listening for events from a {@link HexEditor}.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public interface HexEditorListener extends EventListener {

	/**
	 * Called when bytes in a hex editor are added, removed, or modified.
	 *
	 * @param e
	 *            The event object.
	 */
	public void hexBytesChanged(HexEditorEvent e);

}