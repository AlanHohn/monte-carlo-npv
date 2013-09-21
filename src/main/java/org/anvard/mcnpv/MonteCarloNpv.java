package org.anvard.mcnpv;

import java.util.concurrent.ForkJoinPool;

public class MonteCarloNpv {

	public static void main(String[] args) {
		// Set up initial investment and cash flows
		Distribution initial = new SingleValueDistribution(-20000);
		Distribution year1 = new TriangleDistribution(0, 4000, 10000);
		Distribution year2 = new TriangleDistribution(0, 4000, 10000);
		Distribution year3 = new TriangleDistribution(1000, 8000, 20000);
		Distribution year4 = new TriangleDistribution(1000, 8000, 20000);
		Distribution year5 = new TriangleDistribution(5000, 12000, 40000);
		Distribution rate = new TriangleDistribution(2, 4, 8);
		
		ForkJoinPool pool = new ForkJoinPool();
		NpvTask task = new NpvTask(10, 100, rate, initial, year1, year2, year3, year4, year5);
		StatsCollector stats = pool.invoke(task);
		System.out.println(stats);
	}
}
