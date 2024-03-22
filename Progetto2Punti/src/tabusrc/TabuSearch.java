package tabusrc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import tabusrc.changes.Changes;
import tabusrc.changes.Solution;
import tabusrc.tabumanager.TabuManager;

public class TabuSearch {
    // Define the objective function
    public static double objectiveFunction(Solution solution, double[][] data) {
    	double T = 0.0;
		double C_j = 0.0;//Cumulative
		for(int j = 0; j < solution.value.size(); j++) {
			double[] temp_data = data[solution.value.get(j).key];
			// temp_data:
			// 			0 -> w
			//			1 -> p
			// 			2 -> d
			double w_j = temp_data[0];
			double p_j = temp_data[1];
			double d_j = temp_data[2];
			/*
			 * Completion time Cj
			 * is computed as the sum of the
			 * processing time pj of job j, and the processing times of all the jobs
			 * that have been scheduled before j.
			 */
			C_j += p_j;
			double v_plus = C_j - d_j;
			// v_plus = max(0,v) = in this case = max(0,v_plus)
			if(v_plus < 0.0) { v_plus = 0.0; }
			T += (w_j * v_plus);
		}
		return T;
    }
 
    // Define the neighborhood function
    public static List<Solution> getNeighbors(Solution solution, int m, int n) {
    	List<Solution> neighbors = new ArrayList<>();
    	for (int i = 0; i < solution.value.size(); i++) {
    		//For each element I evaluate the changes from i-m to i+m
    		//and n random
    		int from_i = i-m;
    		while(from_i <= i+m && from_i < solution.value.size()) {
    			//If the start index is out of lower bound or is equals to current
    			//element, do anything, increment variable and continue (is useless
    			//to change element i with himself..).
    			if(from_i < 0 || from_i == i) {
    				from_i++;
    				continue;
    			}
    			//If is > than 0 or different from himself, evaluate the change from
    			//i to j by creating a new solution and evaluate it.
    			int j = from_i;
    			Solution sol = new Solution(new ArrayList<>(solution.value));
    			//Switch element i with element j.
    			//NB: the value of element at the position 2 for instance is not the
    			// index, is the value!! Example:
    			// I have solution: {5,1,3,2,6,4}
    			// i = 3 and m = 1 => i=3 means we're at position 4 with value 2.
    			// {5,1,3,(2),6,4}
    			// and we want to switch all items from i-m to i+m with element i.
    			// So, in this case we switch i-1 with i and i+1 with i and, after this
    			// current while loop we create 2 new solution (= 2 neighbors).
    			// From:
    			// {5,1,(3),(2),(6),4}
    			// We obtain:
    			// {5,1,2,3,6,4}
    			// and
    			// {5,1,3,6,2,4}
    			// So let's create new neighbor by switching the element i with j.
    			// We have list of key value (in our case is 6 that are x1,x2...x6).
    			// if the KeyValue instance in the position 0 has value 2 means that
    			// variable x1 has value 2 => the index 2 is in the first position.
    			KeyValue temp = solution.value.get(i);
    			sol.value.set(i, sol.value.get(j));
    			sol.value.set(j, temp);
    			//Add this new neighbor to the list
    			neighbors.add(sol);
    			from_i++;
    		}
    		// Now that we've evaluated all neighbors in a greedy way. I want to add
    		// random switching to see different part of feasible solutions (this
    		// part of the algorithm is good for large number of data to increment
    		// differentiation). In other terms random switch increase the size of
    		// switching area... Example with 10 elements, m=1,n=1,i=4:
    		// {5,1,10,(3),6,9,2,4,7,8}
    		// Normally 3 can be switched with 10 and 6 cause m=1. With random i can
    		// switch 3 with also the last element 8 and we obtain:
    		// {5,1,10,(8),6,9,2,4,7,(3)}
    		
    		
    		//Now random neighbor: Create n random switch solutions
    		int random_count = 0;
    		while(random_count < n) {
    			// Check if the algorithm switch left or right from i:
    			// i plus something (forward) or i minus something?
    			int forward_or_backward = (int)Math.random();
    			int from_val,to_val;
        		if(forward_or_backward == 1) { // go forward
    				from_val = i+m;
        			to_val = solution.value.size()-1;
        			int rand_j = to_val;
        			if(to_val < to_val) {
	        			//get random value in range [i+m,len(arr)-1]
	        			rand_j = (int)((Math.random() * (to_val - from_val)) + from_val);
        			}
        			Solution sol = new Solution(new ArrayList<>(solution.value));
        			// Do the switch
        			KeyValue temp = solution.value.get(i);
        			sol.value.set(i, sol.value.get(rand_j));
        			sol.value.set(rand_j, temp);
        			//Add switch to the neighborhood
        			neighbors.add(sol);
        		}else { // Go backward
    				from_val = 0;
        			to_val = i-m-1;
        			int rand_j = from_val;
        			if(to_val > from_val)
        				rand_j = (int)((Math.random() * (to_val - from_val)) + from_val);
        			// Create new solution
        			Solution sol = new Solution(new ArrayList<>(solution.value));
        			// Switch
        			KeyValue temp = solution.value.get(i);
        			sol.value.set(i, sol.value.get(rand_j));
        			sol.value.set(rand_j, temp);
        			// Add the solution to the neighborhood
        			neighbors.add(sol);
        		}
        		random_count++;
    		}
    	}
        return neighbors;
    }
 
