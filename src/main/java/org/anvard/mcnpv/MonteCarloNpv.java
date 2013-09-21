package org.anvard.mcnpv;

import java.util.concurrent.ForkJoinPool;

import org.springframework.util.StopWatch;

public class MonteCarloNpv {

	private Distribution initial;
	private Distribution year1;
	private Distribution year2;
	private Distribution year3;
	private Distribution year4;
	private Distribution year5;
	private Distribution rate;

	public MonteCarloNpv() {
		initial = new SingleValueDistribution(-20000);
		year1 = new TriangleDistribution(0, 4000, 10000);
		year2 = new TriangleDistribution(0, 4000, 10000);
		year3 = new TriangleDistribution(1000, 8000, 20000);
		year4 = new TriangleDistribution(1000, 8000, 20000);
		year5 = new TriangleDistribution(5000, 12000, 40000);
		rate = new TriangleDistribution(2, 4, 8);
	}

	public StatsCollector parallel(int minChunkSize, int numChunks) {
		ForkJoinPool pool = new ForkJoinPool();
		NpvTask task = new NpvTask(10, 1000000, rate, initial, year1, year2,
				year3, year4, year5);
		task.setMinChunkSize(minChunkSize);
		task.setNumChunks(numChunks);
		return pool.invoke(task);
	}

	public StatsCollector sequential() {
		NpvTask task = new NpvTask(10, 1000000, rate, initial, year1, year2,
				year3, year4, year5);
		task.setFork(false);
		return task.compute();
	}

	public static void main(String args[]) {
		MonteCarloNpv npv = new MonteCarloNpv();
		StopWatch sw = new StopWatch("Monte Carlo NPV");

		sw.start("Sequential");
		npv.sequential();
		sw.stop();
		
		int[] chunkSizes = { 100, 500, 1000, 2000 };
		
		for (int i = 2; i < 7; i++) {
			for (int j: chunkSizes) {
				String name = "Parallel (children=" + i + ", min fork size=" + j + ")";
				sw.start(name);
				npv.parallel(j, i);
				sw.stop();
			}
		}
		System.out.println(sw.prettyPrint());
	}

}
