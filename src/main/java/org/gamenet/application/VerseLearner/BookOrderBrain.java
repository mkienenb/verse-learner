package org.gamenet.application.VerseLearner;

import java.util.Collections;
import java.util.List;
import java.util.Vector;

import org.gamenet.application.VerseLearner.data.Book;

public class BookOrderBrain {
	private Vector<String> bookNameVector = null;

	public BookOrderBrain(List<String> randomBookList) {
		Vector<String> bookNameVector = new Vector<String>(randomBookList);
		setBookNameVector(bookNameVector);
	}

	public Vector<String> getBookNameVector() {
		return bookNameVector;
	}

	public void setBookNameVector(Vector<String> bookNameVector) {
		this.bookNameVector = bookNameVector;
	}

	public void randomizeBookOrder() {
		Collections.shuffle(bookNameVector);
	}

	public boolean areBooksInCorrectOrder() {
		String[] correctBookOrderArray = Book.getBookArray();
		for (int bookIndex = 0; bookIndex < correctBookOrderArray.length; bookIndex++) {
			if (false == bookNameVector.get(bookIndex).equals(correctBookOrderArray[bookIndex])) {
				return false;
			}
		}
		return true;
	}
}