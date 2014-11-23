package com.approximatrix.charting.event;

import java.awt.Point;
import java.util.EventObject;

public class InfoAvailableEvent extends EventObject {

	private Point value = null;
	
	public InfoAvailableEvent(Object source, Point value) {
		super(source);
		this.value = value;
	}

	public Point getDataPoint() {
		return value;
	}
}
