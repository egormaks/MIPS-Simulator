package simulator;
/**
 * Computer class comprises of memory, registers, and
 * can execute instructions based on PC and IR 
 * @author Egor Maksimenka
 */
public class Computer {
	
	/** The strict length for an instruction. */
	private final static int INSTRUCTION_LENGTH = 32;
	/** The size of the memory, no. of available cells. */
	public final static int MAX_MEMORY = 500;
	/** Number of available registers. */
	public final static int MAX_REGISTERS = 32;
	/** Maximum number of instructions allowed. */
	public final static int MAX_INSTRUCTIONS = 200;
	
	/** Op code for ADD, AND, and JR. */
	private final static int ADD_AND_JR_OP = 0;
	/** Function code for ADD. */
	private final static int ADD_FUNC = 32;
	/** Function code for AND. */
	private final static int AND_FUNC = 36;
	/** Function code for JR. */
	private final static int JR_FUNC = 8;
	/** Op code for ADDI. */
	private final static int ADDI_OP = 8;
	/** Op code for ANDI. */
	private final static int ANDI_OP = 12;
	/** Op code for LW. */
	private final static int LW_OP = 35;
	/** Op code for SW. */
	private final static int SW_OP = 43;
	/** Op code for BEQ. */
	private final static int BEQ_OP =  4;
	/** Op code for J. */
	private final static int J_OP = 2;

	/** The registers used by the computer. */
	private BitString mRegisters[];
	/** The simulated memory used by the computer. */
	private BitString mMemory[];
	/** The instructions for the input program. */
	private BitString mInstructions[];
	/** The PC, or the current instruction addr. */
	private BitString mPC;
	/** The instruction located at the PC. */
	private BitString mIR;

	/**
	 * Default constructor for Computer. Initializes all values to 0.
	 */
	public Computer() {
		resetProgram();
	}
	 
	/**
	 * Loads an array of machine code instructions into the computer
	 * @param instructions
	 * @exception IAG if array is longer than allowed, if the array is empty, if the array is
	 * uninstantiated, or if the array contains instructions of incorrect length
	 */
	public void loadProgram(String[] instructions) {
		if (instructions.length >= MAX_INSTRUCTIONS || instructions.length == 0) { 
			throw new IllegalArgumentException("Invalid no. of instructions");
		}
		int i;
		for (i = 0; i < instructions.length; i++) {
			if (instructions[i] == null) { 
				throw new IllegalArgumentException("Invalid program / uninstantiated array");
			}
			String str = instructions[i];
			if (str.length() != INSTRUCTION_LENGTH) { 
				throw new IllegalArgumentException("Invalid program.");
			}
			BitString inst = new BitString();
			inst.setBits(str.toCharArray());
			mInstructions[i] = inst;
		}
	}

	public void resetProgram() {
        int i;
        mPC = new BitString();
        mPC.setValue(0);
        mIR = new BitString();
        mIR.setValue(0);
        mRegisters = new BitString[MAX_REGISTERS];
        for (i = 0; i < MAX_REGISTERS; i++) {
            mRegisters[i] = new BitString();
            mRegisters[i].setValue(0);
        }

        mInstructions = new BitString[MAX_INSTRUCTIONS];
        for (i = 0; i < mInstructions.length; i++) {
            mInstructions[i] = new BitString();
            mInstructions[i].setValue(0);
        }

        mMemory = new BitString[MAX_MEMORY];
        for (i = 0; i < MAX_MEMORY; i++) {
            mMemory[i] = new BitString();
            mMemory[i].setValue(0);
        }
    }

    /**
     * Gets all current data stored in all registers
     * @return array of data in the registers
     */
	public BitString[] getRegisterContents() {
	    return mRegisters;
    }

	/**
	 * Testing method. Returns the contents of the desired register
	 * @param n the register number
	 * @return the contents of the register
	 */
	public int getRegisterContents(int n) { 
		return mRegisters[n].getValue2sComp();
	}
	
	/**
	 * Testing method. Sets the content of the specified register to the given value
	 * @param value the value 
	 * @param register the register to be modified
	 */
	public void setRegisterContents(int value, int register) { 
		mRegisters[register].setValue2sComp(value);
	}

    /**
     * Gets all current data stored in memory
     * @return array of data in memory
     */
	public BitString[] getMemoryContents() {
	    return mMemory;
    }

	/**
	 * Testing method. Gets the content at the specified memory address.
	 * @param n the memory address.
	 * @return the stored value
	 */
	public int getMemoryContents(int n) { 
		return mMemory[n].getValue2sComp();
	}
	
