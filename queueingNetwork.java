
										/* Name: Srivahni Chivukula
							     			NetID: sxc150530 */
				 			
import java.util.Map.Entry;
import java.util.TreeMap;

public class queueingNetwork {
	
	public enum Events{
		ARR,
		DEP1,
		DEP2,
		DEP3;
	}

	public static TreeMap<Double , Events> EventList = new TreeMap<Double , Events>(); 		// Map for EventList
	public static double clock = 0.0;			// Time at which event occurs
	public static double prev = 0.0;			// Previous time
	public static boolean done = false;
	public static Entry<Double, Events> event;
	public static Events currentEvent;			// Current Event being processed
	public static int N1 = 0; 	 				// Number of customers in Queue 1
	public static int N2 = 0; 	 				// Number of customers in Queue 2
	public static int N3 = 0; 	 				// Number of customers in Queue 3
	public static double mu1;					//Service rate of Queue 1
	public static double mu2;					//Service rate of Queue 2
	public static double mu3;					//Service rate of Queue 3
	public static double exp;					// Exponential value
	public static double lambda = 10;			// Arrival rate to the overall system
	public static double rs1;					// probability of customer entering Queue 1
	public static double rs2;					// probability of customer entering Queue 2
	public static double r11;					// Probability of customer departing from Queue 1 and entering Queue 1 again
	public static double r13;					// Probability of customer departing from Queue 1 and entering Queue 3
	public static double r3d;					// Probability of customer leaving system
	public static double r32;					// Probability of customer leaving Queue 3 and entering Queue 2
	public static double Ndep1;					// Number of departures from Queue 1
	public static double Ndep2;					// Number of departures from Queue 2
	public static double Ndep3;					// Number of departures from Queue 3
	public static double Ndep;					// Total number of Departures
	public static double EN1;					// Expected number of customers in Queue 1
	public static double EN2;					// Expected number of customers in Queue 2
	public static double EN3;					// Expected number of customers in Queue 3
	
	
	// for generating rv
	public static double k = 16807.0;
	public static double mi = 2.147483647e9; 
	public static double Seed = 1111.0;		
	public static double rv;
	public static double prob;
	
	
	// for Theoretical values 
	public static double rho1;
	public static double rho2;
	public static double rho3;
	public static double theta1;
	public static double theta2;
	public static double theta3;
	public static double[] theta = new double[3];
	public static double[] Theo_throughput; 
	public static double EN1_theo;
	public static double EN2_theo;
	public static double EN3_theo;
	public static double[] EN_theo = new double[3];
	public static double[] Theo_EN;
	public static double[] Theo_ExpTime;
	public static double AvgTime1;
	public static double AvgTime2;
	public static double AvgTime3;
	public static double[] AvgTime = new double[3];
	public static double util1;
	public static double util2;
	public static double util3;
	
	
	public static void main(String[] args) 
	{
		// User Inputs	
		rs1 = Double.parseDouble(args[0]);
		rs2 = Double.parseDouble(args[1]);
		r11 = Double.parseDouble(args[2]);
		r13 = Double.parseDouble(args[3]);
		r3d = Double.parseDouble(args[4]);
		r32 = Double.parseDouble(args[5]);
		mu1 = Double.parseDouble(args[6]);
		mu2 = Double.parseDouble(args[7]);
		mu3 = Double.parseDouble(args[8]);
			
		EventList.put(exp_rv(lambda), Events.ARR);			// Generating first Arrival event
		
		
		while(!done)										// Until the system departs 500000 customers
		{
			event = EventList.pollFirstEntry();				// Getting first event from the EventList
			prev = clock;									// Previous events clock
			currentEvent = event.getValue();				// Current Event being Processed
			clock = event.getKey();							// Clock of the Current Event
			EN1 += N1*(clock - prev);						
			EN2 += N2*(clock - prev);						
			EN3 += N3*(clock - prev);						
			if(N1 > 0)										
			{
				util1 += 1 * (clock - prev);;
			}
			
			if(N2 > 0)
			{
				util2 += 1 * (clock - prev);;
			}	
			
			if(N3 > 0)
			{
				util3 += 1 * (clock - prev);;
			}	
		switch(currentEvent)								// To process type of event
		{
		case ARR:											// If Arrival Event
			EventList.put(clock+exp_rv(lambda), Events.ARR);// Generating an Arrival event with rate Lambda	
			prob = uni_rv();								// Tossing a coin or generating a uniform random variable
			if(prob <= rs1)    								// Enter Queue 1				
			{
				N1++;										// Incrementing number of customers in Queue 1 by 1
				if(N1 == 1)									// When one customer in Queue 1
				{
					EventList.put(clock+exp_rv(mu1), Events.DEP1);	// Generate a Departure event with service rate mu1
				}	
			}
			else
			{
				N2++;										// Incrementing number of customers in Queue 2 by 1
				if(N2 == 1)									// When one customer in Queue 2
				{
					EventList.put(clock+exp_rv(mu2), Events.DEP2);	// Generate a Departure event with service rate mu2
				}
			}	
			
		break;
		
		case DEP1:															// If Departure event at Queue 1
				N1--;														// Decrement number of customers in Queue 1
				Ndep1++;													// Increment Number of Departures from Queue 1
				prob = uni_rv();											// Tossing a coin or generating a uniform random variable
				if(prob <= r11)												// Go to Queue 1 again
				{
					N1++;													// Incrementing number of customers in Queue 1 by 1
					if(N1 == 1)												// When one customer in Queue 1
					{
						EventList.put(clock+exp_rv(mu1), Events.DEP1);		// Generating a Departure event for Queue 1 with service rate mu1
						continue;
					}
					
				}
				else
				{
					//Go to Queue 3
					N3++;													// Increment number of customers in Queue 3
					if(N3 == 1)												// If one customer in Queue 3
					{
						EventList.put(clock+exp_rv(mu3), Events.DEP3);		// Generating a Departure event for Queue 3 with service rate mu3
					}
				}
				
				if(N1 > 0)													// If customer in Queue 1
				{
					EventList.put(clock+exp_rv(mu1), Events.DEP1);			// Generate Departure event for Queue 1 with Service rate mu1
				}
		break;
		
		case DEP2:															// If Departure event at Queue 2
				N2--;														// Decrement Queue 2 by one customer
				Ndep2++;													// Increment number of departures from Queue 2
				N3++;														// Increment Number of customers in Queue 3 as customer from Queue 2 enters Queue 3
				if(N3 == 1)													// If only one customer in Queue 3
				{	
				EventList.put(clock+exp_rv(mu3), Events.DEP3);				// Generate Departure event for Queue 3 with Service rate mu3
				}
				if(N2 > 0)													// If customer in Queue 2
				{
					EventList.put(clock+exp_rv(mu2), Events.DEP2);			// Generate Departure event for Queue 2 with Service rate mu2
				}		
		break;
		
		case DEP3:															// If Departure event at Queue 3
				N3--;														// Decrement Queue 3 by one customer
				Ndep3++;													// Increment Number of customers in Queue 3 by 1
				prob = uni_rv();											// Tossing a coin or generating a uniform random variable
				if(prob <= r32)												// Go to Queue 2 from Queue 3
				{
					N2++;													// Increment number of customers in Queue 2 by 1
					if(N2 == 1)												// If one customer in Queue 2
					{
						EventList.put(clock+exp_rv(mu2), Events.DEP2);		// Generate Departure event for Queue 2 with Service rate mu2
					}		
					
				}
				else
				{
					Ndep++;													// Increment Number of departures as customer leaves system
				}
				
				
				if(N3 > 0)													// If customer in Queue 3
				{
					EventList.put(clock+exp_rv(mu3), Events.DEP3);			// Generate Departure event for Queue 3 with Service rate mu3
				}	
				
		break;
		}
		
	
		if(Ndep > 500000)				// for 500,000 departures			
		{
			done = true;
		}
		
		
		}
		
		System.out.println("Lambda: " +lambda);
		System.out.println("--------------Throughput-------------");
		System.out.println("Throughput (simulation): " );
		System.out.println("Queue 1: " +Ndep1/clock);
		System.out.println("Queue 2: " +Ndep2/clock);
		System.out.println("Queue 3: " +Ndep3/clock);
		System.out.println("");
		Theo_throughput = Throughput_theoretical();
		System.out.println("Throughput (Theoretial): ");
		System.out.println("Queue 1: " + Theo_throughput[0]);
		System.out.println("Queue 2: " + Theo_throughput[1]);
		System.out.println("Queue 3: " + Theo_throughput[2]);
		System.out.println("");
		System.out.println("");
		
		
		System.out.println("--------------Expected number of customers-------------");
		System.out.println("Expected number of customers (simulation): ");
		System.out.println("Queue 1: " +EN1/clock);
		System.out.println("Queue 2: " +EN2/clock);
		System.out.println("Queue 3: " +EN3/clock);
		System.out.println("");
		System.out.println("Expected number of customers (Theoretial): ");
		Theo_EN = EN_theoretical();
		System.out.println("Queue 1: " +Theo_EN[0]);
		System.out.println("Queue 2: " +Theo_EN[1]);
		System.out.println("Queue 3: " +Theo_EN[2]);
		System.out.println("");
		System.out.println("");
		
		System.out.println("---------------Expected Waiting time--------------------");
		System.out.println("Expected waiting time (simulation) : ");
		System.out.println("Queue 1: " + EN1/Ndep1);
		System.out.println("Queue 2: " + EN2/Ndep2);
		System.out.println("Queue 3: " + EN3/Ndep3);
		System.out.println("");
		System.out.println("Expected waiting time (Theoretical): ");
		Theo_ExpTime = ExpTime_Theoretical();
		System.out.println("Queue 1: " + Theo_ExpTime[0]);
		System.out.println("Queue 2: " + Theo_ExpTime[1]);
		System.out.println("Queue 3: " + Theo_ExpTime[2]);
		System.out.println("");
		System.out.println("");
		
		System.out.println("---------------Utilization--------------------");
		System.out.println("Utilization (simulation) : ");
		System.out.println("Server 1: " + util1/clock);
		System.out.println("Server 2: " + util2/clock);
		System.out.println("Server 3: " + util3/clock);
		System.out.println("");
		System.out.println("Utilization (Theoretical): ");
		System.out.println("Server 1: " + rho1);
		System.out.println("Server 2: " + rho2);
		System.out.println("Server 3: " + rho3);
		System.out.println("");
		System.out.println("");
	}


