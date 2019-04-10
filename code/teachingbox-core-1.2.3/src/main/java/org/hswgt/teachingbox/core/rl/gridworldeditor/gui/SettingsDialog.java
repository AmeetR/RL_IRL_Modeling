package org.hswgt.teachingbox.core.rl.gridworldeditor.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.io.Serializable;

import javax.swing.AbstractListModel;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.TitledBorder;
import javax.swing.colorchooser.DefaultColorSelectionModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * Dialog within the settings of font, color of the active cell and the size of one cell in the grid table.s
 */
public class SettingsDialog extends JDialog implements ChangeListener, Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 9158343169163573965L;
	/** A return status code - returned if Cancel button has been pressed */
	public static final int RET_CANCEL = 0;
	/** A return status code - returned if OK button has been pressed */
	public static final int RET_OK = 1;
	// the font
	private Font font;
	private Color color;
	

	private JPanel fontPanel;
	private JScrollPane jScrollPane1;
	private JLabel jLabel1;
	private JLabel jLabel3;
	private JLabel jLabel2;
	private JList lstSize;
	private JButton okButton;
	private JList lstFont;
	private JScrollPane jScrollPane2;
	private JList lstStyle;
	private JPanel mainPanel;
	private JButton cancelButton;
	private JPanel previewPanel;
	private JLabel lblPreview;
	private JPanel buttonPanel;
	private JScrollPane jScrollPane3;
	private JPanel tablePanel;
	private JPanel colorPanel;
	private JColorChooser tcc;
	private JTextField cellHeight, cellWidth;
	private int returnStatus = RET_CANCEL;

	/**
	 * Constructor
	 * @param parent GridWorldGUI
	 */
	public SettingsDialog(GridWorldGUI parent) {
		super(parent, true);
		this.font = new Font("Dialog", Font.PLAIN, 12);
		initComponents();
		lblPreview.setFont(font);
		tcc.setColor(GridWorldPropertyManager.getColor());
		cellWidth.setText(Integer.toString(parent.getTablePanel().getTable().getColSize()));
		cellHeight.setText(Integer.toString(parent.getTablePanel().getTable().getRowSize()));
	}

	/**
	 * build the dialog
	 */
	private void initComponents() {
		java.awt.GridBagConstraints gridBagConstraints;

		mainPanel = new JPanel();
		fontPanel = new JPanel();
		jLabel1 = new JLabel();
		jLabel2 = new JLabel();
		jLabel3 = new JLabel();
		jScrollPane1 = new JScrollPane();
		lstFont = new JList(GraphicsEnvironment.getLocalGraphicsEnvironment()
				.getAvailableFontFamilyNames());
		jScrollPane2 = new JScrollPane();
		lstStyle = new JList();
		jScrollPane3 = new JScrollPane();
		lstSize = new JList();
		previewPanel = new JPanel();
		tablePanel = new JPanel();
		colorPanel = new JPanel();
		lblPreview = new JLabel();
		buttonPanel = new JPanel();
		okButton = new JButton();
		cancelButton = new JButton();
		cellHeight = new JTextField();
		cellWidth = new JTextField();
		
		setTitle("Font and Color Settings");
		setModal(true);
		setResizable(false);
		addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(WindowEvent evt) {
				closeDialog(evt);
			}
		});

		mainPanel.setLayout(new GridLayout(2, 2));

		fontPanel.setLayout(new GridBagLayout());

		jLabel1.setText("Font");
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.insets = new Insets(1, 1, 1, 1);
		gridBagConstraints.weightx = 2.0;
		fontPanel.add(jLabel1, gridBagConstraints);

		jLabel2.setText("Style");
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.insets = new Insets(1, 1, 1, 1);
		fontPanel.add(jLabel2, gridBagConstraints);

		jLabel3.setText("Size");
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.insets = new Insets(1, 1, 1, 1);
		gridBagConstraints.weightx = 0.2;
		fontPanel.add(jLabel3, gridBagConstraints);

		lstFont.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		lstFont.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent evt) {
				lstFontValueChanged();
			}
		});

		jScrollPane1.setViewportView(lstFont);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.ipadx = 1;
		gridBagConstraints.insets = new Insets(1, 1, 1, 1);
		gridBagConstraints.weightx = 2.0;
		fontPanel.add(jScrollPane1, gridBagConstraints);

		lstStyle.setModel(new AbstractListModel() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			String[] strings = { "Plain", "Bold", "Italic", "Bold Italic" };

			public int getSize() {
				return strings.length;
			}

			public Object getElementAt(int i) {
				return strings[i];
			}
		});
		lstStyle.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		lstStyle.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent evt) {
				lstStyleValueChanged();
			}
		});

		jScrollPane2.setViewportView(lstStyle);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.ipadx = 1;
		gridBagConstraints.insets = new Insets(1, 1, 1, 1);
		fontPanel.add(jScrollPane2, gridBagConstraints);

		lstSize.setModel(new AbstractListModel() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			String[] strings = { "2", "4", "6", "8", "10", "11", "12", "14",
					"16", "20", "24", "28", "36", "48", "72", "96" };

			public int getSize() {
				return strings.length;
			}

			public Object getElementAt(int i) {
				return strings[i];
			}
		});
		lstSize.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		lstSize.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent evt) {
				lstSizeValueChanged();
			}
		});

		jScrollPane3.setViewportView(lstSize);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 2;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.ipadx = 1;
		gridBagConstraints.insets = new Insets(1, 1, 1, 1);
		gridBagConstraints.weightx = 0.2;
		fontPanel.add(jScrollPane3, gridBagConstraints);

		mainPanel.add(fontPanel);
		
		previewPanel.setLayout(new BorderLayout());

		previewPanel.setBorder(new TitledBorder(null, "Preview",
				TitledBorder.DEFAULT_JUSTIFICATION,
				TitledBorder.DEFAULT_POSITION, new Font("Dialog", 0, 12)));
		lblPreview.setFont(new Font("Dialog", 0, 12));
		lblPreview.setText("ABCDEFG abcdefg");
		previewPanel.setPreferredSize(new Dimension(50,50));
		previewPanel.setMaximumSize(new Dimension(50,50));
		previewPanel.setMinimumSize(new Dimension(50,50));
		previewPanel.add(lblPreview, BorderLayout.CENTER);

		mainPanel.add(previewPanel);
		
		colorPanel.setLayout(new BorderLayout());
		colorPanel.setBorder(new TitledBorder(null, "Active Cell Color",
				TitledBorder.DEFAULT_JUSTIFICATION,
				TitledBorder.DEFAULT_POSITION, new Font("Dialog", 0, 12)));
		tcc = new JColorChooser();
		tcc.setPreferredSize(new Dimension(200,250));
		tcc.getSelectionModel().addChangeListener(this);

		colorPanel.add(tcc, BorderLayout.CENTER);
		mainPanel.add(colorPanel);
		
		tablePanel.setLayout(new GridBagLayout());
		
		tablePanel.setBorder(new TitledBorder(null, "Table Settings",
				TitledBorder.DEFAULT_JUSTIFICATION,
				TitledBorder.DEFAULT_POSITION, new Font("Dialog", 0, 12)));
		
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.ipadx = 1;
		gridBagConstraints.insets = new Insets(1, 1, 1, 1);
		gridBagConstraints.weightx = 0.2;

		
		tablePanel.add(new JLabel("Cell Height:"), gridBagConstraints);
		
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.ipadx = 1;
		gridBagConstraints.insets = new Insets(1, 1, 1, 1);
		gridBagConstraints.weightx = 0.2;

		cellHeight.setMinimumSize(new Dimension(50,20));
		cellHeight.setPreferredSize(new Dimension(50,20));
		tablePanel.add(cellHeight, gridBagConstraints);
		
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.ipadx = 1;
		gridBagConstraints.insets = new Insets(1, 1, 1, 1);
		gridBagConstraints.weightx = 0.2;

		
		tablePanel.add(new JLabel("Cell Width:"), gridBagConstraints);
		
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
	
		
		tablePanel.add(cellWidth, gridBagConstraints);
		
		
		mainPanel.add(tablePanel);
		
		getContentPane().add(mainPanel, BorderLayout.CENTER);

		buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));

		okButton.setText("OK");
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				okButtonActionPerformed();
			}
		});

		buttonPanel.add(okButton);

		cancelButton.setText("Cancel");
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				cancelButtonActionPerformed();
			}
		});

		buttonPanel.add(cancelButton);

		getContentPane().add(buttonPanel, BorderLayout.SOUTH);

		pack();
		java.awt.Dimension screenSize = Toolkit.getDefaultToolkit()
				.getScreenSize();
		setSize(new java.awt.Dimension(443, 429));
		setLocation((screenSize.width - 443) / 2, (screenSize.height - 429) / 2);
	}

	/**
	 * Close the Dialog
	 * @param evt WindowEvent
	 */
	private void closeDialog(WindowEvent evt) {
		doClose(RET_CANCEL);
	}

	/**
	 * @param retStatus
	 */
	private void doClose(int retStatus) {
		returnStatus = retStatus;
		setVisible(false);
	}

	/**
	 * Change the font style in the preview panel
	 */
	private void lstStyleValueChanged() {
		int style = -1;
		String selStyle = (String) lstStyle.getSelectedValue();
		if (selStyle == "Plain")
			style = Font.PLAIN;
		if (selStyle == "Bold")
			style = Font.BOLD;
		if (selStyle == "Italic")
			style = Font.ITALIC;
		if (selStyle == "Bold Italic")
			style = Font.BOLD + Font.ITALIC;

		font = new Font(font.getFamily(), style, font.getSize());
		lblPreview.setFont(font);
	}

	/**
	 * Change the font size in the preview panel
	 * @param evt
	 */
	private void lstSizeValueChanged() {
		int size = Integer.parseInt((String) lstSize.getSelectedValue());
		font = new Font(font.getFamily(), font.getStyle(), size);
		lblPreview.setFont(font);
	}

	/**
	 * Change the font value in the preview panel
	 */
	private void lstFontValueChanged() {
		font = new Font((String) lstFont.getSelectedValue(), font.getStyle(),
				font.getSize());
		lblPreview.setFont(font);
	}

	/**
	 * action if ok button is pressed
	 */
	private void okButtonActionPerformed() {
		doClose(RET_OK);
	}

	/**
	 * action if cancel button is pressed
	 */
	private void cancelButtonActionPerformed() {
		doClose(RET_CANCEL);
	}

	/** @return the return status of this dialog - one of RET_OK or RET_CANCEL */
	public int getReturnStatus() {
		return returnStatus;
	}

	/** @return the font chosen by the user */
	public Font getFont() {
		return font;
	}
	/**
	 * @return the color chosen by the user
	 */
	public Color getColor() {
		return color;
	}

	/* (non-Javadoc)
	 * @see javax.swing.event.ChangeListener#stateChanged(javax.swing.event.ChangeEvent)
	 */
	public void stateChanged(ChangeEvent arg0) {
		DefaultColorSelectionModel cc = (DefaultColorSelectionModel)arg0.getSource();
		this.color = cc.getSelectedColor();
	}

	public int getCellHeight() {
		return Integer.parseInt(cellHeight.getText());
	}

	public int getCellWidth() {
		return Integer.parseInt(cellWidth.getText());
	}
}
