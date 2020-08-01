# MIPS-Simulator

# About MIPS
MIPS is an assembly architecture that, like most other architectures, takes instructions represented in binary, and dissects said instructions using hardware in seperate stages in order to execute them. There are 5 stages of an instruction in MIPS: Instruction Decode (ID), Instruction Fetch (IF), Execute (EX), Memory (MEM), and Write Back (WB). 

IF/ID work somewhat in tandem even though they are seperate stages. IF retrieves the current instruction to be executed, and ID dissassembles it and retrieves the necessary information needed to execute it (what instruction it is, what registers to read/write, what memory location to read/write, etc.). EX takes this information and executes whatever operation is needed for the instruction. MEM either reads from memory or writes to memory, depending on the instruction. And finally WB writes the result of the instruction into the appropriate register.

# About the project
This project simulates hardware behavior of the architecture on a higher level of abstraction, the software level. Instructions in this simulation are bit string objects (BitString) that are 32-bits long. Each stage in MIPS has a corresponding method or behavior in the simulator. For example, the simulator will simulate the ID phase by splitting the current instruction into necessary components, and uses those BitString components to deduce what to do next. In that sense, all 5 stages in MIPS have their behavior correctly modelled in Java, down to a bit-by-bit basis. 

The project currently supports the following operations:
  - ADD/ADDI
  - AND/ANDI
  - J/JR
  - BEQ
  - LW/SW

# How to use
When starting the program, a GUI will display. This GUI allows the user to input seperate lines of machine code instructions (32 bits long) into the corresponding field and simulate a compilation. If there are any compile-time errors in the machine code instructions, the simulator will indicate that the input program is invalid. Following this compilation, the user can either step through each instruction manually or run all instructions from the current one automatically. The register and memory contents will be displayed in the GUI and the user will be able to see any changes as they occur with each executed instruction. 

# Current tasks (descending order of priority)
1) Implement instruction-level pipelining. 
   - Since each instruction is executed in terms of 5 stages, seperate instructions can be executed concurrently if they do not share the same resources. One  
     instruction can be in the ID stage and the other can be in the EX stage assuming they do not use the same registers. 
   - This pipelining will be implemented using Java's Thread objects. 
2) Implement additional operations such as MULT/DIV/SUB/etc.
3) Implement additional hardware level exceptions (overflow, etc.)