	/**
	 * Testing method. Sets the value of the specified memory address.
	 * @param val the value to be stored
	 * @param addr the memory address.
	 */
	public void setMemoryContents(int val, int addr) { 
		mMemory[addr].setValue2sComp(val);
	}

	/**
	 * Testing method. Returns the PC
	 * @return a copy of the PC BitString
	 */
	public BitString getPC() {
		return mPC.copy();
	}
	
	/**
	 * Testing method. Returns the IR
	 * @return a copy of the IR BitString
	 */
	public BitString getIR() { 
		return mIR.copy();
	}
	
	/**
	 * Testing method. Returns the array of instructions.
	 * @return the BitString array containing the instructions
	 */
	public BitString[] getInstructions() { 
		return mInstructions.clone();
	}
	
	/**
	 * Executes the provided instructions, starting at 0 and runs until all instructions 
	 * are executed.
	 */
	public void execute() {
		BitString opCodeStr;
		int opCode;
		while (true) {
			mIR = mInstructions[mPC.getValue() / 4];
			if (mIR.getValue() == 0) { 
				System.out.println("Program finished.");
				break;
			}

			mPC.setValue(mPC.getValue() + 4);
			
			opCodeStr = mIR.getOpCode();
			opCode = opCodeStr.getValue(); 
			
			if (opCode == ADD_AND_JR_OP) { 
				int func = mIR.getFunct().getValue();
				if (func == ADD_FUNC) {  
					executeAdd(false);
				} else if (func == AND_FUNC) { 
					executeAnd(false);
				} else if (func == JR_FUNC) { 
					executeRegJump(mIR.getRs());
				} else { 
					throw new IllegalArgumentException("Undefined function");
				}
			} else if (opCode == ADDI_OP) {
				executeAdd(true);
			} else if (opCode == ANDI_OP) { 
				executeAnd(true);
			} else if (opCode == LW_OP) { 
				executeLoadWord(mIR.getRs(), mIR.getRt(), mIR.getCnst());
			} else if (opCode == SW_OP) { 
				executeStoreWord(mIR.getRs(), mIR.getRt(), mIR.getCnst());
			} else if (opCode == J_OP) { 
				executeJump(mIR.getPseudoAddr());
			} else if (opCode == BEQ_OP) { 
				executeBeq(mIR.getRs(), mIR.getRt(), mIR.getCnst());
			}	else {
				throw new IllegalArgumentException("Undefined opcode");
			}
		}
	}
	
	/**
	 * Another method of executing the program. Manually called from client as 
	 * opposed to automatically running through the instructions.
	 * @return null when all instructions are executed, otherwise a generic placeholder string
	 */
	public String increment() {
		BitString opCodeStr;
		int opCode;
		
		mIR = mInstructions[mPC.getValue() / 4];
		if (mIR.getValue() == 0) { 
			return null;
		}		
		
		mPC.setValue(mPC.getValue() + 4);

		
		opCodeStr = mIR.getOpCode();
		opCode = opCodeStr.getValue(); 
		
		if (opCode == ADD_AND_JR_OP) { 
			int func = mIR.getFunct().getValue();
			if (func == ADD_FUNC) {  
				executeAdd(false);
			} else if (func == AND_FUNC) { 
				executeAnd(false);
			} else if (func == JR_FUNC) { 
				executeRegJump(mIR.getRs());
			} else { 
				System.out.println("Undefine function at instruction: ");
				mIR.display(true);
				throw new IllegalArgumentException("Undefined function");
				
			}
		} else if (opCode == ADDI_OP) {
			executeAdd(true);
		} else if (opCode == ANDI_OP) { 
			executeAnd(true);
		} else if (opCode == LW_OP) { 
			executeLoadWord(mIR.getRs(), mIR.getRt(), mIR.getCnst());
		} else if (opCode == SW_OP) { 
			executeStoreWord(mIR.getRs(), mIR.getRt(), mIR.getCnst());
		} else if (opCode == J_OP) { 
			executeJump(mIR.getPseudoAddr());
		} else if (opCode == BEQ_OP) { 
			executeBeq(mIR.getRs(), mIR.getRt(), mIR.getCnst());
		}	else {
			System.out.println("Undefine opcode at instruction: ");
			mIR.display(true);
			throw new IllegalArgumentException("Undefined opcode");
		}
		return "running";
	}
	
