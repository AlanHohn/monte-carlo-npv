package org.anvard.mcnpv;

public class SingleValueDistribution implements Distribution {

	private double value;
	
	public SingleValueDistribution(double value) {
		this.value = value;
	}
	
	@Override
	public double sample() {
		return value;
	}

	@Override
	public double getMax() {
		return value;
	}

	@Override
	public double getMin() {
		return value;
	}

}
