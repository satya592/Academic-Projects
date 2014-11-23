package com.approximatrix.charting.event;

import java.util.EventListener;

public interface InfoAvailableListener extends EventListener {

	public void onInformationAvailable(InfoAvailableEvent event);
}
