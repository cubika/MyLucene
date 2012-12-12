package com.libin.ir;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.text.Document;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;
import javax.swing.JTabbedPane;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.JLabel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import java.awt.Font;
import javax.swing.SwingConstants;
import javax.swing.JButton;
import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;

import javax.swing.JFileChooser;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class LuceneFrame extends JFrame {

	private static final long serialVersionUID = -3153508170604549026L;
	private JPanel contentPane;
	private JTextField queryTextField;
	private JTextField docTextField;
	private JTextField indexTextField;
	
	private File docFile;
	private File indexFile;
	/**
	 * @wbp.nonvisual location=149,597
	 */
	private final JFileChooser docFileChooser = new JFileChooser();
	/**
	 * @wbp.nonvisual location=289,597
	 */
	private final JFileChooser indexFileChooser = new JFileChooser();

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					LuceneFrame frame = new LuceneFrame();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public LuceneFrame() {
		indexFileChooser.setFileSelectionMode(1);
		docFileChooser.setFileSelectionMode(2);
		setTitle("My Lucene System");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);
		setBounds(100, 100, 764, 569);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		contentPane.add(tabbedPane, BorderLayout.CENTER);
		
		JPanel panel = new JPanel();
		tabbedPane.addTab("Create Index", null, panel, null);
		
		JLabel lbl_doc = new JLabel("\u6587\u6863\u8DEF\u5F84\uFF1A");
		lbl_doc.setHorizontalAlignment(SwingConstants.CENTER);
		lbl_doc.setFont(new Font("·ÂËÎ", Font.BOLD, 16));
		
		JLabel lbl_index = new JLabel("\u7D22\u5F15\u8DEF\u5F84\uFF1A");
		lbl_index.setHorizontalAlignment(SwingConstants.CENTER);
		lbl_index.setFont(new Font("·ÂËÎ", Font.BOLD, 16));
		
		docTextField = new JTextField();
		docTextField.setEditable(false);
		docTextField.setColumns(10);
		
		indexTextField = new JTextField();
		indexTextField.setEditable(false);
		indexTextField.setColumns(10);
		
		JButton select_doc_button = new JButton("browse");
		select_doc_button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				int userselection=docFileChooser.showOpenDialog(null);
				if(userselection==JFileChooser.APPROVE_OPTION){
					docFile=docFileChooser.getSelectedFile();
				}
				try {
					docTextField.setText(docFile.getCanonicalPath());
					System.out.println("Choose document file : " + docFile.getCanonicalPath());
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		select_doc_button.setFont(new Font("Consolas", Font.PLAIN, 14));
		
		JButton select_index_button = new JButton("browse");
		select_index_button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int userselection=indexFileChooser.showOpenDialog(null);
				if(userselection==JFileChooser.APPROVE_OPTION){
					indexFile=indexFileChooser.getSelectedFile();
				}
				try {
					indexTextField.setText(indexFile.getCanonicalPath());
					System.out.println("Choose index folder : " + indexFile.getCanonicalPath());
				} catch (IOException ex) {
					ex.printStackTrace();
				}				
			}
		});
		select_index_button.setFont(new Font("Consolas", Font.PLAIN, 14));
		
		final JLabel lblResult = new JLabel("");
		lblResult.setHorizontalAlignment(SwingConstants.CENTER);
		lblResult.setForeground(Color.RED);
		lblResult.setFont(new Font("Consolas", Font.PLAIN, 12));
		
		JButton create_index_button = new JButton("Create Index");
		create_index_button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				lblResult.setText(indexProcess());
			}
		});
		create_index_button.setFont(new Font("Consolas", Font.PLAIN, 14));

		GroupLayout gl_panel = new GroupLayout(panel);
		gl_panel.setHorizontalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel.createSequentialGroup()
					.addGap(280)
					.addComponent(create_index_button)
					.addContainerGap(334, Short.MAX_VALUE))
				.addGroup(Alignment.TRAILING, gl_panel.createSequentialGroup()
					.addGap(96)
					.addGroup(gl_panel.createParallelGroup(Alignment.TRAILING)
						.addComponent(lbl_doc, GroupLayout.DEFAULT_SIZE, 137, Short.MAX_VALUE)
						.addComponent(lbl_index, GroupLayout.DEFAULT_SIZE, 137, Short.MAX_VALUE))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(gl_panel.createParallelGroup(Alignment.LEADING, false)
						.addComponent(lblResult, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addComponent(docTextField, GroupLayout.PREFERRED_SIZE, 230, GroupLayout.PREFERRED_SIZE)
						.addComponent(indexTextField, Alignment.TRAILING, 234, 234, Short.MAX_VALUE))
					.addGap(33)
					.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
						.addComponent(select_doc_button)
						.addComponent(select_index_button))
					.addGap(152))
		);
		gl_panel.setVerticalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel.createSequentialGroup()
					.addGap(117)
					.addGroup(gl_panel.createParallelGroup(Alignment.BASELINE)
						.addComponent(lbl_doc, GroupLayout.PREFERRED_SIZE, 33, GroupLayout.PREFERRED_SIZE)
						.addComponent(docTextField, GroupLayout.PREFERRED_SIZE, 32, GroupLayout.PREFERRED_SIZE)
						.addComponent(select_doc_button, GroupLayout.PREFERRED_SIZE, 31, GroupLayout.PREFERRED_SIZE))
					.addGap(55)
					.addGroup(gl_panel.createParallelGroup(Alignment.BASELINE)
						.addComponent(lbl_index, GroupLayout.PREFERRED_SIZE, 31, GroupLayout.PREFERRED_SIZE)
						.addComponent(indexTextField, GroupLayout.PREFERRED_SIZE, 30, GroupLayout.PREFERRED_SIZE)
						.addComponent(select_index_button, GroupLayout.PREFERRED_SIZE, 30, GroupLayout.PREFERRED_SIZE))
					.addGap(62)
					.addComponent(create_index_button, GroupLayout.PREFERRED_SIZE, 41, GroupLayout.PREFERRED_SIZE)
					.addGap(34)
					.addComponent(lblResult)
					.addContainerGap(112, Short.MAX_VALUE))
		);
		panel.setLayout(gl_panel);
		
		JPanel panel_1 = new JPanel();
		tabbedPane.addTab("Search", null, panel_1, null);
		panel_1.setLayout(new BorderLayout(0, 0));
		
		JPanel search_header_pane = new JPanel();
		panel_1.add(search_header_pane, BorderLayout.NORTH);
		
		queryTextField = new JTextField();
		queryTextField.setColumns(10);
		
		final JLabel lblQueryRequired = new JLabel("");
		lblQueryRequired.setForeground(Color.RED);
		lblQueryRequired.setFont(new Font("Consolas", Font.PLAIN, 12));
		lblQueryRequired.setHorizontalAlignment(SwingConstants.CENTER);
		
		JButton btnSearch = new JButton("Search");
		btnSearch.setFont(new Font("Consolas", Font.PLAIN, 14));
		

		GroupLayout gl_search_header_pane = new GroupLayout(search_header_pane);
		gl_search_header_pane.setHorizontalGroup(
			gl_search_header_pane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_search_header_pane.createSequentialGroup()
					.addGap(164)
					.addGroup(gl_search_header_pane.createParallelGroup(Alignment.LEADING, false)
						.addComponent(lblQueryRequired, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addComponent(queryTextField, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 259, Short.MAX_VALUE))
					.addGap(38)
					.addComponent(btnSearch)
					.addContainerGap(201, Short.MAX_VALUE))
		);
		gl_search_header_pane.setVerticalGroup(
			gl_search_header_pane.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_search_header_pane.createSequentialGroup()
					.addContainerGap(21, Short.MAX_VALUE)
					.addGroup(gl_search_header_pane.createParallelGroup(Alignment.BASELINE)
						.addComponent(queryTextField, GroupLayout.PREFERRED_SIZE, 29, GroupLayout.PREFERRED_SIZE)
						.addComponent(btnSearch))
					.addGap(14)
					.addComponent(lblQueryRequired)
					.addContainerGap())
		);
		search_header_pane.setLayout(gl_search_header_pane);
		
		JPanel search_footer_pane = new JPanel();
		panel_1.add(search_footer_pane, BorderLayout.SOUTH);
		
		final JTextPane textPane = new JTextPane();
		textPane.setFont(new Font("SansSerif", Font.PLAIN, 14));
		textPane.setContentType("text/html");
		textPane.setEditable(false);
		final HTMLEditorKit kit=new HTMLEditorKit();
		textPane.setEditorKit(kit);
		StyleSheet ss=kit.getStyleSheet();
		ss.addRule("span.red {color:red;}");
		ss.addRule("body {font-family:sans-serif,Georgia,·ÂËÎ;}");
		final Document doc = kit.createDefaultDocument();
		doc.putProperty("IgnoreCharsetDirective", new Boolean(true));
		textPane.setDocument(doc);
		
		JScrollPane scrollPane = new JScrollPane(textPane);
		scrollPane.setAutoscrolls(true);
		panel_1.add(scrollPane, BorderLayout.CENTER);
		
		btnSearch.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				textPane.setText("");
				if(queryTextField.getText().trim().equals("")){
					lblQueryRequired.setText("You must enter a query word!");
				}else{
					try {
						String str=Queryer.search(indexFile, queryTextField.getText());
						kit.read(new StringReader(str), doc, 0);
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}
			}
		});
		
//		File htmlFile=new File("E:/BufferFolder/lucene/docs/demo.html");
//
//		try {
//			kit.read(new FileReader(htmlFile), doc, 0);
//		} catch (IOException | BadLocationException e) {
//			e.printStackTrace();
//		}
	}
	
	private String indexProcess(){
		if(docFile==null) return "Please Choose a document file first!";
		if(indexFile==null) return "Please Choose index folder first!";
		try {
			int ret=Indexer.index(docFile, indexFile);
			if(ret>0) return "Create index file success!";
			else return "Create index failed!";
		} catch (IOException e) {
			e.printStackTrace();
			return "Exception occurred during creating index!";
		}
	}
}
