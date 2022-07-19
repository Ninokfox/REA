package fitnessFunc;

import java.util.Random;

import algo.BitString;
import algo.Init;

public class OneMax implements Fitness {
   
	BitString optimum;
	int len;
	  
	public OneMax(BitString a, int SIZE){
	          
		this.optimum = a;
		len = SIZE;
		
	}
	
	public int fitness(BitString a) {
		return len - a.hammingDist(optimum);
	}
	public int maxValue() {
		return len;
	}
	
    public String getName() {
        return "OneMax";
    }
    
    public BitString getOptimum() {
        return optimum;
    }


    public void optimumChange(String type) {
		switch (type) {
		case "Random":
			Random random = new Random();	
			
			this.optimum = Init.run(len, random); //random new optimum
		    break;
		
		case "Permutation":				
			this.optimum.permutation();
		    break;
		default:
			throw new IllegalArgumentException("Invalid type name: " + type);	
		}		
	}

}
