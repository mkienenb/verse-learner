package org.gamenet.application.VerseLearner;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.gamenet.application.VerseLearner.data.Verse;

public class VerseOrderBrain {
	private List<Verse> verseList = null;
	final private Vector<Verse> verseVector = new Vector<Verse>();

	public VerseOrderBrain(List<Verse> verseList) {
		setVerseList(verseList);
	}

	private void setVerseList(List<Verse> verseList) {
		this.verseList = verseList;

		randomizeVersesIntoVector(verseList);
	}

	public Vector<Verse> getVerseVector() {
		return verseVector;
	}

	private void randomizeVersesIntoVector(List<Verse> verseList) {
		verseVector.clear();
		verseVector.addAll(verseList);

		randomizeVerseOrder();
	}

	void randomizeVerseOrder() {
		Collections.shuffle(verseVector);
	}

	public boolean areVersesInCorrectOrder() {
		for (int verseIndex = 0; verseIndex < verseList.size(); verseIndex++) {
			if (false == verseVector.get(verseIndex).equals(verseList.get(verseIndex))) {
				return false;
			}
		}

		return true;
	}

}