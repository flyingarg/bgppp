package com.bgppp.gui;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.ImageIcon;
import javax.swing.JTextPane;
import javax.swing.JTextField;
import javax.swing.JLabel;

import java.awt.Graphics;
import java.awt.Font;

import javax.swing.SwingConstants;

import java.awt.Color;

import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JFormattedTextField;
import javax.swing.JButton;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.HashMap;
import java.util.Map;








//import com.bgppp.gui.ObjectMapper;
import org.codehaus.jackson.map.ObjectMapper;

import javax.swing.Icon;


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
	private final JButton btnGo = new JButton("Go!");
	public static ImageIcon image = new ImageIcon("C:\\MIKLE MY DATA\\Misc\\Eclipse\\workspace\\BGP\\bgppp\\router-image.png");
	private final static JLabel lblNewLabel = new JLabel((Icon) null);
	private final static JLabel label = new JLabel((Icon) null);
	private final static JLabel label_1 = new JLabel((Icon) null);
	private final static JLabel label_2 = new JLabel((Icon) null);
	private final static JLabel label_3 = new JLabel((Icon) null);

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
		
		btnGo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				 String textIp = formattedTextField.getText();
				 String textSpeed = textField.getText();
				 String textComboBox = comboBox.getSelectedItem().toString();
				txtpnSomeDataFrom.setText(textComboBox + "  " + textIp + "  " + textSpeed);
				transfer (textComboBox, textIp, textSpeed);
				paintRouter (textComboBox);
			}
		});
		btnGo.setBounds(265, 128, 59, 23);
		
		contentPane.add(btnGo);
		lblNewLabel.setForeground(Color.WHITE);
		lblNewLabel.setBackground(Color.WHITE);
		lblNewLabel.setBounds(10, 16, 66, 49);
				
		contentPane.add(lblNewLabel);
		label.setForeground(Color.WHITE);
		label.setBackground(Color.WHITE);
		label.setBounds(32, 89, 66, 49);
		
		contentPane.add(label);
		label_1.setForeground(Color.WHITE);
		label_1.setBackground(Color.WHITE);
		label_1.setBounds(32, 162, 66, 49);
		
		contentPane.add(label_1);
		label_2.setForeground(Color.WHITE);
		label_2.setBackground(Color.WHITE);
		label_2.setBounds(142, 140, 66, 49);
		
		contentPane.add(label_2);
		label_3.setForeground(Color.WHITE);
		label_3.setBackground(Color.WHITE);
		label_3.setBounds(153, 43, 66, 49);
		
		contentPane.add(label_3);
	}
	
	public static void transfer(String textComboBox, String textIp, String textSpeed) {
				
		ObjectMapper mapper = new ObjectMapper();
		Map<String, String> map = new HashMap<String, String>();
		
		map.put("Number", textComboBox);
		map.put("IP", textIp);
		map.put("Speed", textSpeed);
		
		File foo = new File("C:\\MIKLE MY DATA\\example.json");
		
		foo.setExecutable(true);
	    foo.setReadable(true);
	    foo.setWritable(true);
		
		try {
			mapper.writeValue(foo, map);		
		
		} catch (Exception e) {
			e.printStackTrace();
		} 			
	}
	
	
	@SuppressWarnings("null")
	public static void paintRouter (String textComboBox) {
		
		lblNewLabel.setIcon(image);
		label.setIcon(image);
		label_1.setIcon(image);
		label_2.setIcon(image);
		label_3.setIcon(image);
		
		Graphics g = null;
		g.drawLine(43, 65, 65, 138);
			
		/*AppGuiMain frame = new AppGuiMain();
	    frame.setSize(300, 450);
	    frame.setTitle("Picture Test");
	    frame.setLocationRelativeTo(null);
	    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    frame.setVisible(true);*/		

		
	}
}

