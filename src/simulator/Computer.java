package simulator;

import java.util.Stack;

/**
 * Computer class comprises of memory, registers, and
 * can executeProgram instructions based on PC and IR
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

	/** Stores the address of the op_code in a pipeline. */
    private static final int OP_CODE = 0;
    /** Stores the address of the target register in a pipeline. */
    private static final int REGISTER_TARGET = 1;
    /** Stores the address of the target mem location in a pipeline. */
    private static final int MEMORY_TARGET = 2;
    /** Stores the address of value to be written in a pipeline. */
    private static final int WRITE_VAL = 3;
    /** Stores the address of IR in a pipeline. */
    private static final int M_IR = 4;
    /** The id for the IF stage within the pipeline stack. */
    private static final int IF = 1;
    /** The id for the ID stage within the pipeline stack. */
    private static final int ID = 2;
    /** The id for the EX stage within the pipeline stack. */
    private static final int EX = 3;
    /** The id for the MEM stage within the pipeline stack. */
    private static final int MEM = 4;
    /** The id for the WB stage within the pipeline stack. */
    private static final int WB = 5;

	/** The registers used by the computer. */
	private BitString mRegisters[];
	/** The simulated memory used by the computer. */
	private BitString mMemory[];
	/** The instructions for the input program. */
	private BitString mInstructions[];
	/** The PC, or the current instruction addr. */
	private BitString mPC;
    /**
     *  The pipeline between the IF and ID stages. Sends the
     *  IR to ID.
     */
	private BitString[] ifIdPipeline;
    /**
     *  The pipeline between the ID and EX stages. Sends the OP_CODE and
     *  IR to the EX stage.
     */
	private BitString[] idExPipeline;
    /**
     *  The pipeline between the EX and MEM stages. Sends the value to be stored,
     *  mem location if that's necessary, and the target register if that's necessary.
     */
	private BitString[] exMemPipeline;
    /**
     *  The pipeline between the MEM and WB stages. Sends the value to be stored and
     *  the corresponding register (-1 if no write necessary).
     */
	private BitString[] memWbPipeline;
    /**
     * The stack used to stimulate overlaying stages between multiple instructions.
     */
	private Stack<Integer> pipelineOrder;


	/**
	 * Default constructor for Computer. Initializes all values to 0.
	 */
	public Computer() {
		ifIdPipeline = new BitString[5];
		idExPipeline = new BitString[5];
		exMemPipeline = new BitString[5];
		memWbPipeline = new BitString[5];
		for (int i = 0; i < 5; i++) {
			ifIdPipeline[i] = new BitString();
			idExPipeline[i] = new BitString();
			exMemPipeline[i] = new BitString();
			memWbPipeline[i] = new BitString();
		}
		this.pipelineOrder = new Stack<>();
		resetProgram();
	}
	 
	/**
	 * Loads an array of machine code instructions into the computer
	 * @param instructions
	 * @exception IAG if array is longer than allowed, if the array is empty, if the array is
	 * uninstantiated, or if the array contains instructions of incorrect length
	 */
	public void loadProgram(String[] instructions) {
		if (instructions.length >= MAX_INSTRUCTIONS || instructions.length == 0)
			throw new IllegalArgumentException("Invalid no. of instructions");
		int i;
		for (i = 0; i < instructions.length; i++) {
			if (instructions[i] == null)
				throw new IllegalArgumentException("Invalid program / uninstantiated array");
			String str = instructions[i];
			if (str.length() != INSTRUCTION_LENGTH)
				throw new IllegalArgumentException("Invalid program.");
			BitString inst = new BitString();
			inst.setBits(str.toCharArray());
			mInstructions[i] = inst;
		}
	}

    /**
     * Sets all entries in the PC, instructions, registers, and memory to 0.
     */
	public void resetProgram() {
        int i;
        mPC = new BitString();
        mPC.setValue(0);
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
	 * Executes the provided instructions, starting at 0 and runs until all instructions 
	 * are executed.
	 */
	public void executeProgram() {
		while (incrementFiveCycles() != null) { }
	}
	
	/**
	 * Another method of executing the program. Manually called from client as 
	 * opposed to automatically running through the instructions.
	 * @return null when all instructions are executed, otherwise a generic placeholder string
	 */
	public String incrementFiveCycles() {
		for (int i = 0; i < 5; i++) {
			if (incrementCycle() == null)
				return null;
		}
		return "running";
	}
	
	private String incrementCycle() {
		if (pipelineOrder.isEmpty() && mPC.getValue() == 0)
			pipelineOrder.push(1);
		int largest = pipelineOrder.peek();
		int smallest = pipelineOrder.peek();
		while (!pipelineOrder.isEmpty()) {
			int currentOp = pipelineOrder.pop();
			smallest = Math.min(smallest, currentOp);
			if (currentOp == IF) {
				instructionFetch();
			} else if (currentOp == ID) {
				instructionDecode();
			} else if (currentOp == EX) {
			    execute();
			} else if (currentOp == MEM) {
				memoryOp();
			} else if (currentOp == WB) {
				writeBack();
			}
		}
		if (mInstructions[mPC.getValue() / 4].getValue() != 0) {
			smallest = IF;
		} else
			smallest++;

		if (largest < WB)
			largest++;

		for (int j = smallest; j <= largest; j++) {
			pipelineOrder.add(j);
		}

		if (pipelineOrder.isEmpty())
			return null;
		else 
			return "running";
	}

    /**
     *  Implementation of the IF stage. Retrieves the current instruction iterates PC to PC + 4.
     *  Sends the IR to the IF/ID pipeline.
     */
	private void instructionFetch() {
		ifIdPipeline[M_IR].setValue(mInstructions[mPC.getValue() / 4].getValue());
		mPC.setValue(mPC.getValue() + 4);
	}

    /**
     * Implementation of the ID stage. Retrieves the op code of the current instruction.
     * Sends the op code and the IR to the ID/EX pipeline.
     */
	private void instructionDecode() {
		BitString opCodeStr = ifIdPipeline[M_IR].getOpCode();
		idExPipeline[OP_CODE] = opCodeStr;
		idExPipeline[M_IR].setValue(ifIdPipeline[M_IR].getValue());
	}

    /**
     * Implementation of the EX stage. Executes the function corresponding to the opcode. Sends the value to be
     * stored, calculated memory address, or target register to the EX/MEM pipeline.
     */
	private void execute() {
		if (idExPipeline[OP_CODE].getValue() == ADD_AND_JR_OP) {
			int func = idExPipeline[M_IR].getFunct().getValue();
			if (func == ADD_FUNC) {
				executeAdd(false);
			} else if (func == AND_FUNC) {
				executeAnd(false);
			} else if (func == JR_FUNC) {
				executeRegJump(idExPipeline[M_IR].getRs());
			} else {
				idExPipeline[M_IR].display(true);
				throw new IllegalArgumentException("Undefined function");

			}
		} else if (idExPipeline[OP_CODE].getValue() == ADDI_OP) {
			executeAdd(true);
		} else if (idExPipeline[OP_CODE].getValue() == ANDI_OP) {
			executeAnd(true);
		} else if (idExPipeline[OP_CODE].getValue() == LW_OP) {
			executeLoadWord(idExPipeline[M_IR].getRs(), idExPipeline[M_IR].getRt(), idExPipeline[M_IR].getCnst());
		} else if (idExPipeline[OP_CODE].getValue() == SW_OP) {
			executeStoreWord(idExPipeline[M_IR].getRs(), idExPipeline[M_IR].getRt(), idExPipeline[M_IR].getCnst());
		} else if (idExPipeline[OP_CODE].getValue() == J_OP) {
			executeJump(idExPipeline[M_IR].getPseudoAddr());
		} else if (idExPipeline[OP_CODE].getValue() == BEQ_OP) {
			executeBeq(idExPipeline[M_IR].getRs(), idExPipeline[M_IR].getRt(), idExPipeline[M_IR].getCnst());
		} else {
			idExPipeline[M_IR].display(true);
			throw new IllegalArgumentException("Undefined opcode");
		}
		exMemPipeline[M_IR].setValue(idExPipeline[M_IR].getValue());
	}

    /**
     * Implementation of the MEM operation. Retrieves from or writes to memory. Sends the value to be written as
     * well as the target register to the MEM/WB pipeline.
     */
	private void memoryOp() {
		if (OP_CODE == LW_OP) {
			memWbPipeline[WRITE_VAL].setValue2sComp(mMemory[exMemPipeline[MEMORY_TARGET].getValue()].getValue2sComp());
			memWbPipeline[REGISTER_TARGET].setValue(exMemPipeline[REGISTER_TARGET].getValue());
		} else if (OP_CODE == SW_OP) {
			mMemory[exMemPipeline[MEMORY_TARGET].getValue()].setValue2sComp(exMemPipeline[WRITE_VAL].getValue2sComp());
		} else {
			memWbPipeline[WRITE_VAL].setValue2sComp(exMemPipeline[WRITE_VAL].getValue2sComp());
			memWbPipeline[REGISTER_TARGET].setValue2sComp(exMemPipeline[REGISTER_TARGET].getValue2sComp());
		}
		memWbPipeline[M_IR].setValue(exMemPipeline[M_IR].getValue());
	}

    /**
     * Implementation of the WB operation. If necessary (target reg > -1), stores the calculated value in the
     * register.
     */
	private void writeBack() {
		if (memWbPipeline[REGISTER_TARGET].getValue() >= 0) {
			mRegisters[memWbPipeline[REGISTER_TARGET].getValue()]
					.setValue2sComp(memWbPipeline[WRITE_VAL].getValue2sComp());
			ifIdPipeline[REGISTER_TARGET].setValue2sComp(-1);
		}
	}
	
	/**
	 * Executes the ADD/ADDI instructions. Distinguishes between the two and performs 
	 * the respective operations
	 * @param imm if true, executes ADDI, otherwise executes ADD
	 * @throws IAG if an overflow exception occurs or the target register is $zero
	 */
	private void executeAdd(boolean imm) {
		BitString rS = idExPipeline[M_IR].getRs();
		int val;
		if (rS.equals(exMemPipeline[REGISTER_TARGET]))
			val = exMemPipeline[WRITE_VAL].getValue2sComp();
		else
			val = mRegisters[rS.getValue()].getValue2sComp();

		BitString target;
		int ans;
		if (imm) {
			target = idExPipeline[M_IR].getRt();
			int cnst = idExPipeline[M_IR].getCnst().getValue2sComp();
			if (checkOverflow(cnst, val)) {
				System.out.println("Overflow exception: ");
				idExPipeline[M_IR].display(true);
				throw new IllegalArgumentException("Overflow exception");
			}
			ans = val + cnst;
		} else {
			int rtVal;
			if (exMemPipeline[REGISTER_TARGET].equals(idExPipeline[M_IR].getRt()))
				rtVal = exMemPipeline[WRITE_VAL].getValue2sComp();
			else
				rtVal = mRegisters[idExPipeline[M_IR].getRt().getValue()].getValue2sComp();
			target = idExPipeline[M_IR].getRd();
			if (checkOverflow(val, rtVal)) {
				idExPipeline[M_IR].display(true);
				throw new IllegalArgumentException("Overflow exception");
			}
			ans = val + rtVal;
		}
		int tVal = target.getValue();
		if (tVal == 0) { 
			idExPipeline[M_IR].display(true);
			throw new IllegalArgumentException("Invalid register ($zero)");
		}
		exMemPipeline[WRITE_VAL].setValue2sComp(ans);
		exMemPipeline[REGISTER_TARGET].setValue(tVal);
	}
	
	/**
	 * Executes the AND/ANDI instructions. Distinguishes between the two and performs 
	 * the respective operations
	 * @param imm if true, executes ANDI, otherwise executes AND
	 * @throws IAG if the target register is $zero
	 */
	private void executeAnd(boolean imm) {
		BitString rS = idExPipeline[M_IR].getRs();
		int val;
		if (rS.equals(exMemPipeline[REGISTER_TARGET]))
			val = exMemPipeline[WRITE_VAL].getValue2sComp();
		else
			val = mRegisters[rS.getValue()].getValue2sComp();

		BitString target;
		int ans;
		if (imm) {
			target = idExPipeline[M_IR].getRt();
			int cnst = idExPipeline[M_IR].getCnst().getValue2sComp();
			ans = val & cnst;
		} else {
			int rtVal;
			if (exMemPipeline[REGISTER_TARGET].equals(idExPipeline[M_IR].getRt()))
				rtVal = exMemPipeline[WRITE_VAL].getValue2sComp();
			else
				rtVal = mRegisters[idExPipeline[M_IR].getRt().getValue()].getValue2sComp();
			target = idExPipeline[M_IR].getRd();
			ans = val & rtVal;
		}
		int tVal = target.getValue();
		if (tVal <= 0) { 
			idExPipeline[M_IR].display(true);
			throw new IllegalArgumentException("Invalid register ($zero)");
		}
		exMemPipeline[WRITE_VAL].setValue2sComp(ans);
		exMemPipeline[REGISTER_TARGET].setValue(tVal);
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
			idExPipeline[M_IR].display(true);
			throw new IllegalArgumentException("Out of bounds jump target at instruction");
		}
		mPC.setValue(newPC.getValue());
	}
	
	/**
	 * Executes the JR instruction. Updates the PC to the stored address in the register.
	 * @param register
	 * @throws IAG when the stored address is not a multiple of 4.
	 */
	private void executeRegJump(BitString register) {
		BitString newAddr = new BitString();
		if (exMemPipeline[REGISTER_TARGET].equals(register.getValue()))
			newAddr.setValue(exMemPipeline[WRITE_VAL].getValue());
		else
			newAddr.setValue(register.getValue());
		if (newAddr.getValue2sComp() % 4 != 0) {
			idExPipeline[M_IR].display(true);
			throw new IllegalArgumentException("Address error exception, not aligned.");
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
			if (val1 + val2 < 0)
				overflow = true;
		} else if (val1 < 0 && val2 < 0) {
			if (val1 + val2 > 0)
				overflow = true;
		}
		return overflow;
	}

	private void validateMemory(int addrIndex) {
        if (addrIndex % 4 != 0) {
            throw new IllegalArgumentException("Address error exception, not aligned.");
        }
        if (addrIndex >= MAX_MEMORY || addrIndex < 0) {
            throw new IllegalArgumentException("Memory address exceeds limit.");
        }
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
			idExPipeline[M_IR].display(true);
			throw new IllegalArgumentException("Cannot write to 0 register @LW");
		}
		BitString addr1 = new BitString();
		if (rS.equals(exMemPipeline[REGISTER_TARGET]))
			addr1.setValue(exMemPipeline[WRITE_VAL].getValue());
		else
			addr1 = mRegisters[rS.getValue()];

		BitString combined = new BitString();
		int off = offset.getValue2sComp();
		int addr = addr1.getValue2sComp();
		if (checkOverflow(off, addr)) { 
			idExPipeline[M_IR].display(true);
			throw new IllegalArgumentException("Overflow exception");
		}
		int sum = off + addr;
		combined.setValue2sComp(sum);
		int addrIndex = combined.getValue();
		validateMemory(addrIndex);

		int register = rT.getValue();
		exMemPipeline[REGISTER_TARGET].setValue(register);
		exMemPipeline[MEMORY_TARGET].setValue(addrIndex);
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
		int regVal;
		if (rS.equals(exMemPipeline[REGISTER_TARGET]))
			regVal = exMemPipeline[WRITE_VAL].getValue2sComp();
		else
			regVal = mRegisters[rS.getValue()].getValue2sComp();

		int off = offset.getValue2sComp();
		if (checkOverflow(off, regVal)) { 
			idExPipeline[M_IR].display(true);
			throw new IllegalArgumentException("Overflow Exception");
		}
		int addr = regVal + off;
		validateMemory(addr);

		exMemPipeline[WRITE_VAL].setValue2sComp(mRegisters[rT.getValue()].getValue2sComp());
		exMemPipeline[MEMORY_TARGET].setValue(addr);
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
				idExPipeline[M_IR].display(true);
				throw new IllegalArgumentException("Out of bounds register");
			}
			mPC.setValue(newAddr);
		}
	}

	public BitString[] getRegisterContents() {
		return mRegisters;
	}

	public BitString[] getMemoryContents() {
		return mMemory;
	}
}
