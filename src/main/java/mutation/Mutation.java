package mutation;

import algo.BitString;

import java.util.Random;
import fitnessFunc.*;
public interface Mutation {
    public BitString mutate(BitString individual, Random random, Fitness fit);
    public String getName();
  
}
