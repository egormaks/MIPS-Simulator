package gui;

import java.awt.*;

import javax.swing.*;
import javax.swing.border.Border;

import simulator.Computer;



public class Driver extends JFrame  {
	private static final long serialVersionUID = 1L;
	/* Default columns for register and memory data tables, can be changed w/ future improvements. */
	private static final String[] COLUMN_NAMES = {"Location", "Hex. Value", "Dec. Value"};
	private static final int DEFAULT_WIDTH = 750;
	private static final int DEFAULT_HEIGHT = 600;

	private JTable regMemory;
	private JTable dataMemory;
	
	/*
	 * Initializes the primary window. 
	 */
	public Driver() {
		this.setTitle("MIPS Simulator");
		this.setLayout(new GridLayout(1, 3, 5, 0));
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setPreferredSize(new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));

		this.regMemory = new JTable(new String[Computer.MAX_REGISTERS][COLUMN_NAMES.length], COLUMN_NAMES);
		this.regMemory.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		this.dataMemory = new JTable(new String[Computer.MAX_MEMORY][COLUMN_NAMES.length], COLUMN_NAMES);
		this.regMemory.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		
		for (int i = 0; i < Computer.MAX_REGISTERS; i++) { 
			regMemory.setValueAt("Reg. " + i, i, 0);
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
	}

	private JPanel generateProgramPanel() {
		JPanel programPanel = new JPanel();
		JScrollPane program = new JScrollPane(new JTextArea());
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
		JButton step = new JButton("Step");
		JButton run = new JButton("Run");

		JPanel buttons = new JPanel();
		buttons.add(compile, BorderLayout.EAST);
		buttons.add(step, BorderLayout.CENTER);
		buttons.add(run, BorderLayout.WEST);

		return buttons;
	}
	
	public static void main(String[] args) { 
		Driver driver = new Driver();
		driver.setVisible(true);
		
		while(true) { }
	}
}
                                                                                                                         