package simulator;
import java.util.Scanner;
/**
 * Driver class that initializes a simulated computer and executes a provided set of MIPS instructions
 * @author Egor Maksimenka
 */
public class Simulator  {
	/** Scanner used for user input. */
	private static Scanner scan;

	/** The driver main. */
	public static void main(String[] args) {		 
		scan = new Scanner(System.in);		
		// initializes the computer
		Computer comp = new Computer();
		
		// Load machine code strings into this array, they will be executed by the program
		String[] program =  {"00100000000010000000000000000100",
				"00100000000010010000000000000111",
				"00001000000000000000000000010000",
				"00000001000010010101000000100100",
				"00000001010010100101000000100000"};
		
		
		System.out.println("Welcome to the MIPS simulator!\nMake sure you put in the machine code as seperate "
				+ "strings in the array, and that the array contains VALID instructions and isn't empty\nEx: "
				+ "{\"00100001000010000000000000000001\", \"00100001001010010000000000000001\"}");
		System.out.println("Regardless if executed automatically or incrementally, the program will display the "
				+ "data before any instruction has been excuted, and after the last instruction has executed.\n"
				+ "This allows for analysis of registers and memory before and after execution for your conveinence."
				+ "\nIf so desired, there are methods that can be used to manually extract the given register/memory.\n");
		try {
			// attempt to load the program
			comp.loadProgram(program);
			System.out.println("Program loaded. Would you like to run the"
					+ " program automatically or incrementally?\n\nEnter a for automatically, i for incrementally, or e to exit");
			String response = scan.nextLine();
			
			// iteratively execute the specified program
			if (response.equalsIgnoreCase("i")) {
				System.out.println("\nEnter step to step through program, exit to exit the program,\nor finish to "
						+ "finish the program automatically.\n");
				String dataDisp = incrementalDecision();
				System.out.println("\nData before execution:");
				comp.display();
				System.out.println("\nWhat is your decision: ");
				String step = scan.nextLine(); 
				String curr = "";
				while (step.equalsIgnoreCase("step") && curr != null) { 
					curr = comp.increment();
					if (curr == null) { 
						System.out.println();
						comp.display();
						System.out.println();
						System.out.println("Program finished.");
						System.exit(0);
					}
					System.out.println();
					if (dataDisp.equals("y")) { 
						comp.display();
						System.out.println();
					}
					System.out.println("\nWhat is your decision: ");
					step = scan.nextLine();
				}
				if (step.equalsIgnoreCase("exit")) {
					System.exit(0);
				} else if (step.equalsIgnoreCase("finish")) { 
					comp.execute();
					System.out.println("\nData after execution");
					comp.display();
				} else { 
					// did not implement this fail condition in time if you mistype during an iteration :(
					System.out.println("Please enter a correct response during the next iterative execution. Exiting...");
					System.exit(0);
				}
				
			// automatically execute the specified program	
			} else if (response.equalsIgnoreCase("a")) { 
				System.out.println("\nData before execution:");
				comp.display();
				comp.execute();
				System.out.println("\nData after execution");
				comp.display();
				
			// exit 
			} else if (response.equalsIgnoreCase("e")) { 
				System.exit(0);
			} else { 
				while (!response.equalsIgnoreCase("a") && !response.equalsIgnoreCase("e") && !response.equalsIgnoreCase("i")) {
					System.out.println("Invalid command, try again: ");
					response = scan.nextLine();
				}
				if (response.equalsIgnoreCase("i")) {
					
				} else if (response.equalsIgnoreCase("a")) { 
					System.out.println("\nData before execution:");
					comp.display();
					comp.execute();
					System.out.println("\nData after execution");
					comp.display();
				} else if (response.equalsIgnoreCase("e")) { 
					System.exit(0);
				}
			}
		// if something goes wrong with executing the program. Details are provided upon crash.
		} catch (Exception e) {
			System.out.println("\nInvalid/faulty program, please re-enter a valid program and restart main. Aborting...");
			System.exit(0);
		}
	}
	
	/**
	 * Decides the format of incremental execution
	 * @return the decision in regards to the format (data displayed after every iteration or just at the beginning and end)
	 */
	public static String incrementalDecision() { 
		System.out.println("Would you like to display the data after each iteration (y/n)? (default is n): ");
		String response = scan.nextLine();
		while (!response.equalsIgnoreCase("y") && !response.equalsIgnoreCase("n")) {
				System.out.println("Invaid command, try again: ");
				response = scan.nextLine();
		}
		return response.toLowerCase();
	}

}
