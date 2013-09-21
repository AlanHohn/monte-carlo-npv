package org.anvard.mcnpv;

public interface Distribution {

	double sample();

	double getMax();

	double getMin();

}