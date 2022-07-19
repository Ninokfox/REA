package algo;

import java.util.ArrayList;
import java.util.Random;

import fitnessFunc.Fitness;
import mutation.Mutation;

public class SimpleSolver {
	public String getName() {
		return "Gradient";
	}

	public ArrayList<Integer> run(int bestValue,BitString opt, String aname, int gamma, int individLen, int evSize, Fitness fitness, Mutation mutation) {
		Random random = new Random();
		BitString ind = opt;
		ArrayList<Integer> ff = new ArrayList<Integer>();
		ff.add(fitness.fitness(ind));
		//System.out.println(fitness.fitness(ind));
		
		if (aname.equals("EA")) {
			for (int t = 1; t < evSize; t++){
				
				BitString mutated = mutation.mutate(ind, random, fitness);
				int lastF = fitness.fitness(ind);
				int currF = fitness.fitness(mutated);
				if (currF >= lastF) {
					ind = mutated;					
					ff.add(currF);
				} else {
					ff.add(lastF);
				}					
			}
		}

		else if (aname.equals("REA")) {
			ArrayList<BitString> x = new ArrayList<BitString>(); // array of individuals on hamming distance
			ArrayList<Boolean> defined = new ArrayList<Boolean>();			
			int definedSize = 0;
			BitString parent = ind;
			BitString xStar = ind;		
			BitString xOld = ind;
			
			boolean reaIsWorking = true;
			for (int i = 0; i < gamma + 1; i++) {
				x.add(Init.run(individLen, random));
				defined.add(false);
			}					
			x.set(0, xOld);
			defined.set(0, true);
			definedSize++;			
			for (int t = 1; t < evSize; t++) {				
				if (reaIsWorking) {
					if (definedSize > 1) {							
						//int percent = (int)(1/0.5);
						//if (random.nextInt(percent) == 0) {									
						//	parent = xStar;									
						//} else {
							while (true) {
								int index = random.nextInt(x.size());
								BitString pp = x.get(index);
								if (pp.equals(xStar) || (!defined.get(index))) {
									continue;
								} else {
									parent = pp;
									break;
								}
							}
						//}						
						
					} else {
						parent = xStar;
					}
				} else {
					parent = xStar;
				}
				BitString mutated = mutation.mutate(parent, random, fitness);
				if (fitness.fitness(mutated) >= fitness.fitness(xStar)) {
					xStar = mutated;
					ff.add(fitness.fitness(mutated));

				} else {
					ff.add(fitness.fitness(xStar));
				}
				if (reaIsWorking) {
					int i = Math.min(mutated.hammingDist(xOld), gamma);
					
					if (defined.get(i)) {
						if (fitness.fitness(mutated) >= fitness.fitness(x.get(i))) {
							x.set(i, mutated);
							
						}
					} else {
						x.set(i, mutated);
						defined.set(i, true);
						definedSize++;
					}
					
					
					if (fitness.fitness(xStar) >= bestValue) {
						reaIsWorking = false;						
						definedSize = 0;
					}
				}
			}
		}

		return ff;
		

	}

}
