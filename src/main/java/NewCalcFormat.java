import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import algo.BitString;
import algo.CumulativeAlgo;
import algo.EAsolver;
import algo.HistoAlgo;
import algo.Init;
import algo.RuntimeAlgo;
import algo.SimpleSolver;
import fitnessFunc.Fitness;
import fitnessFunc.LeadingOnes;
import fitnessFunc.OneMax;
import mutation.MutEA;
import mutation.MutRLS;
import mutation.Mutation;

public class NewCalcFormat {
	public static void deleteDirectory(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i=0; i<children.length; i++) {
                File f = new File(dir, children[i]);
                deleteDirectory(f);
            }
            dir.delete();
        } else dir.delete();
    }
	
	public static ArrayList<Integer> histoOut(String aname, int rounds, boolean modefied, boolean smooth, double smoothSlope, double EAinREA, int kappa, 
			String type, int gamma, int dynamicBudjet, int individLen, int evSize, Fitness fitness, Mutation mutation,  boolean recentered) {
		
		ArrayList<Integer> Results = new ArrayList();
		for (int i = 0; i < individLen+1; ++i) {
			Results.add(0);
		}		
		for (int i = 0; i < rounds; ++i) {
			ArrayList<Integer> fit1 = new ArrayList();
			HistoAlgo algo = new HistoAlgo();						
			fit1 = algo.run(modefied, aname, smooth, smoothSlope, EAinREA, kappa, type, gamma, dynamicBudjet, individLen, evSize, fitness, mutation, recentered);
			for (int h =0; h < individLen+1; ++h) {
				int sum = Results.get(h) + fit1.get(h);
				Results.set(h, sum);	
				//System.out.print(Results.get(h) +" ");
			}			
		}		
		return Results;
    }	
	
	public static ArrayList<Integer> cumulativeOut(String aname, int rounds, boolean modefied, boolean smooth, double smoothSlope, double EAinREA, int kappa, 
			String type, int gamma, int dynamicBudjet, int individLen, int evSize, Fitness fitness, Mutation mutation,  boolean recentered) {
		
		ArrayList<Integer> Results = new ArrayList();
		for (int i = 0; i < individLen+1; ++i) {
			Results.add(0);
		}		
		for (int i = 0; i < rounds; ++i) {
			ArrayList<Integer> fit1 = new ArrayList();
			HistoAlgo algo = new HistoAlgo();						
			fit1 = algo.run(modefied, aname, smooth, smoothSlope, EAinREA, kappa, type, gamma, dynamicBudjet, individLen, evSize, fitness, mutation, recentered);
			for (int h =0; h < individLen+1; ++h) {
				int sum = Results.get(h) + fit1.get(h);
				Results.set(h, sum);	
				//System.out.print(Results.get(h) +" ");
			}
			
		}
		for (int h = individLen; h >0; --h) {
			System.out.println(h + " " + Results.get(h) +" " + Results.get(h-1));
			int sum = Results.get(h-1) + Results.get(h);
			//System.out.println(sum);
			Results.set(h-1, sum);
			//System.out.println(Results.get(h-1));
		}			
		return Results;
    }	
	
	public static ArrayList<Integer> runtimeOut(String aname, int rounds, boolean modefied, boolean smooth, double smoothSlope, double EAinREA, int kappa, 
			String type, int gamma, int dynamicBudjet, int individLen, int evSize, Fitness fitness, Mutation mutation,  boolean recentered) {

		ArrayList<Integer> Results = new ArrayList();
		
		for (int i = 0; i < evSize; ++i) {
			Results.add(0);
		}	
		for (int i = 0; i < rounds; ++i) {
			ArrayList<Integer> fit1 = new ArrayList();
			RuntimeAlgo algo = new RuntimeAlgo();
			
			fit1 = algo.run(modefied, aname, smooth, smoothSlope, EAinREA, kappa, type, gamma, dynamicBudjet, individLen, evSize, fitness, mutation, recentered);
			for (int h =0; h < evSize; ++h) {
				int sum = Results.get(h) + fit1.get(h);
				Results.set(h, sum);				
			}
		}
		return Results;
    }	
	
	public static List<ArrayList<Integer>> gradient(BitString oldOpt, int bestValue, BitString bestFirst, int rounds, int gamma, int individLen, int evSize, Fitness fitness, Mutation mutation) {
		String type = "";
		if (gamma == 1) {
			type = "Single";			
		}
		else if (gamma == 5) {
			type = "Five";			
		}
		else if (gamma == 3) {
			type = "Three";			
		}
		else if (gamma == 10) {
			type = "Ten";			
		}
		List<ArrayList<Integer>> Results = new ArrayList();		
		
        for(int i = 0; i < 2; i++){
            ArrayList <Integer> arr1=new ArrayList<>();
            for(int j = 0; j < evSize; ++j){                
                arr1.add(0);
            }
            Results.add(arr1);
        }		
		Fitness save = new LeadingOnes(oldOpt, individLen);
		
		for (int i = 0; i < rounds; ++i) {
			//System.out.println("bef " + oldOpt);
			BitString optCopy = oldOpt.clone();
			fitness = new LeadingOnes(optCopy, individLen);
			//System.out.println("bef " + fitness.getOptimum());
			fitness.optimumChange(type);
			//System.out.println("aft " + oldOpt);
			//System.out.println("aft " + fitness.getOptimum());
			//System.out.println("2 " + opt.toString());
			ArrayList<Integer> fit1 = new ArrayList();
			SimpleSolver algo = new SimpleSolver();
			
			fit1 = algo.run(bestValue, bestFirst, "EA", gamma, individLen, evSize, fitness, mutation);
			//System.out.println("3 " + opt.toString());
			for (int h =0; h < evSize; ++h) {
				int sum = Results.get(0).get(h) + fit1.get(h);
				//System.out.println("EA " + sum);
				Results.get(0).set(h, sum);				
			}
			fit1 = new ArrayList();
			fit1 = algo.run(bestValue, bestFirst, "REA", gamma, individLen, evSize, fitness, mutation);
			for (int h =0; h < evSize; ++h) {
				int sum = Results.get(1).get(h) + fit1.get(h);
				//System.out.println("REA " + sum);
				Results.get(1).set(h, sum);				
			}
		}
		return Results;
    }	
	
	public static void main(boolean indgrad, boolean gradient, boolean comparation, boolean cumulative, boolean runtime, boolean histo, double EAinREA, boolean modefied, boolean smooth, double[] smoothSlope, 
			String problem, String[] mut, int evSize, int rounds, int individLen, String type, 
			int[] kappa, int gamma, int dynamicBudjet, Boolean recentered) throws FileNotFoundException {
		
		Fitness fitness;		
		Mutation[] mutation = new Mutation[mut.length];
		
		for (int i = 0; i < mut.length; ++i) {
			switch (mut[i]) {
				case "EA":
					mutation[i] = new MutEA();
					break;
				case "RLS":
					mutation[i] = new MutRLS();
				    break;
			default:
					throw new IllegalArgumentException("Invalid mutation name: " + mut[i]);	
			}
		}
		Random random = new Random();
		switch (problem) {
			case "OneMax":
				BitString optOM = Init.run(individLen, random);
				fitness = new OneMax(optOM, individLen);
			    break;

			case "LeadingOnes":
				BitString optLO = Init.run(individLen, random);
				fitness = new LeadingOnes(optLO, individLen);			
			    break;
			default:
				throw new IllegalArgumentException("Invalid problem name: " + problem);	
		}
		String experimentRoot = "results/data";
		if (gradient) {
			experimentRoot += "/gradient";
		}
		if (comparation) {
			experimentRoot += "/comparation";
		}
		if (runtime) {
			experimentRoot += "/runtime";
		}
		if (histo) {
			experimentRoot += "/histo";
		}		
		if (cumulative) {
			experimentRoot += "/cumulative";
		}
		if (smooth) {
			experimentRoot += "/smooth";
		}
		
		String name = "";
		if (type.equals("Single")) {
			name += "Single";
			gamma = 1;
		}
		else if (type.equals("Five")) {
			name += "Five";
			gamma = 5;
		}
		else if (type.equals("Three")) {
			name += "Three";
			gamma = 3;
		}
		else if (type.equals("Ten")) {
			name += "Ten";
			gamma = 10;
		}
		if (!gradient) {
			name += String.valueOf(dynamicBudjet) + String.valueOf(gamma);
		}
		else {
			name += String.valueOf(individLen) + String.valueOf(gamma);
		}
		
		if (smooth) {
			name += smoothSlope[0];
		}
		//System.out.println(experimentRoot);
		PrintWriter xxx = new PrintWriter(new FileOutputStream(experimentRoot + "/" + name + ".csv"));
		if (!gradient) {
			xxx.println("step,algorithm,value");
		}
		else {
			if (indgrad) {
				xxx.println("gamma \t dimension \t algorithm \t run \t step \t value");
			}
			else {
		
			xxx.println("gamma \t dimension \t step \t algorithm \t value");
			}
		}
		
		if (gradient) {
			if (indgrad) {
				BitString opt = fitness.getOptimum().clone();
				random = new Random();
				BitString oldOpt = fitness.getOptimum().clone();
				BitString bestFirst = opt;
				int bestValue = fitness.fitness(bestFirst);
				
				for (int i = 0; i < rounds; ++i) {
					BitString optCopy = oldOpt.clone();
					fitness = new LeadingOnes(optCopy, individLen);
					fitness.optimumChange(type);
					ArrayList<Integer> fit1 = new ArrayList();
					SimpleSolver algo = new SimpleSolver();					
					fit1 = algo.run(bestValue, bestFirst, "EA", gamma, individLen, evSize, fitness, mutation[0]);
					int prev = fit1.get(0);
					xxx.printf(Locale.US, "%d \t %d \t EA \t %d \t 1 \t %d\n", gamma, individLen, i+1, fit1.get(0));
					for (int h =1; h < evSize; ++h) {
						if (prev != fit1.get(h)) {
							xxx.printf(Locale.US, "%d \t %d \t EA \t %d \t %d \t %d\n", gamma, individLen, i+1, h+1, fit1.get(h));	
							prev = fit1.get(h);
						}
						//xxx.printf(Locale.US, "%d \t %d \t EA \t %d \t %d \t %d\n", gamma, individLen, i, h+1, fit1.get(h));			
					}
					fit1 = new ArrayList();
					fit1 = algo.run(bestValue, bestFirst, "REA", gamma, individLen, evSize, fitness, mutation[0]);
					
					prev = fit1.get(0);
					xxx.printf(Locale.US, "%d \t %d \t REA \t %d \t 1 \t %d\n", gamma, individLen, i+1, fit1.get(0));
					for (int h =1; h < evSize; ++h) {
						if (prev != fit1.get(h)) {
							xxx.printf(Locale.US, "%d \t %d \t REA \t %d \t %d \t %d\n", gamma, individLen, i+1, h+1, fit1.get(h));	
							prev = fit1.get(h);
						}
						//xxx.printf(Locale.US, "%d \t %d \t EA \t %d \t %d \t %d\n", gamma, individLen, i, h+1, fit1.get(h));			
					}
					//for (int h =0; h < evSize; ++h) {
					//	xxx.printf(Locale.US, "%d \t %d \t REA \t %d \t %d \t %d\n", gamma, individLen, i, h+1, fit1.get(h));		
					//}
				}
				
			}
			else {
				//оптимум известен
				BitString opt = fitness.getOptimum().clone();
				
				//оптимум не известен, а ищется
				int[] dynamicBudget = {3000};//500, 1000, 2000, 3000, 4000, 5000, 6000, 7000};
				for (int i =0; i < dynamicBudget.length; ++i) {
					random = new Random();
					
					//EAsolver algo = new EAsolver();		
					BitString oldOpt = fitness.getOptimum().clone();
					//BitString bestFirst = algo.run(gamma, individLen, dynamicBudget[i], fitness, mutation[0]);
					BitString bestFirst = opt;
					int bestValue = fitness.fitness(bestFirst);
					//System.out.println(bestValue);
					//System.out.println("1 "+ bestFirst.toString());
									
					List<ArrayList<Integer>> gradients = gradient(oldOpt, bestValue, bestFirst, rounds, gamma, individLen, evSize, fitness, mutation[0]);
					
					double prevea = (double)gradients.get(0).get(0)/rounds;
					double prevrea = (double)gradients.get(1).get(0)/rounds;
					xxx.printf(Locale.US,"%d \t %d \t 1 \t EA \t %.2f\n", gamma, individLen, prevea);
					xxx.printf(Locale.US,"%d \t %d \t 1 \t REA \t %.2f\n", gamma, individLen, prevrea);
					int h = 1;
					double diff = 0;
					double sumdiff = 0;
					ArrayList<Double> sumdif = new ArrayList();
					while ((h < evSize)){// && (diff >= -0.5)) {
						//System.out.println(h + " " + eaResults.get(h)/rounds);
						double avEA = (double)gradients.get(0).get(h)/rounds;
						double avREA = (double)gradients.get(1).get(h)/rounds;
						double diffEA = avEA-prevea;
						//System.out.println("diffEA" + diffEA);
						double diffREA = avREA-prevrea;
						//System.out.println("diffREA" + diffREA);
						
						xxx.printf(Locale.US, "%d \t %d \t %d \t EA \t %.2f\n", gamma, individLen, h+1, avEA);
	
						System.out.println(h);
						prevea = avEA;
						prevrea = avREA;
						diff = diffREA - diffEA;
						sumdiff += diff;
						sumdif.add(sumdiff);
						xxx.printf(Locale.US,"%d \t %d \t %d \t REA \t %.2f\n", gamma, individLen, h+1, avREA);
						h++;
	
					}	
					//for (int l=0; l < sumdif.size(); ++l) {
					//	if (sumdif.get(l) > -0.5) {
					//		xxx.printf("%d \t %.2f\n", l, sumdif.get(l));
					//	}
					//}
				}
			}
			
			
			
		}
		
		if (runtime) {
			if (comparation) {
				//EA info			
				String aname = "EA";
				ArrayList<Integer> eaResults = runtimeOut(aname, rounds, false, false, 0.0, EAinREA, 0, 
						type, gamma, dynamicBudjet, individLen, evSize, fitness, mutation[0], recentered);			
				
				double prev = (double)eaResults.get(0)/rounds;		
				xxx.printf(Locale.US,"1,EA,%.2f\n",prev);
				for (int h = 1; h < evSize; ++h) {
					
					double avEA = (double)eaResults.get(h)/rounds;
					if (prev != avEA) {
						xxx.printf(Locale.US,h+1 +",EA,%.2f\n",avEA);
						prev = avEA;
					}
				}
				
				//вывод REA
				
				/*aname = "REAwors";			
				ArrayList<Integer> reaResults = runtimeOut(aname, rounds, false, false, 0.0, 0.5, kappa[0], 
						type, gamma, dynamicBudjet, individLen, evSize, fitness, mutation[0], recentered);
				prev = (double)reaResults.get(0)/rounds;		
				xxx.printf(Locale.US,"1,origREAwors,%.2f\n",prev);
				for (int h = 1; h < evSize; ++h) {
					
					double avREA = (double)reaResults.get(h)/rounds;
					if (prev != avREA) {
						xxx.printf(Locale.US,h+1 +",origREAwors,%.2f\n",avREA);
						prev = avREA;
					}
				}*/
				aname = "REA";			
				ArrayList<Integer> reaResults2 = runtimeOut(aname, rounds, false, false, 0.0, 0.5, kappa[0], 
						type, gamma, dynamicBudjet, individLen, evSize, fitness, mutation[0], recentered);
				prev = (double)reaResults2.get(0)/rounds;		
				xxx.printf(Locale.US,"1,origREA,%.2f\n",prev);
				for (int h = 1; h < evSize; ++h) {
					
					double avREA = (double)reaResults2.get(h)/rounds;
					if (prev != avREA) {
						xxx.printf(Locale.US,h+1 +",origREA,%.2f\n",avREA);
						prev = avREA;
					}
				}
				
				//вывод модификаций REA	
				String[] reatype = {"REAStar"};//, "REAwor", "REAwos"};
				for (int g = 0; g < reatype.length; ++g) {
					aname = reatype[g];
					int d = smoothSlope.length;				
					for (int r = 1; r < d+1; ++r) {	
					
						ArrayList<Integer> modREAResults = runtimeOut(aname, rounds, modefied, smooth, smoothSlope[Math.max(0, r-1)], EAinREA, kappa[0], 
								type, gamma, dynamicBudjet, individLen, evSize, fitness, mutation[0], recentered);
						
						String outname = "";
						
						outname = "mod" + EAinREA + "sw"+ aname + smoothSlope[Math.max(0, r-1)];
					
						prev = (double)modREAResults.get(0)/rounds;		
						xxx.printf(Locale.US,"1,"+ outname+",%.2f\n",prev);
						for (int h = 1; h < evSize; ++h) {
							
							double avmodREA = (double)modREAResults.get(h)/rounds;
							if (prev != avmodREA) {
								xxx.printf(Locale.US,h+1 +","+ outname+",%.2f\n",avmodREA);
								prev = avmodREA;
							}
						}												
					}
				}			
			}
			else {
				
				//EA info			
				String aname = "EA";
				ArrayList<Integer> eaResults = runtimeOut(aname, rounds, false, false, 0.0, EAinREA, 0, 
						type, gamma, dynamicBudjet, individLen, evSize, fitness, mutation[0], recentered);			
				
				double prev = (double)eaResults.get(0)/rounds;		
				xxx.printf(Locale.US,"1,EA,%.2f\n",prev);
				for (int h = 1; h < evSize; ++h) {
					
					double avEA = (double)eaResults.get(h)/rounds;
					if (prev != avEA) {
						xxx.printf(Locale.US,h+1 +",EA,%.2f\n",avEA);
						prev = avEA;
					}
				}
				
				//вывод REA
				
				aname = "REA";			
				ArrayList<Integer> reaResults = runtimeOut(aname, rounds, false, false, 0.0, 0.5, kappa[0], 
						type, gamma, dynamicBudjet, individLen, evSize, fitness, mutation[0], recentered);
				prev = (double)reaResults.get(0)/rounds;		
				xxx.printf(Locale.US,"1,REA,%.2f\n",prev);
				for (int h = 1; h < evSize; ++h) {
					
					double avREA = (double)reaResults.get(h)/rounds;
					if (prev != avREA) {
						xxx.printf(Locale.US,h+1 +",REA,%.2f\n",avREA);
						prev = avREA;
					}
				}
				
				//вывод модификаций REA			
				for (int j = 0; j < kappa.length; ++j) {
					int d = 1;					
					d += smoothSlope.length;				
					for (int r = 0; r < d; ++r) {					
						if(r == 0) {
							smooth = false;
						}
						else {
							smooth = true;
						}					
						ArrayList<Integer> modREAResults = runtimeOut(aname, rounds, modefied, smooth, smoothSlope[Math.max(0, r-1)], EAinREA, kappa[j], 
								type, gamma, dynamicBudjet, individLen, evSize, fitness, mutation[0], recentered);
						
						String outname = "";
						if (smooth) {
							outname = "mod" + EAinREA + "swREA" + smoothSlope[Math.max(0, r-1)];
						}
						else {
							outname =  "mod" + EAinREA + "REA";
						}
						prev = (double)modREAResults.get(0)/rounds;		
						xxx.printf(Locale.US,"1,"+ outname+",%.2f\n",prev);
						for (int h = 1; h < evSize; ++h) {
							
							double avmodREA = (double)modREAResults.get(h)/rounds;
							if (prev != avmodREA) {
								xxx.printf(Locale.US,h+1 +","+ outname+",%.2f\n",avmodREA);
								prev = avmodREA;
							}
						}		
					}
				}
			}
			

		}
		else if (histo) {	
			if (comparation) {
				String aname = "EA";
				ArrayList<Integer> eaResults = histoOut(aname, rounds, false, false, 0.0, EAinREA, 0, 
						type, gamma, dynamicBudjet, individLen, evSize, fitness, mutation[0], recentered);	
				
				for (int h = 0; h < individLen+1; ++h) {
					double percent = (double)100*eaResults.get(h)/(rounds*(Math.floor(evSize/dynamicBudjet)));
					xxx.printf(Locale.US,h +",EA"+",%.2f\n",percent);
					//xxx.println(h +",EA,"+percent);
				}
				//вывод REA				
				aname = "REA";			
				ArrayList<Integer> reaResults = histoOut(aname, rounds, false, false, 0.0, 0.5, kappa[0], 
						type, gamma, dynamicBudjet, individLen, evSize, fitness, mutation[0], recentered);
				
				for (int h = 0; h < individLen+1; ++h) {
					double percent = (double)100*reaResults.get(h)/(rounds*(Math.floor(evSize/dynamicBudjet)));
					xxx.printf(Locale.US,h +",origREA"+",%.2f\n",percent);
				}
				
				//вывод модификаций REA	
				String[] reatype = {"REA"};//, "REAwor", "REAwos"};
				for (int g = 0; g < reatype.length; ++g) {
					aname = reatype[g];
					for (int j = 0; j < kappa.length; ++j) {									
						int d = smoothSlope.length;				
						for (int r = 1; r < d+1; ++r) {										
							ArrayList<Integer> modREAResults = histoOut(aname, rounds, modefied, smooth, smoothSlope[Math.max(0, r-1)], EAinREA, kappa[j], 
									type, gamma, dynamicBudjet, individLen, evSize, fitness, mutation[0], recentered);						
							String outname = "";										
							outname = "mod" + EAinREA + "sw"+ aname + smoothSlope[Math.max(0, r-1)];						
							for (int h = 0; h < individLen+1; ++h) {
								double percent = (double)100*modREAResults.get(h)/(rounds*(Math.floor(evSize/dynamicBudjet)));							
								xxx.printf(Locale.US,h +"," + outname+ ","+"%.2f\n",percent);
							}				
						}
					}
				}
			}
			else {
			//вывод EA			
			String aname = "EA";
			ArrayList<Integer> eaResults = histoOut(aname, rounds, false, false, 0.0, EAinREA, 0, 
					type, gamma, dynamicBudjet, individLen, evSize, fitness, mutation[0], recentered);	
			
			for (int h = 0; h < individLen+1; ++h) {
				double percent = (double)100*eaResults.get(h)/(rounds*(Math.floor(evSize/dynamicBudjet)));
				xxx.printf(Locale.US,h +",EA"+",%.2f\n",percent);
			}
			//вывод REA
			
			aname = "REA";			
			ArrayList<Integer> reaResults = histoOut(aname, rounds, false, false, 0.0, 0.5, kappa[0], 
					type, gamma, dynamicBudjet, individLen, evSize, fitness, mutation[0], recentered);
			
			for (int h = 0; h < individLen+1; ++h) {
				double percent = (double)100*reaResults.get(h)/(rounds*(Math.floor(evSize/dynamicBudjet)));
				xxx.printf(Locale.US,h +",REA"+",%.2f\n",percent);
			}
			//вывод модификаций REA			
			for (int j = 0; j < kappa.length; ++j) {
				//int d = 1;					
				int d = smoothSlope.length;				
				for (int r = 1; r < d+1; ++r) {					
								
					ArrayList<Integer> modREAResults = histoOut(aname, rounds, modefied, smooth, smoothSlope[Math.max(0, r-1)], EAinREA, kappa[j], 
							type, gamma, dynamicBudjet, individLen, evSize, fitness, mutation[0], recentered);
					
					String outname = "";
					
						outname = "mod" + EAinREA + "swREA" + smoothSlope[Math.max(0, r-1)];
					
					for (int h = 0; h < individLen+1; ++h) {
						double percent = (double)100*modREAResults.get(h)/(rounds*(Math.floor(evSize/dynamicBudjet)));
						
						xxx.printf(Locale.US,h +"," + outname+ ","+"%.2f\n",percent);
						
						
					}				
				}
			}
			}
		
		}
		else if (cumulative) {	
			if (comparation) {
				String aname = "EA";
				ArrayList<Integer> eaResults = cumulativeOut(aname, rounds, false, false, 0.0, EAinREA, 0, 
						type, gamma, dynamicBudjet, individLen, evSize, fitness, mutation[0], recentered);	
				
				for (int h = 0; h < individLen+1; ++h) {
					double percent = (double)100*eaResults.get(h)/(rounds*(Math.floor(evSize/dynamicBudjet)));
					xxx.printf(Locale.US,h +",EA"+",%.2f\n",percent);
					//xxx.println(h +",EA,"+percent);
				}
							
				aname = "REA";			
				ArrayList<Integer> reaResults = cumulativeOut(aname, rounds, false, false, 0.0, 0.5, kappa[0], 
						type, gamma, dynamicBudjet, individLen, evSize, fitness, mutation[0], recentered);
				
				for (int h = 0; h < individLen+1; ++h) {
					double percent = (double)100*reaResults.get(h)/(rounds*(Math.floor(evSize/dynamicBudjet)));
					xxx.printf(Locale.US,h +",origREA"+",%.2f\n",percent);
				}
				
				
				String[] reatype = {"REA"};//, "REAwor", "REAwos"};
				for (int g = 0; g < reatype.length; ++g) {
					aname = reatype[g];
					for (int j = 0; j < kappa.length; ++j) {									
						int d = smoothSlope.length;				
						for (int r = 1; r < d+1; ++r) {										
							ArrayList<Integer> modREAResults = cumulativeOut(aname, rounds, modefied, smooth, smoothSlope[Math.max(0, r-1)], EAinREA, kappa[j], 
									type, gamma, dynamicBudjet, individLen, evSize, fitness, mutation[0], recentered);						
							String outname = "";										
							outname = "mod" + EAinREA + "sw"+ aname + smoothSlope[Math.max(0, r-1)];						
							for (int h = 0; h < individLen+1; ++h) {
								double percent = (double)100*modREAResults.get(h)/(rounds*(Math.floor(evSize/dynamicBudjet)));							
								xxx.printf(Locale.US,h +"," + outname+ ","+"%.2f\n",percent);
							}				
						}
					}
				}
			}
					
			else {	
			
				//out EA			
				String aname = "EA";
				ArrayList<Integer> eaResults = cumulativeOut(aname, rounds, false, false, 0.0, EAinREA, 0, 
						type, gamma, dynamicBudjet, individLen, evSize, fitness, mutation[0], recentered);	
				
				for (int h = 0; h < individLen+1; ++h) {
					double percent = (double)100*eaResults.get(h)/(rounds*(Math.floor(evSize/dynamicBudjet)));
					xxx.printf(Locale.US,h +",EA"+",%.2f\n",percent);
					//xxx.println(h +",EA,"+percent);
				}
				//out REA
				
				aname = "REA";			
				ArrayList<Integer> reaResults = cumulativeOut(aname, rounds, false, false, 0.0, 0.5, kappa[0], 
						type, gamma, dynamicBudjet, individLen, evSize, fitness, mutation[0], recentered);
				
				for (int h = 0; h < individLen+1; ++h) {
					double percent = (double)100*reaResults.get(h)/(rounds*(Math.floor(evSize/dynamicBudjet)));
					xxx.printf(Locale.US,h +",REA"+",%.2f\n",percent);
				}
				//modification REA			
				for (int j = 0; j < kappa.length; ++j) {
									
					int d = smoothSlope.length;				
					for (int r = 1; r < d+1; ++r) {					
								
						ArrayList<Integer> modREAResults = cumulativeOut(aname, rounds, modefied, smooth, smoothSlope[Math.max(0, r-1)], EAinREA, kappa[j], 
								type, gamma, dynamicBudjet, individLen, evSize, fitness, mutation[0], recentered);
						
						String outname = "";
						
							outname = "mod" + "swREA" + smoothSlope[Math.max(0, r-1)];
						
						for (int h = 0; h < individLen+1; ++h) {
							double percent = (double)100*modREAResults.get(h)/(rounds*(Math.floor(evSize/dynamicBudjet)));
							
							xxx.printf(Locale.US,h +"," + outname+ ","+"%.2f\n",percent);
						}				
					}
				}
			}
		
		
		}
		
		
	xxx.close();			
	}

}
