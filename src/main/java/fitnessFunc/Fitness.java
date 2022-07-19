package fitnessFunc;
import java.util.Random;


import algo.BitString;

public interface Fitness{
	
    public int fitness(BitString individual);
    public String getName();
    public int maxValue();
	public BitString getOptimum();
	public void optimumChange(String type);
	//public void optimumChangePermutation();

}
