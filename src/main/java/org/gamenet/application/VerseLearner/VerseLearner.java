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

package org.gamenet.application.VerseLearner;

import java.awt.Component;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Vector;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import org.gamenet.application.VerseLearner.data.Book;
import org.gamenet.application.VerseLearner.data.ReferenceException;
import org.gamenet.application.VerseLearner.data.Verse;
import org.gamenet.application.VerseLearner.data.parser.BibleGatewayHtmlParser;
import org.gamenet.application.VerseLearner.data.parser.FlatFileVerseReferenceVerseTextParser;
import org.gamenet.application.VerseLearner.data.parser.VerseParser;
import org.gamenet.application.VerseLearner.data.parser.VerseParser.WrongFormatException;
import org.gamenet.application.VerseLearner.gui.VerseLearnerFrame;
import org.gamenet.application.VerseLearner.gui.VerseLearnerGUI;

public class VerseLearner {

	private VerseBrain verseBrain;
	private VerseOrderBrain verseOrderBrain;
	private BookOrderBrain bookOrderBrain;

    private VerseLearnerGUI window = null;

	private int answerRequests;
	private int checkRequests;
	private int hintRequests;

    private boolean showReferencesOnEachVerse = false;
    private boolean includeReferenceInAnswer = false;

	public boolean isShowReferencesOnEachVerse() {
		return showReferencesOnEachVerse;
	}

	public void setShowReferencesOnEachVerse(boolean showReferencesOnEachVerse) {
		this.showReferencesOnEachVerse = showReferencesOnEachVerse;
	}

	public boolean isIncludeReferenceInAnswer() {
		return includeReferenceInAnswer;
	}

	public void setIncludeReferenceInAnswer(boolean includeReferenceInAnswer) {
		this.includeReferenceInAnswer = includeReferenceInAnswer;
	}

	public VerseLearner(List<Verse> verseList) {
        this.verseBrain = new VerseBrain(verseList);
		bookOrderBrain = new BookOrderBrain(Book.randomBookList());
    }

	public static void main(String[] args)
    {
		List<Verse> verseList = getVerseListFromFile(null);

        if (null == verseList)
        {
        	// TODO: add help panel stating what a verse list file looks like
            System.exit(1);
        }

		VerseLearner verseLearner = new VerseLearner(verseList);
		verseLearner.testChapterMatch();
	}

    private static List<Verse> getVerseListFromFile(Component parent)
    {
        List<Verse> verseList = null;
		try {
            // BibleGatewayHtmlParser
            String bibleGatewayDirectoryName = "/home/mkienenb/workspaces/oxygen-personal/VerseLearner/Data";

            File file = null;
            JFileChooser fc = new JFileChooser();
            fc.setCurrentDirectory(new File(bibleGatewayDirectoryName));
            int returnVal = fc.showOpenDialog(parent);

            if (returnVal == JFileChooser.APPROVE_OPTION) {
                file = fc.getSelectedFile().getAbsoluteFile();
            } else {
                return null;
            }

            VerseParser verseParser = new BibleGatewayHtmlParser();

			FileInputStream inputStream = new FileInputStream(file);
            WrongFormatException bibleGatewayHtmlParserWrongFormatException = null;
			try
            {
                verseList = verseParser.parseInputStream(inputStream);
            }
            catch (WrongFormatException e)
            {
                bibleGatewayHtmlParserWrongFormatException = e;

                inputStream.close();
                inputStream = null;
                inputStream = new FileInputStream(file);

                // FlatFileVerseReferenceVerseTextParser
                verseParser = new FlatFileVerseReferenceVerseTextParser();
                try
                {
                    verseList = verseParser.parseInputStream(inputStream);
                }
                catch (WrongFormatException e1)
                {
                    JOptionPane.showMessageDialog(parent, "Unsupported file type");
                    bibleGatewayHtmlParserWrongFormatException.printStackTrace();
                    e1.printStackTrace();
                    return null;
                }
            }
            finally
            {
                if (null != inputStream)
                {
                    inputStream.close();
                    inputStream = null;
                }
            }
		} catch (IOException e) {
            JOptionPane.showMessageDialog(parent, e.getMessage());
			e.printStackTrace();
			return null;
        } catch (ReferenceException e) {
            JOptionPane.showMessageDialog(parent, e.getMessage());
            e.printStackTrace();
            return null;
        }

		if (null == verseList)
		{
            JOptionPane.showMessageDialog(parent, "No verses found in file.");
            return null;
		}
        return verseList;
    }

	public void checkVerse(String proposedAnswer) {
		if (verseBrain.isVerseCorrect(proposedAnswer)) {
			verseIsCorrect();
		}
	}

	private void verseIsCorrect() {
		boolean goToNextVerse = false;
		if ((0 == answerRequests) && (0 == checkRequests) && (0 == hintRequests))
		{
			goToNextVerse = true;
		}
		else
		{
			String tryAgainOption = "Try verse again.";
			String nextOption = "Close enough. Go to the next verse.";

			// JOptionPane.showMessageDialog(mainWindow, tryAgainOption);

			Object[] possibleValues = { tryAgainOption, nextOption };
			Object selectedValue = JOptionPane.showInputDialog(null,
				"Try verse again, or go to the next verse?", "Correct.",
				JOptionPane.QUESTION_MESSAGE, null,
				possibleValues, possibleValues[0]);

			if (selectedValue.equals(nextOption))
			{
				goToNextVerse = true;
			}
		}

		if (goToNextVerse)
		{
	        String currentVerseText = verseBrain.getReference() + "\n" + verseBrain.getMemoryVerse().getText();

            window.addCurrentVerseToCompletedVerseList(currentVerseText);

            verseBrain.goToNextVerse();
		}

		initializeVerse();
	}