    // Define the Tabu Search algorithm
    public static Solution tabuSearch(Solution initialSolution, 
                                           int maxIterations, int tabuListSize,
                                           double[][] data,
                                           int m, int n) throws Exception {
        Solution bestSolution = new Solution(new ArrayList<>(initialSolution.value));
        Solution currentSolution = new Solution(new ArrayList<>(initialSolution.value));
        TabuManager tabu_manager = new TabuManager(tabuListSize);
 
        for (int iter = 0; iter < maxIterations; iter++) {
        	System.out.println("Iteration "+iter);
        	print_best_solution(bestSolution, data);
        	//-----PHASE 2: Generate the neighborhood N(x)----------------
            List<Solution> neighbors = getNeighbors(currentSolution,m,n);
            
	        //-----PHASE 3: Find a solution y ∈ N(x) such that:
	        //     (1) f(y) is minimal; (2) y!=x; (3) the solution
            //	   does not violate any tabu move-----
            Solution bestNeighbor = new Solution(new ArrayList<>());
            double bestNeighborFitness = Double.MAX_VALUE;
            for (Solution neighbor : neighbors) {
            	//(3) the solution does not violate any tabu move
                if (tabu_manager.allow(currentSolution,neighbor)) {
                    double neighborFitness = objectiveFunction(neighbor, data);
                    //(1) f(y) is minimal && "strict less than" implies that (2) y != x.
                    if (neighborFitness < bestNeighborFitness) {
                        bestNeighbor = new Solution(new ArrayList<>(neighbor.value));
                        bestNeighborFitness = neighborFitness;
                    }
                }
            }
 
            if (bestNeighbor.value.isEmpty()) {
                // No non-tabu neighbors found, terminate the search
                break;
            }
            //Before set y = x catch the changes
            Changes current_changes_from_old = currentSolution.getChangesFromNew(bestNeighbor);
            //-----PHASE 4: Set x = y-----
            currentSolution = new Solution(new ArrayList<>(bestNeighbor.value));
            //-----PHASE 5: Update TL by adding the move that changes x into y-----
            /*
             * In this case I add to tabuList the best neighbor. To know the changes
             * I get the last 2 element in tabuList and check what is changed from the
             * first to the second. For instance:
             * element 2 (last): 1 2 4 3 5
             * element 1: 	     1 2 3 4 5
             * The move was: 3 <-> 4
            */
            tabu_manager.add_tabu(current_changes_from_old);
 
            if (objectiveFunction(bestNeighbor, data) < objectiveFunction(bestSolution, data)) {
                // Update the best solution if the current neighbor is better
                bestSolution = new Solution(new ArrayList<>(bestNeighbor.value));
                System.out.println("Found best at iteration "+iter);
            }
        }
 
        return bestSolution;
    }
 
