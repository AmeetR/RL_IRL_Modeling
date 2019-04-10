package org.hswgt.teachingbox.core.rl.gridworldeditor.gui;

import java.awt.Color;
import java.awt.Toolkit;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

/**
 * A user-defined Textfield for the Reward-Toolbar. It validate the input and the length of the Field. 
 * Furthermore an own texttip is set. 
 */
public class CTextField extends JTextField implements FocusListener, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6418176177479683138L;
	public final static int DEFAULT_LIMIT = 1000;
	private final static Color FOCUS_BACKGROUND_COLOR = new Color(224, 254, 173);
	private final static Color INATIVE_BACKGROUND_COLOR = UIManager
			.getColor("TextField.inactiveBackground");
	private final static Color INATIVE_FORGROUND_COLOR = UIManager
			.getColor("TextField.inactiveForeground");
	private final static Color BACKGROUND_COLOR = UIManager
			.getColor("TextField.background");
	private final static Color FORGROUND_COLOR = UIManager
			.getColor("TextField.forground");

	private String texttip;
	private int limit;

	private TextDocument document;

	/**
	 * Constructor
	 */
	public CTextField() {
		this("", 0, DEFAULT_LIMIT);
	}

	/**
	 * Constructor
	 * 
	 * @param columns
	 *            Number of columns in the textfield
	 */
	public CTextField(int columns) {
		this("", columns, DEFAULT_LIMIT);
	}

	/**
	 * Constructor
	 * 
	 * @param text
	 *            Default text of the textfield
	 */
	public CTextField(String text) {
		this(text, 0, DEFAULT_LIMIT);
	}

	/**
	 * Constructor
	 * 
	 * @param text
	 *            Default text of the textfield
	 * @param columns
	 *            Number of columns in the textfield
	 */
	public CTextField(String text, int columns) {
		this(text, columns, DEFAULT_LIMIT);
	}

	/**
	 * Constructor
	 * 
	 * @param text
	 *            Default text of the textfield
	 * @param columns
	 *            Number of columns in the textfield
	 * @param limit
	 *            Limit of the input-length of the textfield
	 */
	public CTextField(String text, int columns, int limit) {
		super(text, columns);
		this.limit = limit;
		// listener for the insert values
		document = new TextDocument();
		setDocument(document);
		setText(text);
		addFocusListener(this);
		setColumns(columns);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.FocusListener#focusGained(java.awt.event.FocusEvent)
	 */
	public void focusGained(FocusEvent e) {
		setBackground(FOCUS_BACKGROUND_COLOR);
		setForeground(FORGROUND_COLOR);
		if (super.getText().equals(texttip)) {
			setText("");
		}
		checkBackgroundColor();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.FocusListener#focusLost(java.awt.event.FocusEvent)
	 */
	public void focusLost(FocusEvent e) {
		setBackground(BACKGROUND_COLOR);

		checkBackgroundColor();
		if (getText().length() <= 0) {
			setTexttip(texttip);
		}

	}

	/**
	 * Set Backgroundcolor of the TextField
	 */
	private void checkBackgroundColor() {
		if (!isEnabled())
			setBackground(getDisabledTextColor());
		if (!isEditable())
			setBackground(INATIVE_BACKGROUND_COLOR);
	}

	/**
	 * Set the Texttip of the TextField. The texttip is a string that will shown
	 * as default value in the TextField.
	 * 
	 * @param texttip
	 *            String TextTip
	 */
	public void setTexttip(String texttip) {
		this.texttip = texttip;
		if (getText().length() <= 0) {
			setText(texttip);
			setForeground(INATIVE_FORGROUND_COLOR);
		}
	}

	/**
	 * Return the Texttip
	 * 
	 * @return Texttip
	 */
	public String getexttip() {
		return texttip;
	}

	/**
	 * An own TextDocument to validate the content of the TextField
	 * 
	 * @author tobiby
	 * 
	 */
	public class TextDocument extends PlainDocument {
		/**
		 * 
		 */
		private static final long serialVersionUID = -7454173737867255036L;
		String regex;
		Pattern pattern;

		public TextDocument() {
			super();

		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see javax.swing.text.PlainDocument#insertString(int,
		 * java.lang.String, javax.swing.text.AttributeSet)
		 */
		public void insertString(int offset, String s, AttributeSet attributeSet)
				throws BadLocationException {
			try {
				// check the limit
				if (getText(0, this.getLength()).length() >= limit
						|| s.length() > limit) {
					throw new NumberFormatException();
				}
				regex = "\\-|[0-9]|\\.";
				pattern = Pattern.compile(regex);
				Matcher matcher = pattern.matcher(s);
				if (!matcher.find()) {
					regex = "Up|Down|Left|Right";
					pattern = Pattern.compile(regex);
					matcher = pattern.matcher(s);
					if (!matcher.find()) {
						throw new NumberFormatException();
					}
				}
			} catch (NumberFormatException ex) {
				Toolkit.getDefaultToolkit().beep(); // macht ein beep
				return;
			}
			super.insertString(offset, s, attributeSet);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.text.JTextComponent#getText()
	 */
	@Override
	public String getText() {
		return super.getText().equals(texttip) ? "0" : super.getText();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.text.JTextComponent#setText(java.lang.String)
	 */
	@Override
	public void setText(String t) {
		if (t.equals("0.0")) {
			super.setText(texttip);
		} else {
			super.setText(t);
		}
	}

}