	private static double[] Throughput_theoretical() 
	{
		theta1 = (lambda * rs1) / (1 - r11);
		theta3 = ((lambda * rs2) + (theta1 * r13)) / (1 - r32);
		theta2 = (lambda * rs2) + (theta3 * r32);
		theta[0] = theta1;
		theta[1] = theta2;
		theta[2] = theta3;
		return theta;
	}


	private static double[] EN_theoretical() 
	{
		
		rho1 = theta1 / mu1;
		rho2 = theta2 / mu2;
		rho3 = theta3 / mu3;
		EN1_theo = rho1 / (1 - rho1);
		EN2_theo = rho2 / (1 - rho2);
		EN3_theo = rho3 / (1 - rho3);
		EN_theo[0] = EN1_theo;
		EN_theo[1] = EN2_theo;
		EN_theo[2] = EN3_theo;	
		return EN_theo;
	}

	
	private static double[] ExpTime_Theoretical() 
	{
		AvgTime1 = EN1_theo / theta1;
		AvgTime2 = EN2_theo / theta2;
		AvgTime3 = EN3_theo / theta3;
		AvgTime[0] = AvgTime1;
		AvgTime[1] = AvgTime2;
		AvgTime[2] = AvgTime3;
		return AvgTime;
	}	

	// Generation of an exponential and uniform random variable
	public static double exp_rv(double lambda)
	{
		exp = (-1/lambda) * (Math.log(uni_rv()));
		return exp;
	}
			
	public static double uni_rv()
	{
		Seed = (k * Seed) % mi;		
		rv=Seed/mi;
	    return(rv);
	}
	
	
}