	/**
	 * Executes the ADD/ADDI instructions. Distinguishes between the two and performs 
	 * the respective operations
	 * @param imm if true, executes ADDI, otherwise executes ADD
	 * @throws IAG if an overflow exception occurs or the target register is $zero
	 */
	private void executeAdd(boolean imm) {
		BitString rS = mIR.getRs();
		BitString target;
		int ans;
		int rs = rS.getValue();
		int rsVal = mRegisters[rs].getValue2sComp();
		if (imm) {
			target = mIR.getRt();
			int cnst = mIR.getCnst().getValue2sComp();
			if (checkOverflow(cnst, rsVal)) { 
				System.out.println("Overflow exception: ");
				mIR.display(true);
				throw new IllegalArgumentException("Overflow");
			}
			ans = rsVal + cnst;
		} else {
			target = mIR.getRd();
			int rt = mIR.getRt().getValue();
			int rtVal = mRegisters[rt].getValue2sComp();
			if (checkOverflow(rsVal, rtVal)) { 
				System.out.println("Overflow exception: ");
				mIR.display(true);
				throw new IllegalArgumentException("Overflow");
			}
			ans = rsVal + rtVal;
		}
		int tVal = target.getValue();
		if (tVal == 0) { 
			System.out.println("Attempt to write to $zero at instruction: ");
			mIR.display(true);
			throw new IllegalArgumentException("Invalid register");
		}
		mRegisters[tVal].setValue2sComp(ans);
		
	}
	
	/**
	 * Executes the AND/ANDI instructions. Distinguishes between the two and performs 
	 * the respective operations
	 * @param imm if true, executes ANDI, otherwise executes AND
	 * @throws IAG if the target register is $zero
	 */
	private void executeAnd(boolean imm) { 
		BitString rS = mIR.getRs();
		BitString target;
		int ans;
		int rs = rS.getValue();
		int rsVal = mRegisters[rs].getValue2sComp();
		if (imm) {
			target = mIR.getRt();
			int cnst = mIR.getCnst().getValue2sComp();
			ans = rsVal & cnst;
		} else {
			target = mIR.getRd();
			int rt = mIR.getRt().getValue();
			int rtVal = mRegisters[rt].getValue2sComp();
			ans = rsVal & rtVal;
		}
		int tVal = target.getValue();
		if (tVal <= 0) { 
			System.out.println("Attempt to write to $zero at instruction: ");
			mIR.display(true);
			throw new IllegalArgumentException("Invalid register");
		}
		mRegisters[target.getValue()].setValue2sComp(ans);
	}
	
	/**
	 * Executes the jump instruction. Appends all necessary values to create the new location
	 * and updates PC.
	 * @param pseudoAddr the pseudo-address that's appended to the PC[31:28] value
	 * @throws IAG when the target address is greater than the amount of instruction space available.
	 */
	private void executeJump(BitString pseudoAddr) { 
		BitString newPC = mPC.substring(0, 4);
		newPC = newPC.append(pseudoAddr);
		char zeros[] = {'0', '0'};
		BitString z = new BitString();
		z.setBits(zeros);
		newPC = newPC.append(z);
		if (newPC.getValue() >= MAX_INSTRUCTIONS) { 
			System.out.println("Out of bounds jump target at instruction: ");
			mIR.display(true);
			throw new IllegalArgumentException("Out of bounds jump target at instruction");
		}
		mPC.setValue(newPC.getValue());;
	}
	
	/**
	 * Executes the JR instruction. Updates the PC to the stored address in the register.
	 * @param register
	 * @throws IAG when the stored address is not a multiple of 4.
	 */
	private void executeRegJump(BitString register) { 
		BitString newAddr = mRegisters[register.getValue()];
		if (newAddr.getValue2sComp() % 4 != 0) { 
			System.out.println("Address error exception at instruction (not naturally aligned): ");
			mIR.display(true);
			throw new IllegalArgumentException("Address error exception");
		}
		mPC.setValue(newAddr.getValue());
	}
	
	/**
	 * Private helper method. Checks to see if the two specified values cause an overflow 
	 * when added.
	 * @param val1 first value
	 * @param val2 second value
	 * @return true if there is an overflow, false otherwise.
	 */
	private boolean checkOverflow(int val1, int val2) { 
		boolean overflow = false;
		if (val1 > 0 && val2 > 0) { 
			if (val1 + val2 < 0) { 
				overflow = true;
			} 
		} else if (val1 < 0 && val2 < 0) { 
			if (val1 + val2 > 0) { 
				overflow = true;
			}
		}
		return overflow;
	}
	
