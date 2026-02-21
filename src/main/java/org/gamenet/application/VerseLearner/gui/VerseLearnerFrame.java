/*
 * Copyright (c) 2008 Maurice Kienenberger
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package org.gamenet.application.VerseLearner.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToggleButton.ToggleButtonModel;
import javax.swing.ListCellRenderer;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.gamenet.application.VerseLearner.VerseLearner;
import org.gamenet.application.VerseLearner.data.Verse;
import org.gamenet.swing.DraggableJList;
import org.gamenet.swing.GridBagConstraintsOneShareWidthOneShareHeight;
import org.gamenet.swing.GridBagConstraintsWidthRemainder;
import org.gamenet.swing.GridBagConstraintsWidthRemainderOneShareHeight;
import org.gamenet.swing.JFontChooser;

public class VerseLearnerFrame extends JFrame implements VerseLearnerGUI {
	private static final long serialVersionUID = -3726495478154449465L;

    final private JPanel modeVerseTextFromReferencesPanel = new JPanel();

    final private JScrollPane verseOutputJScrollPane = new JScrollPane();
    final private JTextArea verseOutputTextComponent = new JTextArea(1, 40);
	final private JTextArea verseInputTextComponent = new JTextArea(1, 40);
	final private JLabel verseReferenceLabel = new JLabel();
	final private JTextField verseReferenceInput = new JTextField(20);
    final private JTextArea answerLabel = new JTextArea(1, 40);
    final private JTextArea checkAnswerLabel = new JTextArea(1, 40);
	final private JLabel hintLabel = new JLabel();
	final private JButton showAnswerButton = new JButton("Answer");
	final private JButton hideAnswerButton = new JButton("Hide Answer");
	final private JButton verseReferenceButton = new JButton("Switch to this verse");
	final private JButton reportUnhelpfulHintButton = new JButton("Report Unhelpful Hint");
    final private JButton clearPreviousVersesButton = new JButton("Clear completed verse display.");
    final private JButton bookmarkVerseButton = new JButton("Set verse bookmark");
    final private JButton jumpToBookmarkButton = new JButton("Jump to bookmark");

    final private JPanel modeVerseOrderPanel = new JPanel();

    final private JPanel modeBookOrderPanel = new JPanel();

    private VerseLearner controller;

	private Font currentFont;

	public VerseLearnerFrame(VerseLearner verseLearner) {
		super("VerseLearner");

		this.controller = verseLearner;

		initialize();
	}

	public void initialize() {
		this.setSize(640, 480);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setLocationRelativeTo(null);

        setUpMenuUI();

        this.getContentPane().setLayout(new BorderLayout());
        this.getContentPane().add(modeVerseTextFromReferencesPanel, BorderLayout.CENTER);

        initializeModeVerseTextFromReferenceUI();
        initializeModeVerseOrderUI();
        initializeModeBookOrderUI();
	}

	private void setUpMenuUI()
    {
        JMenuBar menuBar = new JMenuBar();
        this.setJMenuBar(menuBar);

        JMenu fileMenu = new JMenu("File");
        menuBar.add(fileMenu);

        JMenu modeMenu = new JMenu("Activities");
        menuBar.add(modeMenu);

        JMenu optionMenu = new JMenu("Options");
        menuBar.add(optionMenu);

        JMenuItem openMenuItem = new JMenuItem("Open");
        fileMenu.add(openMenuItem);

        JMenuItem quitMenuItem = new JMenuItem("Quit");
        fileMenu.add(quitMenuItem);

        openMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	controller.openFile();
            }
        });

        final Component parent = this;
        quitMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int result = JOptionPane.showConfirmDialog(parent, "Are you sure you want to quit?");
                if (JOptionPane.YES_OPTION == result || JOptionPane.OK_OPTION == result)
                {
                	controller.quit();
                }
            }
        });

        ButtonGroup modeButtonGroup = new ButtonGroup();

        JRadioButtonMenuItem modeFullVerseTextMenuItem = new JRadioButtonMenuItem("Write verses from references");
        modeButtonGroup.add(modeFullVerseTextMenuItem);
        modeMenu.add(modeFullVerseTextMenuItem);

        JRadioButtonMenuItem modeVerseOrderMenuItem = new JRadioButtonMenuItem("Put verses in correct order.");
        modeButtonGroup.add(modeVerseOrderMenuItem);
        modeMenu.add(modeVerseOrderMenuItem);

        JRadioButtonMenuItem modeBookOrderMenuItem = new JRadioButtonMenuItem("Put books of the bible in correct order.");
        modeButtonGroup.add(modeBookOrderMenuItem);
        modeMenu.add(modeBookOrderMenuItem);

        modeFullVerseTextMenuItem.setSelected(true);

        modeFullVerseTextMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                controller.setWriteVersesFromReferencesMode();
            }
        });

        modeVerseOrderMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                controller.setPutVersesInCorrectOrderMode();
            }
        });

        modeBookOrderMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                controller.setPutBooksOfTheBibleInCorrectOrderMode();
            }
        });

        // optionMenu.addSeparator();
        final JCheckBoxMenuItem allowAnyNumberOfSpacesAfterASentenceCheckbox = new JCheckBoxMenuItem(
        		"Allow any number of spaces after a sentence",
        		controller.isAllowAnyNumberOfSpacesAfterASentence());
        optionMenu.add(allowAnyNumberOfSpacesAfterASentenceCheckbox);

        final JCheckBoxMenuItem ignoreWhitespaceCheckbox = new JCheckBoxMenuItem(
        		"Ignore whitespace",
        		controller.isIgnoreWhitespace());
        optionMenu.add(ignoreWhitespaceCheckbox);

        final JCheckBoxMenuItem ignoreCaseCheckbox = new JCheckBoxMenuItem(
        		"Ignore case-sensitivity",
        		controller.isIgnoreCase());
        optionMenu.add(ignoreCaseCheckbox);

        final JCheckBoxMenuItem ignorePunctuationCheckbox = new JCheckBoxMenuItem(
        		"Ignore punctuation",
        		controller.isIgnorePunctuation());
        optionMenu.add(ignorePunctuationCheckbox);

        JMenuItem selectFontMenuItem = new JMenuItem("Select Font...");
        optionMenu.add(selectFontMenuItem);

        selectFontMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
    			JFontChooser fontChooser = new JFontChooser();
    			int result = fontChooser.showDialog(parent);
    			if (result == JFontChooser.OK_OPTION) {
    				setCurrentFont(fontChooser.getSelectedFont());
    			}
            }
        });

		JMenuItem increaseFontSizeMenuItem = new JMenuItem("IncreaseFontSize");
        optionMenu.add(increaseFontSizeMenuItem);

        JMenuItem decreaseFontSizeMenuItem = new JMenuItem("decreaseFontSize");
        optionMenu.add(decreaseFontSizeMenuItem);

        increaseFontSizeMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	increaseFontSize();
            }
        });

        decreaseFontSizeMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	decreaseFontSize();
            }
        });

        allowAnyNumberOfSpacesAfterASentenceCheckbox.addChangeListener(new ChangeListener() {

            public void stateChanged(ChangeEvent e) {
                ToggleButtonModel toggleButtonModel = (ToggleButtonModel)allowAnyNumberOfSpacesAfterASentenceCheckbox.getModel();
                controller.setAllowAnyNumberOfSpacesAfterASentence(toggleButtonModel.isSelected());
            }
        });

        ignoreWhitespaceCheckbox.addChangeListener(new ChangeListener() {

            public void stateChanged(ChangeEvent e) {
                ToggleButtonModel toggleButtonModel = (ToggleButtonModel)ignoreWhitespaceCheckbox.getModel();
                controller.setIgnoreWhitespace(toggleButtonModel.isSelected());
            }
        });

        ignoreCaseCheckbox.addChangeListener(new ChangeListener() {

            public void stateChanged(ChangeEvent e) {
                ToggleButtonModel toggleButtonModel = (ToggleButtonModel)ignoreCaseCheckbox.getModel();
                controller.setIgnoreCase(toggleButtonModel.isSelected());
            }
        });

        ignorePunctuationCheckbox.addChangeListener(new ChangeListener() {

            public void stateChanged(ChangeEvent e) {
                ToggleButtonModel toggleButtonModel = (ToggleButtonModel)ignorePunctuationCheckbox.getModel();
                controller.setIgnorePunctuation(toggleButtonModel.isSelected());
            }
        });
    }

    private void initializeModeBookOrderUI()
    {
        DraggableJList<String> bookJList = new DraggableJList<String>(controller.getOrderedBookNameVector());
        bookJList.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent m) {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run()
                    {
                    	controller.bookDragged();
                    }
                });
            }
        });

        modeBookOrderPanel.setLayout(new GridBagLayout());
        modeBookOrderPanel.add(new JScrollPane(bookJList), new GridBagConstraintsOneShareWidthOneShareHeight());
    }

    private void initializeModeVerseOrderUI()
    {
        final DraggableJList<Verse> bookJList = new DraggableJList<Verse>(controller.getOrderedVerseList());
        bookJList.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent m) {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run()
                    {
                    	controller.verseDragged();
                    }
                });
            }
        });

        class JLabelOutputRenderer extends JLabel implements ListCellRenderer
        {
			private static final long serialVersionUID = -5503085525279912369L;

			public Component getListCellRendererComponent(JList list, Object value,
                    int index, boolean isSelected, boolean cellHasFocus)
            {
                Verse verse = (Verse)value;
                String output = verse.getText();
                if (controller.isShowReferencesOnEachVerse())
                {
                    output = verse.getReference() + ": " + output;
                }

                this.setText(output);

                if (isSelected) {
                    this.setBackground(list.getSelectionBackground());
                    this.setForeground(list.getSelectionForeground());
                }
                else {
                    this.setBackground(list.getBackground());
                    this.setForeground(list.getForeground());
                }
                this.setEnabled(list.isEnabled());
                if (null == currentFont) {
                    this.setFont(list.getFont());
                } else {
                    this.setFont(currentFont);
                }

                this.setOpaque(true);

                return this;
            }
        };

        ListCellRenderer bookJListCellRenderer = new JLabelOutputRenderer();
        bookJList.setCellRenderer(bookJListCellRenderer);

        JPanel controlsPanel = new JPanel();
        controlsPanel.setLayout(new BoxLayout(controlsPanel, BoxLayout.Y_AXIS));

        modeVerseOrderPanel.setLayout(new GridBagLayout());
        modeVerseOrderPanel.add(controlsPanel, new GridBagConstraintsOneShareWidthOneShareHeight());

        final JCheckBox showReferencesOnEachVerseCheckbox = new JCheckBox(
        		"Show Reference On Each Verse",
        		controller.isShowReferencesOnEachVerse());

        controlsPanel.add(new JScrollPane(bookJList));
        controlsPanel.add(showReferencesOnEachVerseCheckbox);

        showReferencesOnEachVerseCheckbox.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                ToggleButtonModel toggleButtonModel = (ToggleButtonModel)showReferencesOnEachVerseCheckbox.getModel();
                controller.setShowReferencesOnEachVerse(toggleButtonModel.isSelected());

                bookJList.invalidate();
                bookJList.repaint();
            }
        });

    }

    private void initializeModeVerseTextFromReferenceUI()
    {
        final JButton checkAnswerButton = new JButton("Check");
        final JButton hintButton = new JButton("Hint");
        final JButton previousVerseButton = new JButton("Go back to previous verse");
        final JButton nextVerseButton = new JButton("Go to next verse");

        final JCheckBox includeReferenceInAnswerCheckbox = new JCheckBox(
        		"Include Reference In Answer",
        		controller.isIncludeReferenceInAnswer());

        verseOutputTextComponent.setEditable(false);
        verseOutputTextComponent.setLineWrap(true);
        verseOutputTextComponent.setWrapStyleWord(true);

        JPanel verseOutputInternalPanel = new JPanel();
        verseOutputInternalPanel.setLayout(new FlowLayout());
        verseOutputInternalPanel.add(verseOutputTextComponent);

//		JPanel inputTextPanel = new JPanel();
//		inputTextPanel.setBorder(new LineBorder(Color.BLACK));
//		inputTextPanel.setLayout(new BoxLayout(inputTextPanel, BoxLayout.Y_AXIS));
//        inputTextPanel.add(verseInputTextComponent);

        JPanel verseInputInternalPanel = new JPanel();
        verseInputInternalPanel.setLayout(new FlowLayout());
        verseInputInternalPanel.add(verseInputTextComponent);


		verseInputTextComponent.setLineWrap(true);
        verseInputTextComponent.setWrapStyleWord(true);
        Font font = verseInputTextComponent.getFont();
		verseInputTextComponent.setFont(font.deriveFont((float)font.getSize()+4));

        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));

		JPanel buttonRowPanel = new JPanel();
		buttonRowPanel.setLayout(new FlowLayout());

		JPanel buttonRow2Panel = new JPanel();
		buttonRow2Panel.setLayout(new FlowLayout());

		JPanel buttonRow3Panel = new JPanel();
		buttonRow3Panel.setLayout(new FlowLayout());

        JPanel preferencesPanel = new JPanel();
        preferencesPanel.setLayout(new FlowLayout());

		controlPanel.add(buttonRowPanel);
		buttonRowPanel.add(showAnswerButton);
		buttonRowPanel.add(hideAnswerButton);
		buttonRowPanel.add(checkAnswerButton);
		buttonRowPanel.add(hintButton);
		buttonRowPanel.add(reportUnhelpfulHintButton);

        clearPreviousVersesButton.setVisible(false);

		controlPanel.add(buttonRow2Panel);
		buttonRow2Panel.add(previousVerseButton);
		buttonRow2Panel.add(nextVerseButton);
		buttonRow2Panel.add(jumpToBookmarkButton);
		buttonRow2Panel.add(bookmarkVerseButton);

		controlPanel.add(buttonRow3Panel);
        buttonRow3Panel.add(clearPreviousVersesButton);

        JScrollPane verseInputPanel = new JScrollPane(verseInputInternalPanel);
        verseInputPanel.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        verseInputPanel.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        verseOutputJScrollPane.setViewportView(verseOutputInternalPanel);
        verseOutputJScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        verseOutputJScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        verseOutputJScrollPane.setVisible(false);

        JPanel verseReferencePanel = new JPanel();
        verseReferencePanel.setLayout(new FlowLayout());

		verseReferencePanel.add(verseReferenceLabel);
		verseReferencePanel.add(verseReferenceInput);
		verseReferencePanel.add(verseReferenceButton);

        modeVerseTextFromReferencesPanel.setLayout(new GridBagLayout());

        answerLabel.setEditable(false);
        answerLabel.setLineWrap(true);
        answerLabel.setWrapStyleWord(true);
        answerLabel.setBackground(null);
        answerLabel.setFont(answerLabel.getFont().deriveFont(Font.BOLD));

        checkAnswerLabel.setEditable(false);
        checkAnswerLabel.setLineWrap(true);
        checkAnswerLabel.setWrapStyleWord(true);
        checkAnswerLabel.setBackground(null);
        checkAnswerLabel.setFont(checkAnswerLabel.getFont().deriveFont(Font.BOLD));
        checkAnswerLabel.setForeground(Color.GREEN.darker().darker().darker());
        hintLabel.setForeground(Color.BLUE.darker().darker().darker());

//        // Vertically laid out labels.
//
//        Box labels = Box.createVerticalBox();
//        labels.add(new JLabel("Keep on scrollin'"));
//        labels.add(new JLabel("Keep on scrollin'"));
//        labels.add(new JLabel("Keep on scrollin'"));
//        labels.add(new JLabel("Keep on scrollin'"));
//
//        // Scroll pane for labels.
//        JScrollPane scroller = new JScrollPane(labels)
//        {
//            public Dimension getMaximumSize()
//            {
//                return getPreferredSize();
//            }
//
//            public Dimension getMinimumSize()
//            {
//                return getPreferredSize();
//            }
//        };
//

        JPanel helpsPanel = new JPanel();
        helpsPanel.setLayout(new GridBagLayout());
        helpsPanel.add(answerLabel, new GridBagConstraintsWidthRemainderOneShareHeight());
        helpsPanel.add(checkAnswerLabel, new GridBagConstraintsWidthRemainderOneShareHeight());
        helpsPanel.add(hintLabel, new GridBagConstraintsWidthRemainderOneShareHeight());

        modeVerseTextFromReferencesPanel.add(verseOutputJScrollPane, new GridBagConstraintsWidthRemainderOneShareHeight());
        modeVerseTextFromReferencesPanel.add(verseReferencePanel, new GridBagConstraintsWidthRemainder());
        modeVerseTextFromReferencesPanel.add(verseInputPanel, new GridBagConstraintsWidthRemainderOneShareHeight());
        modeVerseTextFromReferencesPanel.add(helpsPanel, new GridBagConstraintsWidthRemainder());
        modeVerseTextFromReferencesPanel.add(controlPanel, new GridBagConstraintsWidthRemainder());

//        final JFrame myJFrame = this;
//        myJFrame.addComponentListener(new ComponentListener() {
//
//			public void componentShown(ComponentEvent e) {
//			}
//
//			public void componentResized(ComponentEvent e) {
//				modeVerseTextFromReferencesPanel.setSize(myJFrame.getContentPane().getWidth(), modeVerseTextFromReferencesPanel.getHeight());
//			}
//
//			public void componentMoved(ComponentEvent e) {
//			}
//
//			public void componentHidden(ComponentEvent e) {
//			}
//		});

        // main.pack();

        clearPreviousVersesButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                clearCompletedVerseList();
            }
        });

        final Component rootWindow = this;
        verseReferenceButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int result = JOptionPane.showConfirmDialog(rootWindow, "Are you sure you want to change verses?");
                if (JOptionPane.YES_OPTION == result || JOptionPane.OK_OPTION == result) {
                	controller.changeVerses(verseReferenceInput.getText());
                }
            }
        });

		includeReferenceInAnswerCheckbox.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				ToggleButtonModel toggleButtonModel = (ToggleButtonModel)includeReferenceInAnswerCheckbox.getModel();
				controller.setIncludeReferenceInAnswer(toggleButtonModel.isSelected());

				initializeVerseReference();
			}
		});

		hintButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String proposedAnswer = verseInputTextComponent.getText();
				String hint = controller.requestHint(proposedAnswer);

				hintLabel.setText(hint);
				reportUnhelpfulHintButton.setVisible(true);
			}
		});

		bookmarkVerseButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				controller.setCurrentVerseToBookmarkVerse();
				String getVerseReference = controller.getBookmarkVerse().getReference();
				jumpToBookmarkButton.setText("Jump to " + getVerseReference);;
			}
		});

		jumpToBookmarkButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				controller.jumpToBookmark();
			}
		});

		reportUnhelpfulHintButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String proposedAnswer = verseInputTextComponent.getText();
				controller.reportUnhelpfulHint(proposedAnswer);
			}
		});

		previousVerseButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				int result = JOptionPane.YES_OPTION;
				if (0 != verseInputTextComponent.getText().length()) {
					result = JOptionPane.showConfirmDialog(rootWindow, "Are you sure you want to change verses?");
				}
				if (JOptionPane.YES_OPTION == result || JOptionPane.OK_OPTION == result) {
					controller.previousVerse();
				}
			}

		});

		nextVerseButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int result = JOptionPane.YES_OPTION;
				if (0 != verseInputTextComponent.getText().length()) {
					result = JOptionPane.showConfirmDialog(rootWindow, "Are you sure you want to change verses?");
				}
				if (JOptionPane.YES_OPTION == result || JOptionPane.OK_OPTION == result) {
					controller.nextVerse();
				}
			}
		});

		verseInputTextComponent.addKeyListener(new KeyListener() {

			private void onKeyEvent(final JButton reportUnhelpfulHintButton) {
				reportUnhelpfulHintButton.setVisible(false);
				controller.checkVerse(verseInputTextComponent.getText());
			}

			public void keyReleased(KeyEvent e) {
				onKeyEvent(reportUnhelpfulHintButton);
			}

			public void keyPressed(KeyEvent e) {
				onKeyEvent(reportUnhelpfulHintButton);
			}

			public void keyTyped(KeyEvent e) {
				onKeyEvent(reportUnhelpfulHintButton);
			}
		});

		showAnswerButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				answerLabel.setVisible(true);
				showAnswerButton.setVisible(false);
				hideAnswerButton.setVisible(true);
				controller.didShowAnswer();
			}
		});

		hideAnswerButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				answerLabel.setVisible(false);
				showAnswerButton.setVisible(true);
				hideAnswerButton.setVisible(false);
			}
		});

		checkAnswerButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String proposedAnswer = verseInputTextComponent.getText();
				String answerSoFar = controller.checkAnswerSoFar(proposedAnswer);
				checkAnswerLabel.setText(answerSoFar);
			}
		});
    }

    // public methods

	public void initializeVerseAnswer(String answer)
	{
		verseInputTextComponent.setText("");
		verseInputTextComponent.setSelectionStart(0);
		verseInputTextComponent.setSelectionEnd(verseInputTextComponent.getText().length());
		checkAnswerLabel.setText("");
		hintLabel.setText("");
		answerLabel.setVisible(false);
		answerLabel.setText(answer);

		showAnswerButton.setVisible(true);
		hideAnswerButton.setVisible(false);
		reportUnhelpfulHintButton.setVisible(false);

		String getVerseReference = controller.getBookmarkVerse().getReference();
		jumpToBookmarkButton.setText("Jump to " + getVerseReference);;

		initializeVerseReference();

		verseInputTextComponent.requestFocusInWindow();

        // Window window = SwingUtilities.getWindowAncestor(verseInputTextComponent);
        // window.pack();
	}

	public void initializeVerseReference() {
		if (controller.isIncludeReferenceInAnswer())
		{
			verseReferenceLabel.setText("Verse Reference: ");
			verseReferenceInput.setVisible(true);
			verseReferenceButton.setVisible(true);
			// verseReferenceInput.setText("");
		}
		else
		{
			verseReferenceLabel.setText(controller.getReference());
			verseReferenceInput.setVisible(false);
			verseReferenceButton.setVisible(false);
		}
	}

	public void addCurrentVerseToCompletedVerseList(String currentVerseText)
    {
        verseOutputJScrollPane.setVisible(true);
        String previousText = verseOutputTextComponent.getText();
        if (0 != previousText.length())
        {
        	currentVerseText = previousText + "\n" + currentVerseText;
        }
        verseOutputTextComponent.setText(currentVerseText);
        verseOutputTextComponent.setSize(new Dimension(
                verseInputTextComponent.getWidth(),
                verseOutputTextComponent.getHeight()));
        clearPreviousVersesButton.setVisible(true);
    }

    public void clearCompletedVerseList()
    {
        verseOutputJScrollPane.setVisible(false);
        verseOutputTextComponent.setText("");
        clearPreviousVersesButton.setVisible(false);
    }

	public void showModePanel(JPanel modePanel) {
        this.getContentPane().removeAll();
        this.getContentPane().add(modePanel, BorderLayout.CENTER);
        this.validate();
        this.repaint();
    }

	public void showWriteVersesFromReferencesMode() {
		showModePanel(modeVerseTextFromReferencesPanel);
    }

    public void showPutVersesInCorrectOrderMode() {
    	showModePanel(modeVerseOrderPanel);
	}

    public void showPutBooksOfTheBibleInCorrectOrderMode() {
    	showModePanel(modeBookOrderPanel);
	}

	public void displayMessage(String message) {
        JOptionPane.showMessageDialog(this, message);
	}

	private Font getCurrentFont() {
		if (null != currentFont) {
			return currentFont;
		}

        return verseInputTextComponent.getFont();
	}

	private void setCurrentFont(Font font) {
		currentFont = font;
		updateFonts();
	}

	private void increaseFontSize() {
		Font currentFont = getCurrentFont();
		setCurrentFont(currentFont.deriveFont(currentFont.getSize() + 1));
	}

	private void decreaseFontSize() {
		Font currentFont = getCurrentFont();
		setCurrentFont(currentFont.deriveFont(currentFont.getSize() - 1));
	}

	private void updateFonts() {
        // ((JLabelOutputRenderer)bookJList.getCellRenderer()).setFont(currentFont);;

		Font currentFont = getCurrentFont();
        verseOutputTextComponent.setFont(currentFont);
		verseInputTextComponent.setFont(currentFont);
        verseReferenceLabel.setFont(currentFont);
        answerLabel.setFont(currentFont);
        checkAnswerLabel.setFont(currentFont);
        hintLabel.setFont(currentFont);

    	this.repaint();
	}
}