	public void openFile() {
        List<Verse> newVerseList = getVerseListFromFile((VerseLearnerFrame)window);
        if (null != newVerseList)
        {
            verseBrain.setVerseList(newVerseList);
            initializeVerse();
        }
	}

	public void quit() {
    	window.setVisible(false);
        System.exit(0);
	}

	public void initializeVerse()
	{
		answerRequests = 0;
		checkRequests = 0;
		hintRequests = 0;

		window.initializeVerseAnswer(verseBrain.getAnswer());
	}

	public void setWriteVersesFromReferencesMode() {
        initializeVerse();

        window.showWriteVersesFromReferencesMode();
	}

	public void changeVerses(String verseReference) {
        List<Verse> newVerseList;
        try {
            newVerseList = Verse.findVerseByReference(verseBrain.getVerseList(), verseReference);
        } catch (ReferenceException e1) {
        	window.displayMessage("No verse found: " + e1.getMessage());
            return;
        }

        if (null != newVerseList)
        {
        	verseBrain.setVerseList(newVerseList);
        	verseOrderBrain = new VerseOrderBrain(newVerseList);
            initializeVerse();
        }
        else
        {
        	window.displayMessage("No verse found.");
        }
	}

	public void testChapterMatch() {
		verseOrderBrain = new VerseOrderBrain(verseBrain.getVerseList());

		this.window = new VerseLearnerFrame(this);
		window.setVisible(true);
		initializeVerse();
	}

	public void setPutVersesInCorrectOrderMode() {
		verseOrderBrain = new VerseOrderBrain(verseBrain.getVerseList());

        window.showPutVersesInCorrectOrderMode();
	}


	public void setPutBooksOfTheBibleInCorrectOrderMode() {
		bookOrderBrain = new BookOrderBrain(Book.randomBookList());

	    window.showPutBooksOfTheBibleInCorrectOrderMode();
	}

	public void verseDragged() {

        if (verseOrderBrain.areVersesInCorrectOrder()) {
        	return;
        }

        verseOrderBrain.randomizeVerseOrder();

        showReferencesOnEachVerse = false;

        // Do something to show that we're done
    	window.displayMessage("Correct!");
    	// TODO: shouldn't be calling repaint from this class
        window.repaint();
	}

	public String requestHint(String proposedAnswer) {
		hintRequests++;
		String realAnswer = verseBrain.getAnswer();
		String hint = verseBrain.hint(proposedAnswer, realAnswer);

		return hint;
	}

	public void reportUnhelpfulHint(String proposedAnswer) {

		String realAnswer = verseBrain.getAnswer();
		String hint = verseBrain.hint(proposedAnswer, realAnswer);
		String answerSoFar = verseBrain.correctAnswerPrefix(proposedAnswer, realAnswer);

		// IMPLEMENT: report hint
		System.out.println("[" + realAnswer + "] - realAnswer");
		System.out.println("[" + proposedAnswer + "] - proposedAnswer");
		System.out.println("[" + answerSoFar + "] - answerSoFar");
		System.out.println("[" + hint + "] - hint");
	}

	public void setCurrentVerseToBookmarkVerse() {
		verseBrain.setBookmarkedVerseToCurrentVerse();
	}

	public void jumpToBookmark() {
		verseBrain.goToBookmarkedVerse();

		initializeVerse();
	}

	public void previousVerse() {
		verseBrain.goToPreviousVerse();

		initializeVerse();
	}

	public void nextVerse() {
		verseBrain.goToNextVerse();
		initializeVerse();
	}

	public void didShowAnswer() {
		answerRequests++;
	}

	public String checkAnswerSoFar(String proposedAnswer) {
		checkRequests++;
		return verseBrain.correctAnswerSoFar(proposedAnswer);
	}

	public void bookDragged() {
        if (bookOrderBrain.areBooksInCorrectOrder()) {
        	return;
        }

        bookOrderBrain.randomizeBookOrder();

        // Do something to show that we're done
    	window.displayMessage("Correct!");
    	// TODO: shouldn't be calling repaint from this class
        window.repaint();
	}

	public String getReference() {
		return verseBrain.getReference();
	}

	public Verse getBookmarkVerse() {
		return verseBrain.getBookmarkVerse();
	}

	public boolean isAllowAnyNumberOfSpacesAfterASentence() {
		return verseBrain.isAllowAnyNumberOfSpacesAfterASentence();
	}

	public boolean isIgnoreWhitespace() {
		return verseBrain.isIgnoreWhitespace();
	}

	public boolean isIgnoreCase() {
		return verseBrain.isIgnoreCase();
	}

	public boolean isIgnorePunctuation() {
		return verseBrain.isIgnoreCase();
	}

	public void setAllowAnyNumberOfSpacesAfterASentence(boolean flag) {
		verseBrain.setAllowAnyNumberOfSpacesAfterASentence(flag);
	}

	public void setIgnoreWhitespace(boolean flag) {
		verseBrain.setIgnoreWhitespace(flag);
	}

	public void setIgnoreCase(boolean flag) {
		verseBrain.setIgnoreCase(flag);
	}

	public void setIgnorePunctuation(boolean flag) {
		verseBrain.setIgnorePunctuation(flag);
	}

	public Vector<Verse> getOrderedVerseList() {
		return verseOrderBrain.getVerseVector();
	}

	public Vector<String> getOrderedBookNameVector() {
		return bookOrderBrain.getBookNameVector();
	}

}
