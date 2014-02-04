package com.bgppp.gui;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JSplitPane;
import javax.swing.JTextPane;
import javax.swing.JTextField;
import javax.swing.JTextArea;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.SwingConstants;
import java.awt.Component;
import javax.swing.Box;
import java.awt.Dimension;
import java.awt.Color;
import javax.swing.border.LineBorder;
import javax.swing.JList;
import javax.swing.ListSelectionModel;
import javax.swing.border.BevelBorder;
import java.awt.Choice;
import javax.swing.AbstractListModel;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JFormattedTextField;
import java.awt.Canvas;


public class AppGuiMain extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private final JTextPane txtpnSomeDataFrom = new JTextPane();
	private final JLabel lblConsole = new JLabel("Console");
	private final JLabel lblOptions = new JLabel("Options");
	private final JComboBox comboBox = new JComboBox();
	private final JLabel lblNumberOfRouters = new JLabel("Number of routers");
	private final JFormattedTextField formattedTextField = new JFormattedTextField();
	private final JLabel lblIpAddress = new JLabel("IP address");
	private final JTextField textField = new JTextField();
	private final JLabel lblSpeed = new JLabel("Speed");
	private final Canvas canvas = new Canvas();

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					AppGuiMain frame = new AppGuiMain();
					frame.start();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	public void start() {
		   setVisible(true);
		}


	/**
	 * Create the frame.
	 */
	public AppGuiMain() {
		textField.setHorizontalAlignment(SwingConstants.CENTER);
		textField.setText("12");
		textField.setBounds(362, 103, 37, 23);
		initGUI();
	}
	
	private void initGUI() {
		lblNumberOfRouters.setLabelFor(comboBox);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		txtpnSomeDataFrom.setEditable(false);
		txtpnSomeDataFrom.setText("Some data from console");
		txtpnSomeDataFrom.setBounds(300, 162, 124, 88);
		
		contentPane.add(txtpnSomeDataFrom);
		lblConsole.setHorizontalAlignment(SwingConstants.CENTER);
		lblConsole.setFont(new Font("Tahoma", Font.PLAIN, 18));
		lblConsole.setBounds(300, 137, 124, 14);
		
		contentPane.add(lblConsole);
		lblOptions.setHorizontalAlignment(SwingConstants.CENTER);
		lblOptions.setFont(new Font("Tahoma", Font.PLAIN, 18));
		lblOptions.setBounds(300, 11, 124, 22);
		
		contentPane.add(lblOptions);
		comboBox.setToolTipText("");
		comboBox.setModel(new DefaultComboBoxModel(new String[] {"1", "2", "3", "4", "5"}));
		comboBox.setSelectedIndex(0);
		comboBox.setBounds(380, 43, 37, 22);
		
		contentPane.add(comboBox);
		lblNumberOfRouters.setHorizontalAlignment(SwingConstants.CENTER);
		lblNumberOfRouters.setBounds(265, 40, 111, 30);
		
		contentPane.add(lblNumberOfRouters);
		formattedTextField.setText("1 2 3 4");
		formattedTextField.setHorizontalAlignment(SwingConstants.CENTER);
		formattedTextField.setBounds(352, 74, 72, 22);
		
		contentPane.add(formattedTextField);
		lblIpAddress.setBounds(285, 78, 61, 14);
		
		contentPane.add(lblIpAddress);
		
		contentPane.add(textField);
		lblSpeed.setHorizontalAlignment(SwingConstants.CENTER);
		lblSpeed.setBounds(300, 103, 46, 14);
		
		contentPane.add(lblSpeed);
		canvas.setBackground(Color.GRAY);
		canvas.setForeground(Color.WHITE);
		canvas.setBounds(10, 17, 249, 233);
		
		contentPane.add(canvas);
	}
}