    public static void main(String[] args) {
        // Example usage
        // Provide an initial solution
        int maxIterations = 10;
        int tabuListSize = 4; //Tabu tenure
        double[][] data = {
    			{ 1,6,9 },
    			{ 1,4,12},
    			{ 1,8,15},
    			{ 1,2,8},
    			{ 1,10,20},
    			{ 1,3,22}
    		};
        //-----PHASE 1: Identify a feasible solution----------------
        Solution initialSolution = new Solution(List.of(get_initialSolution(data)));
        System.out.print("The initial feasible solution is:");
        for (KeyValue val : initialSolution.value) {
            System.out.print(" " + val.key);
        }
        System.out.printf(" -> (%f)\n",objectiveFunction(initialSolution, data));
        int m = 1; //i-m .... i+m switches
        int n = 1; //Random switches PER ELEMENT =>
        // 2 means 2 random switches per 6 elements => 12 random switches total
        
		try {
			Solution bestSolution = tabuSearch(initialSolution, maxIterations,
					tabuListSize, data,m,n);
			System.out.printf("\n\n----FINAL RESULT----\n");
			print_best_solution(bestSolution,data);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    private static void print_best_solution(Solution bestSolution, double[][] data) {
    	System.out.print("Best solution:");
        for (KeyValue val : bestSolution.value) {
            System.out.print(" " + val.key);
        }
        System.out.printf(" -> (%f)\n",objectiveFunction(bestSolution, data));
    }

    /*
     	I spent time to think about the initial feasible solution and I tried different
     	approaches studying the best for this case.
		Looking at the problem, because was a sequencing problem, initial solution could
		have been the default sequence 1-2-3-4-5. In fact, with the this sequence given
		by the problem and knowing the best (19) and the worst (68), the objective function
		was good (36). The problem is that not always you’re lucky to get a good initial
		solution with this method (the method is: do nothing and use the default sequence
		1,2,3…). 
		The second idea was order by increasing processing time (p). With that I obtain the
		sequence 3-5-1-0-2-4 (27). And for fun the decreasing case too: 4-2-0-1-5-3 (68)
		worst case scenario.
		The third idea was order by increasing normalized date (d). With that I obtain the
		sequence 3-0-1-2-4-5 with value 26. And the decreasing case 5-4-2-1-0-3 (66).
		The fourth idea was getting increasing ratio between processing time and date (p/d).
		The result was not satisfying as my expectations 5-3-1-4-2-0 (36) (decreasing ratio:
		0-2-4-1-3-5 (53)).
		So I tried decreasing date – processing time and I obtained 0-3-2-1-4-5 (30), better
		than the other one but not the best found (increasing case: 5-4-1-2-3-0 (58)).
		So I tried decreasing normalized processing time – normalized date and I obtained
		5-3-1-2-4-0 (33) (increasing case: 0-4-2-1-3-5 (58)).
		The final case I tried was: I get first k values with increasing normalized d and
		with the remaining ones I chose values with increasing normalized p and I obtained
		the best possible 3-0-1-2-5-4 (19).
		Summing up I tried many different approaches and the simplest one, order with
		increasing normalized date, was almost the best (fob: 26) and the final case
		(with mixed sorting) was the best (fob: 19). I used the third (fob: 26) for
		example because in general, with other input data, is difficult to chose the right
		“k”.

     */
    public static KeyValue[] get_initialSolution(double[][] data) {
    	//Sum of d
    	double tot_d = 0.0;
    	for(int i = 0; i < data.length; i++) {
    		tot_d += data[i][2];
    	}
    	//Populate the array
    	KeyValue[] ordered = new KeyValue[data.length];
    	for(int i = 0; i < data.length; i++) {
    		double w_i = data[i][0];
    		double d_i = data[i][2];
			KeyValue kv1 = new KeyValue();
			kv1.key = i;
			kv1.value = (w_i)*((d_i/tot_d));
			ordered[i] = kv1;
		}
    	Arrays.sort(ordered);
    	//This type of sort use the implementation of "Comparable" interface.
		return ordered;
	}

}