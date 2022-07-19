package fitnessFunc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import algo.BitString;

public class LeadingOnes implements Fitness{
	
	BitString optimum;
	int len;
	
	
	public LeadingOnes(BitString a, int SIZE){
        
		this.optimum = a;
		len = SIZE;
		
	}
	    public String getName() {
	        return "LeadingOnes";
	    }

		public int fitness(BitString a) {
			int b = Math.min (len, a.LOmask(optimum));

			return b;
		}
		public int maxValue() {
			return len;
		}

		@Override
		public BitString getOptimum() {
			// TODO Auto-generated method stub
			return optimum;
		}

		@Override
		public void optimumChange(String type) {
			switch (type) {
			case "Single":
				Random random = new Random();	
				int index = random.nextInt(len);
				this.optimum.flip(index);
			    break;
			    
			case "Five":
				ArrayList<Integer> indexes = new ArrayList<Integer>();// = new int[size];
		    	for (int i = 0; i < len; i++) {
		    		indexes.add(i);		    	    
		    	}		    	
		    	Collections.shuffle(indexes);
		    	for (int i = 0; i < 5; ++i) {
		    		this.optimum.flip(indexes.get(i));
		    	}
			    break;
			case "Three":
				ArrayList<Integer> indexes3 = new ArrayList<Integer>();// = new int[size];
		    	for (int i = 0; i < len; i++) {
		    		indexes3.add(i);		    	    
		    	}		    	
		    	Collections.shuffle(indexes3);
		    	for (int i = 0; i < 3; ++i) {
		    		this.optimum.flip(indexes3.get(i));
		    	}
			    break;
			case "Ten":
				ArrayList<Integer> indexes4 = new ArrayList<Integer>();// = new int[size];
		    	for (int i = 0; i < len; i++) {
		    		indexes4.add(i);		    	    
		    	}		    	
		    	Collections.shuffle(indexes4);
		    	for (int i = 0; i < 10; ++i) {
		    		this.optimum.flip(indexes4.get(i));
		    	}
			    break;
			
			case "Permutation":		
				System.out.println("Before permut = " + this.optimum.toString() + "number of ones" + this.optimum.bitCount());
				this.optimum.permutation();
				System.out.println("After permut = " + this.optimum.toString() + "number of ones" + this.optimum.bitCount());
			    break;
			default:
				throw new IllegalArgumentException("Invalid type name: " + type);	
			}		
		}
		





		
	    


	}