	/**
	 * Executes the LW instruction. Access the appropriate memory address and stores the data in 
	 * the specified register.
	 * @param rS
	 * @param rT
	 * @param offset
	 * @throws IAG when trying to write to $zero, an overflow happens, the address is not a multiple of 4, or the memory address 
	 * is less than 0 or greater than the amount of memory available
	 */
	private void executeLoadWord(BitString rS, BitString rT, BitString offset) { 
		if (rT.getValue() == 0) { 
			System.out.println("Attempting to write to $zero at instruction: ");
			mIR.display(true);
			throw new IllegalArgumentException("Cannot write to 0 register @LW");
		}
		BitString addr1 = mRegisters[rS.getValue()];
		BitString combined = new BitString();
		int off = offset.getValue2sComp();
		int addr = addr1.getValue2sComp();
		if (checkOverflow(off, addr)) { 
			System.out.println("Overflow exception: ");
			mIR.display(true);
			throw new IllegalArgumentException("Overflow");
		}
		int sum = off + addr;
		combined.setValue2sComp(sum);
		int addrIndex = combined.getValue();
		System.out.println("addr = " + addrIndex);
				
		if (addrIndex % 4 != 0) { 
			System.out.println("Address error exception at instruction (not naturally aligned): ");
			mIR.display(true);
			throw new IllegalArgumentException("Address error exception");
		}
		if (addrIndex >= MAX_MEMORY || addrIndex < 0) { 
			System.out.println("Memory address exceeds limit at instruction: ");
			mIR.display(true);
			throw new IllegalArgumentException("Memory address exceeds limit");
		}
		
		int register = rT.getValue();
		mRegisters[register].setValue2sComp(mMemory[addrIndex].getValue2sComp());
	}
	
	/**
	 * Executes the SW instruction. Stores the data in a specified register at a calculated address in memory
	 * @param rS used for calculating the mem address
	 * @param rT the register containing the data.
	 * @param offset the offset value used to calculate the memory address
	 * @throws IAG when an overflow occurs, the address is not a multiple of 4, or if the desired address < 0 or
	 * larger than the amount of memory available
	 */
	private void executeStoreWord(BitString rS, BitString rT, BitString offset) {
		int regVal = mRegisters[rS.getValue()].getValue2sComp();
		int off = offset.getValue2sComp();
		if (checkOverflow(off, regVal)) { 
			System.out.println("Overflow exception: ");
			mIR.display(true);
			throw new IllegalArgumentException("Overflow");
		}
		int addr = regVal + off;
		if (addr % 4 != 0) { 
			System.out.println("Address error exception at instruction (not naturally aligned): ");
			mIR.display(true);
			throw new IllegalArgumentException("Address error exception");
		}
		if (addr >= MAX_MEMORY || addr < 0) { 
			System.out.println("Memory address exceeds limit at instruction: ");
			mIR.display(true);
			throw new IllegalArgumentException("Memory address exceeds limit");
		}
		
		mMemory[addr].setValue2sComp(mRegisters[rT.getValue()].getValue2sComp());	
	}
	
	/**
	 * Executes the BEQ instruction if the equality between the Rs and Rt 
	 * registers is met.
	 * @param rS the rS register
	 * @param rT the rT register
	 * @param imm the immediate value used to calculate the address to jump to.
	 * @throws an IAG if the address is less than 0 or beyond the number of 
	 * available instructions.
	 */
	private void executeBeq(BitString rS, BitString rT, BitString imm) { 
		int val = imm.getValue2sComp();
		int rsVal = mRegisters[rS.getValue()].getValue2sComp();
		int rtVal = mRegisters[rT.getValue()].getValue2sComp();
		if (rtVal == rsVal) { 
			int newAddr = mPC.getValue() + 4 * val;
			if (newAddr >= MAX_INSTRUCTIONS || newAddr <= 0) { 
				System.out.println("Out of bounds register at instruction: ");
				mIR.display(true);
				throw new IllegalArgumentException("Out of bounds register");
			}
			mPC.setValue(newAddr);
		}
	}


	/**
	 * Displays the computer's state
	 */
	public void display() {
		System.out.print("\nPC ");
		mPC.display(true);
		System.out.print("   ");

		System.out.print("IR ");
		mPC.display(true);
		System.out.print("   ");

		for (int i = 0; i < MAX_REGISTERS; i++) {
			System.out.printf("R%d ", i);
			mRegisters[i].display(true);
			if (i % 3 == 2) {
				System.out.println();
			} else {
				System.out.print("   ");
			}
		}
		System.out.println();

		for (int i = 0; i < MAX_MEMORY; i++) {
			System.out.printf("%3d ", i);
			mMemory[i].display(true);
			if (i % 3 == 2) {
				System.out.println();
			} else {
				System.out.print("   ");
			}
		}
		System.out.println();

	}
}
