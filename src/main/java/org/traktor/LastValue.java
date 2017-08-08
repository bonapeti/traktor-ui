package org.traktor;

import java.time.Instant;

public class LastValue {

	
	public Instant  time = Instant.now();
	public final Object value;
	
	public LastValue(Object value) {
		super();
		this.value = value;
	}
	
	
}
