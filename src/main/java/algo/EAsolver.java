package algo;

import java.util.ArrayList;
import java.util.Random;

import fitnessFunc.Fitness;
import mutation.Mutation;

public class EAsolver {
	public String getName() {
		return "EAsolver";
	}

	public BitString run(int gamma, int individLen, int evSize, Fitness fitness, Mutation mutation) {
		Random random = new Random();
		BitString ind = Init.run(individLen, random);
		ArrayList<Integer> ff = new ArrayList<Integer>();
		ff.add(fitness.fitness(ind));
		//System.out.println(fitness.fitness(ind));	
		
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
		
		return ind;
	}

}
