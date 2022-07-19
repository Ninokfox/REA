package algo;

import java.util.ArrayList;
import java.util.Random;

import fitnessFunc.Fitness;
import mutation.Mutation;

public class AnalysisOfRecAndSwithing {
	public String getName() {
		return "RuntimeAlgo";
	}

	public ArrayList<Integer> run(boolean modefied, String name, boolean smooth, double smoothSlope, double EAinREA, int kappa, 
			String type, int gamma, int dynamicBudjet, int individLen, int evSize,	Fitness fitness, Mutation mutation,  boolean recentered) {
		Random random = new Random();
		int bujetCount = 0;
		BitString ind = Init.run(individLen, random);
		ArrayList<Integer> ff = new ArrayList<Integer>();
		ff.add(fitness.fitness(ind));
		if (name.equals("EA")) {
			for (int t = 1; t < evSize; t++){
				bujetCount++;
				if (bujetCount == dynamicBudjet) {
					bujetCount = 0;
					fitness.optimumChange(type);
				}
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
		else if (name.equals("REA")) {
			boolean reaIsWorking = false;			
			ArrayList<BitString> x = new ArrayList<BitString>(); // array of individuals on hamming distance
			ArrayList<Boolean> defined = new ArrayList<Boolean>();			
			int definedSize = 0;
			BitString parent = ind;
			BitString xStar = ind;		
			BitString xOld = ind;
			Integer bestValue = fitness.fitness(xOld);
				
			for (int t = 1; t < evSize; t++) {
				bujetCount++;
				if (bujetCount == dynamicBudjet) {
					bujetCount = 0;											
					xOld = xStar;
					bestValue = fitness.fitness(xOld);							
					fitness.optimumChange(type);						
					
					reaIsWorking = true;
					if (definedSize == 0) {
						x = new ArrayList<BitString>();
						defined = new ArrayList<Boolean>();
						definedSize = 0;
						for (int i = 0; i < gamma + 1; i++) {
							x.add(Init.run(individLen, random));
							defined.add(false);
						}
					
						x.set(0, xOld);
						defined.set(0, true);
						definedSize++;
					}
					else {
						//recentering
						ArrayList<BitString> y = new ArrayList<BitString>();
						ArrayList<Boolean> definedy = new ArrayList<Boolean>();
						int definedSizey = 0;
						
						for (int i = 0; i < gamma + 1; i++) {
							y.add(Init.run(individLen, random));
							definedy.add(false);
						}
					
						y.set(0, xOld);
						definedy.set(0, true);
						definedSizey++;	
						t++; // for xOld value evaluation on new ff
						ff.add(ff.get(ff.size() - 1)); //best previous individual
						bujetCount++;	
						for (int i = 0; i < gamma + 1; i++) {								
							if (defined.get(i)) {
								if (!x.get(i).equals(xOld)) {
									t++; // for xOld value evaluation on new ff
									ff.add(ff.get(ff.size() - 1)); //best previous individual
									bujetCount++;
									if (fitness.fitness(x.get(i)) >= fitness.fitness(y.get(0))) {
										int j = Math.min(x.get(i).hammingDist(xOld), gamma);
										if (!definedy.get(j)) {
											y.set(j, x.get(i));	
											definedy.set(j, true);
											definedSizey++;
											// не определено - писать 100%, определено - писать 50%
										}
										else if (random.nextInt(2) == 0) {
											y.set(j, x.get(i));
										}									
									}
								}
							}
						}							
						x = y;
						defined = definedy;
						definedSize = definedSizey;							
					}	
				}
				if (reaIsWorking) {
					if (definedSize > 1) {
						if (!smooth) {
							if(EAinREA != 0.0) {
								System.out.println("Yaaaaay!");
								int percent = (int)(1/EAinREA);
								if (random.nextInt(percent) == 0) {									
									parent = xStar;									
								} else {
									while (true) {
										int index = random.nextInt(x.size());
										BitString pp = x.get(index);
										if (pp.equals(xStar) || (!defined.get(index))) {
											continue;
										} else {
											
											parent = pp;
										}
									}
								}							
							}
							else {
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
							}
						}else {
							double newSlope = individLen*individLen*smoothSlope;
							if(EAinREA != 0.0) {
								System.out.println("Yaaaaay!2");
								int percent = (int)(1/EAinREA);
								if (random.nextInt(percent) != 0) {		
																	
									if (random.nextInt((int)newSlope) < bujetCount) {									
										parent = xStar;									
									}
									else {
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
									}
								}
								else {								
									parent = xStar;									
								}							
							}
							else {	
								System.out.println("Ququ");
								if (random.nextInt((int)newSlope) < bujetCount) {									
									parent = xStar;									
								}
								else {
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
								}
							}						
							if (bujetCount >= newSlope) {
								reaIsWorking = false;
								
								definedSize = 0;
							}						
						}
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
						//System.out.println("REA stops");
						reaIsWorking = false;
						
						definedSize = 0;
					}
				}
			}
		}
		else if (name.equals("REAwor")) {
			boolean reaIsWorking = false;			
			ArrayList<BitString> x = new ArrayList<BitString>(); // array of individuals on hamming distance
			ArrayList<Boolean> defined = new ArrayList<Boolean>();			
			int definedSize = 0;
			BitString parent = ind;
			BitString xStar = ind;		
			BitString xOld = ind;
			Integer bestValue = fitness.fitness(xOld);
			for (int t = 1; t < evSize; t++) {
				bujetCount++;
				if (bujetCount == dynamicBudjet) {
					bujetCount = 0;												
					xOld = xStar;
					bestValue = fitness.fitness(xOld);							
					fitness.optimumChange(type);						
					
					reaIsWorking = true;						
					x = new ArrayList<BitString>();
					defined = new ArrayList<Boolean>();
					definedSize = 0;
					for (int i = 0; i < gamma + 1; i++) {
						x.add(Init.run(individLen, random));
						defined.add(false);
					}					
					x.set(0, xOld);
					defined.set(0, true);
					definedSize++;
				}
				if (reaIsWorking) {
					if (definedSize > 1) {
						if (!smooth) {
							if(EAinREA != 0.0) {
								int percent = (int)(1/EAinREA);
								if (random.nextInt(percent) == 0) {									
									parent = xStar;									
								} else {
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
								}							
							}
							else {
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
							}
						}else {
							double newSlope = individLen*individLen*smoothSlope;
							if(EAinREA != 0.0) {
								int percent = (int)(1/EAinREA);
								if (random.nextInt(percent) != 0) {		
																	
									if (random.nextInt((int)newSlope) < bujetCount) {									
										parent = xStar;									
									}
									else {
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
									}
								}
								else {								
									parent = xStar;									
								}							
							}
							else {															
								if (random.nextInt((int)newSlope) < bujetCount) {									
									parent = xStar;									
								}
								else {
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
								}
							}						
							if (bujetCount >= newSlope) {
								reaIsWorking = false;
								
								definedSize = 0;
							}						
						}
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
						//System.out.println("REA stops");
						reaIsWorking = false;
						
						definedSize = 0;
					}
				}
			}
		}
		if (name.equals("REAwos")) {	
			boolean reaIsWorking = false;
			ArrayList<BitString> x = new ArrayList<BitString>(); // array of individuals on hamming distance
			ArrayList<Boolean> defined = new ArrayList<Boolean>();			
			int definedSize = 0;
			BitString parent = ind;
			BitString xStar = ind;		
			BitString xOld = ind;								
			for (int t = 1; t < evSize; t++) {
				bujetCount++;
				if (bujetCount == dynamicBudjet) {
					reaIsWorking = true;
					bujetCount = 0;												
					xOld = xStar;											
					fitness.optimumChange(type);						
					if (definedSize == 0) {
						x = new ArrayList<BitString>();
						defined = new ArrayList<Boolean>();
						definedSize = 0;
						for (int i = 0; i < gamma + 1; i++) {
							x.add(Init.run(individLen, random));
							defined.add(false);
						}					
						x.set(0, xOld);
						defined.set(0, true);
						definedSize++;
					}
					else {
						//recentering
						ArrayList<BitString> y = new ArrayList<BitString>();
						ArrayList<Boolean> definedy = new ArrayList<Boolean>();
						int definedSizey = 0;
						
						for (int i = 0; i < gamma + 1; i++) {
							y.add(Init.run(individLen, random));
							definedy.add(false);
						}
					
						y.set(0, xOld);
						definedy.set(0, true);
						definedSizey++;	
						t++; // for xOld value evaluation on new ff
						ff.add(ff.get(ff.size() - 1)); //best previous individual
						bujetCount++;	
						for (int i = 0; i < gamma + 1; i++) {								
							if (defined.get(i)) {
								if (!x.get(i).equals(xOld)) {
									t++; // for xOld value evaluation on new ff
									ff.add(ff.get(ff.size() - 1)); //best previous individual
									bujetCount++;
									if (fitness.fitness(x.get(i)) >= fitness.fitness(y.get(0))) {
										int j = Math.min(x.get(i).hammingDist(xOld), gamma);
										if (!definedy.get(j)) {
											y.set(j, x.get(i));	
											definedy.set(j, true);
											definedSizey++;
											// не определено - писать 100%, определено - писать 50%
										}
										else if (random.nextInt(2) == 0) {
											y.set(j, x.get(i));
										}									
									}
								}
							}
						}							
						x = y;
						defined = definedy;
						definedSize = definedSizey;							
					}	
				}
				if (reaIsWorking) {
					if (definedSize > 1) {
						if (!smooth) {
							if(EAinREA != 0.0) {
								int percent = (int)(1/EAinREA);
								if (random.nextInt(percent) == 0) {									
									parent = xStar;									
								} else {
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
								}							
							}
							else {
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
							}
						}else {
							double newSlope = individLen*individLen*smoothSlope;
							if(EAinREA != 0.0) {
								int percent = (int)(1/EAinREA);
								if (random.nextInt(percent) != 0) {		
																	
									if (random.nextInt((int)newSlope) < bujetCount) {									
										parent = xStar;									
									}
									else {
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
									}
								}
								else {								
									parent = xStar;									
								}							
							}
							else {															
								if (random.nextInt((int)newSlope) < bujetCount) {									
									parent = xStar;									
								}
								else {
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
								}
							}							
						}
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
				}
			}
		}
		if (name.equals("REAwors")) {
			boolean reaIsWorking = false;
			ArrayList<BitString> x = new ArrayList<BitString>(); // array of individuals on hamming distance
			ArrayList<Boolean> defined = new ArrayList<Boolean>();			
			int definedSize = 0;
			BitString parent = ind;
			BitString xStar = ind;		
			BitString xOld = ind;
			for (int t = 1; t < evSize; t++) {
				bujetCount++;
				if (bujetCount == dynamicBudjet) {
					reaIsWorking = true;
					bujetCount = 0;											
					xOld = xStar;												
					fitness.optimumChange(type);						
					x = new ArrayList<BitString>();
					defined = new ArrayList<Boolean>();
					definedSize = 0;
					for (int i = 0; i < gamma + 1; i++) {
						x.add(Init.run(individLen, random));
						defined.add(false);
					}
					x.set(0, xOld);
					defined.set(0, true);
					definedSize++;
				}	
				if (reaIsWorking) {
					if (definedSize > 1) {
						if (!smooth) {
							if(EAinREA != 0.0) {
								int percent = (int)(1/EAinREA);
								if (random.nextInt(percent) == 0) {									
									parent = xStar;									
								} else {
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
								}							
							}
							else {
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
							}
						}else {
							double newSlope = individLen*individLen*smoothSlope;
							if(EAinREA != 0.0) {
								int percent = (int)(1/EAinREA);
								if (random.nextInt(percent) != 0) {																
									if (random.nextInt((int)newSlope) < bujetCount) {									
										parent = xStar;									
									}
									else {
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
									}
								}
								else {								
									parent = xStar;									
								}							
							}
							else {															
								if (random.nextInt((int)newSlope) < bujetCount) {									
									parent = xStar;									
								}
								else {
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
								}
							}								
						}
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
				}
			}
		}
		
		//HISTO
		
ArrayList<Integer> answer = new ArrayList<Integer>();
		
		for (int i = 0; i < individLen+1; ++i) {
			answer.add(0);
		}	
		
		//Random random = new Random();
		//int bujetCount = 0;
		//BitString ind = Init.run(individLen, random);
		//ArrayList<Integer> ff = new ArrayList<Integer>();
		ff.add(fitness.fitness(ind));
		
		if (name.equals("EA")) {
			int best = 0;
			for (int t = 0; t < evSize; t++){
				bujetCount++;
				if (bujetCount == dynamicBudjet) {
					bujetCount = 0;
					int number = answer.get(best)+1;
					answer.set(best, number);					
					fitness.optimumChange(type);					
				}
				BitString mutated = mutation.mutate(ind, random, fitness);				
				int lastF = fitness.fitness(ind);
				int currF = fitness.fitness(mutated);
				if (currF >= lastF) {
					ind = mutated;					
					ff.add(currF);
					best = currF;
					
				} else {
					ff.add(lastF);
					best = lastF;
				}					
			}			
		}
		else if (name.equals("REA")) {
			boolean reaIsWorking = false;		
			ArrayList<BitString> x = new ArrayList<BitString>(); // array of individuals on hamming distance
			ArrayList<Boolean> defined = new ArrayList<Boolean>();			
			int definedSize = 0;
			BitString parent = ind;
			BitString xStar = ind;		
			BitString xOld = ind;
			Integer bestValue = fitness.fitness(xOld);			
			for (int t = 0; t < evSize; t++) {
				bujetCount++;
				if (bujetCount == dynamicBudjet) {
					bujetCount = 0;					
					xOld = xStar;						
					bestValue = fitness.fitness(xOld);	
					int number = answer.get(bestValue)+1;
					answer.set(bestValue, number);										
					fitness.optimumChange(type);
					reaIsWorking = true;
					if (definedSize == 0) {
						x = new ArrayList<BitString>();
						defined = new ArrayList<Boolean>();
						definedSize = 0;
						for (int i = 0; i < gamma + 1; i++) {
							x.add(Init.run(individLen, random));
							defined.add(false);
						}						
						x.set(0, xOld);
						defined.set(0, true);
						definedSize++;
					}
					else {
						
						//recentering
						ArrayList<BitString> y = new ArrayList<BitString>();
						ArrayList<Boolean> definedy = new ArrayList<Boolean>();
						int definedSizey = 0;								
						for (int i = 0; i < gamma + 1; i++) {
							y.add(Init.run(individLen, random));
							definedy.add(false);
						}							
						y.set(0, xOld);
						definedy.set(0, true);
						definedSizey++;									
						t++; // for xOld value evaluation on new ff
						ff.add(ff.get(ff.size() - 1)); //best previous individual								
						bujetCount++;								
						for (int i = 0; i < gamma + 1; i++) {									
							if (defined.get(i)) {
								if (!x.get(i).equals(xOld)) {											
									t++; // for xOld value evaluation on new ff
									ff.add(ff.get(ff.size() - 1)); //best previous individual											
									bujetCount++;
									if (fitness.fitness(x.get(i)) >= fitness.fitness(y.get(0))) {
										int j = Math.min(x.get(i).hammingDist(xOld), gamma);
										if (!definedy.get(j)) {
											y.set(j, x.get(i));	
											definedy.set(j, true);
											definedSizey++;
											// не определено - писать 100%, определено - писать 50%
										}
										else if (random.nextInt(2) == 0) {
											y.set(j, x.get(i));
										}									
									}
								}
							}
						}							
						x = y;
						defined = definedy;
						definedSize = definedSizey;
											
					}
				}
				if (reaIsWorking) {
					if (definedSize > 1) {
						if (!smooth) {
							if(EAinREA != 0.0) {
								int percent = (int)(1/EAinREA);
								if (random.nextInt(percent) == 0) {									
									parent = xStar;									
								} else {
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
								}							
							}
							else {
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
							}
						}else {
							double newSlope = individLen*individLen*smoothSlope;
							if(EAinREA != 0.0) {
								int percent = (int)(1/EAinREA);
								if (random.nextInt(percent) != 0) {																			
									if (random.nextInt((int)newSlope) < bujetCount) {									
										parent = xStar;									
									}
									else {
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
									}
								}
								else {								
									parent = xStar;									
								}							
							}
							else {															
								if (random.nextInt((int)newSlope) < bujetCount) {									
									parent = xStar;									
								}
								else {
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
								}
							}						
							if (bujetCount >= newSlope) {
								reaIsWorking = false;								
								definedSize = 0;
							}						
						}
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
		else if (name.equals("REAwor")) {
			boolean reaIsWorking = false;		
			ArrayList<BitString> x = new ArrayList<BitString>(); // array of individuals on hamming distance
			ArrayList<Boolean> defined = new ArrayList<Boolean>();			
			int definedSize = 0;
			BitString parent = ind;
			BitString xStar = ind;		
			BitString xOld = ind;
			Integer bestValue = fitness.fitness(xOld);			
			for (int t = 0; t < evSize; t++) {
				bujetCount++;
				if (bujetCount == dynamicBudjet) {
					bujetCount = 0;					
					xOld = xStar;						
					bestValue = fitness.fitness(xOld);	
					int number = answer.get(bestValue)+1;
					answer.set(bestValue, number);										
					fitness.optimumChange(type);
					reaIsWorking = true;					
					x = new ArrayList<BitString>();
					defined = new ArrayList<Boolean>();
					definedSize = 0;
					for (int i = 0; i < gamma + 1; i++) {
						x.add(Init.run(individLen, random));
						defined.add(false);
					}						
					x.set(0, xOld);
					defined.set(0, true);
					definedSize++;
					
				}
				if (reaIsWorking) {
					if (definedSize > 1) {
						if (!smooth) {
							if(EAinREA != 0.0) {
								int percent = (int)(1/EAinREA);
								if (random.nextInt(percent) == 0) {									
									parent = xStar;									
								} else {
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
								}							
							}
							else {
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
							}
						}else {
							double newSlope = individLen*individLen*smoothSlope;
							if(EAinREA != 0.0) {
								int percent = (int)(1/EAinREA);
								if (random.nextInt(percent) != 0) {																			
									if (random.nextInt((int)newSlope) < bujetCount) {									
										parent = xStar;									
									}
									else {
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
									}
								}
								else {								
									parent = xStar;									
								}							
							}
							else {															
								if (random.nextInt((int)newSlope) < bujetCount) {									
									parent = xStar;									
								}
								else {
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
								}
							}						
							if (bujetCount >= newSlope) {
								reaIsWorking = false;								
								definedSize = 0;
							}						
						}
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
		else if (name.equals("REAwos")) {
			boolean reaIsWorking = false;		
			ArrayList<BitString> x = new ArrayList<BitString>(); // array of individuals on hamming distance
			ArrayList<Boolean> defined = new ArrayList<Boolean>();			
			int definedSize = 0;
			BitString parent = ind;
			BitString xStar = ind;		
			BitString xOld = ind;
			Integer bestValue = fitness.fitness(xOld);			
			for (int t = 0; t < evSize; t++) {
				bujetCount++;
				if (bujetCount == dynamicBudjet) {
					bujetCount = 0;					
					xOld = xStar;						
					bestValue = fitness.fitness(xOld);	
					int number = answer.get(bestValue)+1;
					answer.set(bestValue, number);										
					fitness.optimumChange(type);
					reaIsWorking = true;
					if (definedSize == 0) {
						x = new ArrayList<BitString>();
						defined = new ArrayList<Boolean>();
						definedSize = 0;
						for (int i = 0; i < gamma + 1; i++) {
							x.add(Init.run(individLen, random));
							defined.add(false);
						}						
						x.set(0, xOld);
						defined.set(0, true);
						definedSize++;
					}
					else {
						
						//recentering
						ArrayList<BitString> y = new ArrayList<BitString>();
						ArrayList<Boolean> definedy = new ArrayList<Boolean>();
						int definedSizey = 0;								
						for (int i = 0; i < gamma + 1; i++) {
							y.add(Init.run(individLen, random));
							definedy.add(false);
						}							
						y.set(0, xOld);
						definedy.set(0, true);
						definedSizey++;									
						t++; // for xOld value evaluation on new ff
						ff.add(ff.get(ff.size() - 1)); //best previous individual								
						bujetCount++;								
						for (int i = 0; i < gamma + 1; i++) {									
							if (defined.get(i)) {
								if (!x.get(i).equals(xOld)) {											
									t++; // for xOld value evaluation on new ff
									ff.add(ff.get(ff.size() - 1)); //best previous individual											
									bujetCount++;
									if (fitness.fitness(x.get(i)) >= fitness.fitness(y.get(0))) {
										int j = Math.min(x.get(i).hammingDist(xOld), gamma);
										if (!definedy.get(j)) {
											y.set(j, x.get(i));	
											definedy.set(j, true);
											definedSizey++;
											// не определено - писать 100%, определено - писать 50%
										}
										else if (random.nextInt(2) == 0) {
											y.set(j, x.get(i));
										}									
									}
								}
							}
						}							
						x = y;
						defined = definedy;
						definedSize = definedSizey;
											
					}
				}
				if (reaIsWorking) {
					if (definedSize > 1) {
						if (!smooth) {
							if(EAinREA != 0.0) {
								int percent = (int)(1/EAinREA);
								if (random.nextInt(percent) == 0) {									
									parent = xStar;									
								} else {
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
								}							
							}
							else {
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
							}
						}else {
							double newSlope = individLen*individLen*smoothSlope;
							if(EAinREA != 0.0) {
								int percent = (int)(1/EAinREA);
								if (random.nextInt(percent) != 0) {																			
									if (random.nextInt((int)newSlope) < bujetCount) {									
										parent = xStar;									
									}
									else {
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
									}
								}
								else {								
									parent = xStar;									
								}							
							}
							else {															
								if (random.nextInt((int)newSlope) < bujetCount) {									
									parent = xStar;									
								}
								else {
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
								}
							}						
											
						}
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
				}
			}
		}
		else if (name.equals("REAwors")) {
			boolean reaIsWorking = false;		
			ArrayList<BitString> x = new ArrayList<BitString>(); // array of individuals on hamming distance
			ArrayList<Boolean> defined = new ArrayList<Boolean>();			
			int definedSize = 0;
			BitString parent = ind;
			BitString xStar = ind;		
			BitString xOld = ind;
			Integer bestValue = fitness.fitness(xOld);			
			for (int t = 0; t < evSize; t++) {
				bujetCount++;
				if (bujetCount == dynamicBudjet) {
					bujetCount = 0;					
					xOld = xStar;						
					bestValue = fitness.fitness(xOld);	
					int number = answer.get(bestValue)+1;
					answer.set(bestValue, number);										
					fitness.optimumChange(type);
					reaIsWorking = true;					
					x = new ArrayList<BitString>();
					defined = new ArrayList<Boolean>();
					definedSize = 0;
					for (int i = 0; i < gamma + 1; i++) {
						x.add(Init.run(individLen, random));
						defined.add(false);
					}						
					x.set(0, xOld);
					defined.set(0, true);
					definedSize++;
					
				}
				if (reaIsWorking) {
					if (definedSize > 1) {
						if (!smooth) {
							if(EAinREA != 0.0) {
								int percent = (int)(1/EAinREA);
								if (random.nextInt(percent) == 0) {									
									parent = xStar;									
								} else {
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
								}							
							}
							else {
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
							}
						}else {
							double newSlope = individLen*individLen*smoothSlope;
							if(EAinREA != 0.0) {
								int percent = (int)(1/EAinREA);
								if (random.nextInt(percent) != 0) {																			
									if (random.nextInt((int)newSlope) < bujetCount) {									
										parent = xStar;									
									}
									else {
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
									}
								}
								else {								
									parent = xStar;									
								}							
							}
							else {															
								if (random.nextInt((int)newSlope) < bujetCount) {									
									parent = xStar;									
								}
								else {
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
								}
							}						
												
						}
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
					
				}
			}
		}
		
		
		return ff;
		

	}
}
