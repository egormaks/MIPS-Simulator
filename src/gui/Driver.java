package gui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import simulator.BitString;
import simulator.Computer;



public class Driver extends JFrame  {
	private static final long serialVersionUID = 1L;
	/* Default columns for register and memory data tables, can be changed w/ future improvements. */
	private static final String[] COLUMN_NAMES = {"Location", "Hex. Value", "Dec. Value"};
	private static final int DEFAULT_WIDTH = 750;
	private static final int DEFAULT_HEIGHT = 600;

	private Computer computer;
	private JTable regMemory;
	private JTable dataMemory;
	private JTextArea machineCode;

	public static void main(String[] args) {
		Driver driver = new Driver();
		driver.setVisible(true);

		while(true) { }
	}
	
	/*
	 * Initializes the primary window. 
	 */
	public Driver() {
		this.computer = new Computer();
		this.machineCode = new JTextArea();

		this.setTitle("MIPS Simulator");
		this.setLayout(new GridLayout(1, 3, 5, 0));
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setPreferredSize(new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));

		this.regMemory = new JTable(new String[Computer.MAX_REGISTERS][COLUMN_NAMES.length], COLUMN_NAMES);
		this.regMemory.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		this.dataMemory = new JTable(new String[Computer.MAX_MEMORY][COLUMN_NAMES.length], COLUMN_NAMES);
		this.regMemory.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		
		for (int i = 0; i < Computer.MAX_REGISTERS; i++) {
			if (i == 0) {
				regMemory.setValueAt("$zero ($0)", i, 0);
			} else if (i == 1) {
				regMemory.setValueAt("$at ($1)", i, 0);
			} else if (i == 2 || i == 3) {
				regMemory.setValueAt("$v" + (i - 2) + " ($" + i + ")", i, 0);
			} else if (i >= 4 && i <= 7) {
				regMemory.setValueAt("$a" + (i - 4) + " ($" + i + ")", i, 0);
			} else if (i >= 8 && i <= 15) {
				regMemory.setValueAt("$t" + (i - 8) + " ($" + i + ")", i, 0);
			} else if (i >= 16 && i <= 23) {
				regMemory.setValueAt("$s" + (i - 16) + " ($" + i + ")", i, 0);
			} else if (i == 24 || i == 25) {
				regMemory.setValueAt("$t" + (i - 16) + " ($" + i + ")", i, 0);
			} else if (i == 26 || i == 27) {
				regMemory.setValueAt("$k" + (i - 26) + " ($" + i + ")", i, 0);
			} else if (i == 28) {
				regMemory.setValueAt("$gp ($28)", i, 0);
			} else if (i == 29) {
				regMemory.setValueAt("$sp ($29)", i, 0);
			} else if (i == 30) {
				regMemory.setValueAt("$fp ($30)", i, 0);
			} else {
				regMemory.setValueAt("$ra ($31)", i, 0);
			}
		}
		
		for (int i = 0; i < Computer.MAX_MEMORY; i++) {
			dataMemory.setValueAt(Integer.toString(i), i, 0);
		}

		regMemory.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		dataMemory.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

		this.add(generateProgramPanel());
		this.add(generateRegisterPanel());
		this.add(generateMemoryPanel());

		this.pack();
		updateMemoryTable();
		updateRegisterTable();
	}

	private JPanel generateProgramPanel() {
		JPanel programPanel = new JPanel();
		JScrollPane program = new JScrollPane(machineCode);
		JLabel titleProg = new JLabel("Machine Code Input");

		titleProg.setAlignmentX(JLabel.CENTER_ALIGNMENT);
		programPanel.setLayout(new BoxLayout(programPanel, BoxLayout.PAGE_AXIS));
		programPanel.add(titleProg);
		programPanel.add(program, BorderLayout.CENTER);
		programPanel.add(genProgramButtons(), BorderLayout.SOUTH);

		return programPanel;
	}

	private JPanel generateRegisterPanel() {
		JPanel registerPanel = new JPanel();
		JScrollPane reg = new JScrollPane(this.regMemory);
		JLabel titleReg = new JLabel("Register Contents");

		reg.setPreferredSize(regMemory.getSize());
		titleReg.setAlignmentX(JLabel.CENTER_ALIGNMENT);
		registerPanel.setLayout(new BoxLayout(registerPanel, BoxLayout.PAGE_AXIS));
		registerPanel.add(titleReg);
		registerPanel.add(reg);

		return registerPanel;
	}

	private JPanel generateMemoryPanel() {
		JPanel memPanel = new JPanel();
		JScrollPane mem = new JScrollPane(this.dataMemory);
		JLabel titleMem = new JLabel("Memory Contents");

		mem.setPreferredSize(dataMemory.getSize());
		titleMem.setAlignmentX(JLabel.CENTER_ALIGNMENT);
		memPanel.setLayout(new BoxLayout(memPanel, BoxLayout.PAGE_AXIS));
		memPanel.add(titleMem);
		memPanel.add(mem);

		return memPanel;
	}

	private JPanel genProgramButtons() {
		JButton compile = new JButton("Compile");
		compile.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					String program = machineCode.getText();
					String[] processedCode = program.split("\n");
					computer.loadProgram(processedCode);
					updateMemoryTable();
					updateRegisterTable();
				} catch (IllegalArgumentException ex) {
					JOptionPane.showMessageDialog(Driver.this, ex.getMessage());
				}
			}
		});
		JButton step = new JButton("Step (1 Cycle)");
		step.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					String str = computer.incrementFiveCycles();
					if (str == null) {
						JOptionPane.showMessageDialog(Driver.this,
								"Program finished execution.");
					}
					updateMemoryTable();
					updateRegisterTable();
				} catch (Exception ex) {
					JOptionPane.showMessageDialog(Driver.this, ex.getMessage());
				}
			}
		});
		JButton run = new JButton("Run");
		run.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					computer.executeProgram();
					JOptionPane.showMessageDialog(Driver.this,
								"Program finished execution.");
					updateMemoryTable();
					updateRegisterTable();
				} catch (Exception ex) {
					JOptionPane.showMessageDialog(Driver.this, ex.getMessage());
				}
			}
		});
		JButton reset = new JButton("Reset");
		reset.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				computer.resetProgram();
				updateRegisterTable();
				updateMemoryTable();
			}
		});
		JPanel buttons = new JPanel();
		buttons.add(compile, BorderLayout.EAST);
		buttons.add(step, BorderLayout.CENTER);
		buttons.add(run, BorderLayout.WEST);
		buttons.add(reset, BorderLayout.SOUTH);

		return buttons;
	}

	private void updateRegisterTable() {
		BitString[] regContents = computer.getRegisterContents();
		for (int i = 0; i < regContents.length; i++) {
			regMemory.setValueAt(regContents[i].getHex(), i, 1);
			regMemory.setValueAt(Integer.toString(regContents[i].getValue2sComp()), i, 2);
		}
	}

	private void updateMemoryTable() {
		BitString[] memContents = computer.getMemoryContents();
		for (int i = 0; i < memContents.length; i++) {
			dataMemory.setValueAt(memContents[i].getHex(), i, 1);
			dataMemory.setValueAt(Integer.toString(memContents[i].getValue2sComp()), i, 2);
		}
	}
}
                                                                                                                         