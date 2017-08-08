package org.traktor;

public class EngineDown {

	public final String name;
	public final LastValue last;
	
	public EngineDown(String uri) {
		this.name = "traktor.ui.local.internal." + uri + ".availability";
		this.last = new LastValue("Engine at " + uri + " not available");
	}
}
