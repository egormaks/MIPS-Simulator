package gui;

import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;

import simulator.Computer;



public class Driver extends JFrame {
	private static final long serialVersionUID = 1L;
	/* Default columns for register and memory data tables, can be changed w/ future improvements. */
	private static final String[] COLUMN_NAMES = {"Location", "Hex. Value", "Dec. Value"};
	
	private JTable regMemory;
	private JTable dataMemory;
	
	/*
	 * Initializes the primary window. 
	 */
	public Driver() { 
		this.regMemory = new JTable(new String[Computer.MAX_REGISTERS][COLUMN_NAMES.length], COLUMN_NAMES);
		this.regMemory.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		this.dataMemory = new JTable(new String[Computer.MAX_MEMORY][COLUMN_NAMES.length], COLUMN_NAMES);
		this.regMemory.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		
		
		for (int i = 0; i < Computer.MAX_REGISTERS; i++) { 
			regMemory.setValueAt("Register " + Integer.toString(i), i, 0);
		}
		
		for (int i = 0; i < Computer.MAX_MEMORY; i++) {
			dataMemory.setValueAt(Integer.toString(i), i, 0);
		}
		
		JScrollPane reg = new JScrollPane(this.regMemory);
		JScrollPane mem = new JScrollPane(this.dataMemory);
		JScrollPane program = new JScrollPane(new JTextArea());
		
		this.add(program);
		this.add(reg);
		this.add(mem);
		this.setPreferredSize(new Dimension(600, 600));
		this.setLayout(new GridLayout(2, 3, 30, 0));
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	public static void main(String[] args) { 
		Driver driver = new Driver();
		driver.setVisible(true);
		
		while(true) { }
	}
}
                                                                                                                         