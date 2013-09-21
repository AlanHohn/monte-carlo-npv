package org.anvard.mcnpv;

import org.springframework.util.Assert;

public final class NetPresentValue {

	private NetPresentValue() {
	}

	public static double npv(double[] flows, double discountRate) {
		Assert.notNull(flows);
		double result = 0;
		double rate = 1 + (discountRate * 0.01);
		for (int i = 0; i < flows.length; i++) {
			result += flows[i] / Math.pow(rate, i);
		}
		return result;
	}

}
