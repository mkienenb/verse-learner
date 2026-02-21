package org.gamenet.application.VerseLearner;

import java.util.List;

import org.gamenet.application.VerseLearner.data.Verse;

public class VerseBrain {
	private int verseIndex;
	private List<Verse> verseList = null;
	private boolean ignoreWhitespace = false;
	private boolean ignoreCase = false;
	private boolean ignorePunctuation = false;

	private int bookmarkVerseIndex = 0;
	private boolean allowAnyNumberOfSpacesAfterASentence = true;

	public VerseBrain(List<Verse> verseList) {
		setVerseList(verseList);
	}

	public void setVerseList(List<Verse> newVerseList) {
		verseList = newVerseList;
		verseIndex = 0;
		bookmarkVerseIndex = 0;
	}

	public Verse getBookmarkVerse() {
		return verseList.get(bookmarkVerseIndex);
	}

	public void setBookmarkedVerseToCurrentVerse() {
		bookmarkVerseIndex = verseIndex;
	}

	public void goToNextVerse() {
		verseIndex = verseIndex + 1;
		if (verseIndex >= verseList.size()) {
			verseIndex = 0;
		}
	}

	public void goToPreviousVerse() {
		verseIndex = verseIndex - 1;
		if (verseIndex < 0) {
			verseIndex = verseList.size() - 1;
		}
	}

	public void goToBookmarkedVerse() {
		verseIndex = bookmarkVerseIndex;
		if (verseIndex < 0) {
			verseIndex = verseList.size() - 1;
		} else if (verseIndex >= verseList.size()) {
			verseIndex = 0;
		}
	}

	public boolean isAllowAnyNumberOfSpacesAfterASentence() {
		return allowAnyNumberOfSpacesAfterASentence;
	}

	public void setAllowAnyNumberOfSpacesAfterASentence(boolean allowAnyNumberOfSpacesAfterASentence) {
		this.allowAnyNumberOfSpacesAfterASentence = allowAnyNumberOfSpacesAfterASentence;
	}

	public boolean isIgnoreWhitespace() {
		return ignoreWhitespace;
	}

	public void setIgnoreWhitespace(boolean ignoreWhitespace) {
		this.ignoreWhitespace = ignoreWhitespace;
	}

	public boolean isIgnoreCase() {
		return ignoreCase;
	}

	public void setIgnoreCase(boolean ignoreCase) {
		this.ignoreCase = ignoreCase;
	}

	public boolean isIgnorePunctuation() {
		return ignorePunctuation;
	}

	public void setIgnorePunctuation(boolean ignorePunctuation) {
		this.ignorePunctuation = ignorePunctuation;
	}

	public boolean isVerseCorrect(String proposedAnswer) {
		String answer = normalizeString(getAnswer());
		String normalizedProposedAnswer = normalizeString(proposedAnswer);

		// leading and trailing space aren't important
		return (normalizedProposedAnswer.trim().equals(answer.trim()));
	}

	String getAnswer() {
		String answer = getMemoryVerse().getText();
		//
		// if (includeReferenceInAnswer)
		// {
		// answer = answer + " " + getReference();
		// }

		return answer;
	}

	public Verse getMemoryVerse() {
		return (Verse) verseList.get(verseIndex);
	}

	public String getReference() {
		String answer = getMemoryVerse().getReference();

		return answer;
	}

	String correctAnswerPrefix(String rawProposedAnswer, String rawRealAnswer) {
		String realAnswer = normalizeString(rawRealAnswer);
		String proposedAnswer = normalizeString(rawProposedAnswer);

		int indexOfError = indexOfError(proposedAnswer, realAnswer);
		if (-1 == indexOfError) {
			return realAnswer;
		}
		return realAnswer.substring(0, indexOfError);
	}

	protected String normalizeString(String string) {
		String normalizedString = string;
		if (ignorePunctuation) {
			normalizedString = normalizedString.replace('.', '@');
			normalizedString = normalizedString.replace(',', '@');
			normalizedString = normalizedString.replace(';', '@');
			normalizedString = normalizedString.replace('?', '@');
			normalizedString = normalizedString.replace('-', '@');
			normalizedString = normalizedString.replace('!', '@');
			normalizedString = normalizedString.replace('(', '@');
			normalizedString = normalizedString.replace(')', '@');
			normalizedString = normalizedString.replace('"', '@');
			normalizedString = normalizedString.replace('\'', '@');
			normalizedString = normalizedString.replaceAll("@", "");
			normalizedString = removeConsecutiveWhitespace(normalizedString);
		}

		if (allowAnyNumberOfSpacesAfterASentence) {
			normalizedString = normalizedString.replaceAll("[.]\\s+", ". ");
			normalizedString = normalizedString.replaceAll("[?]\\s+", "? ");
			normalizedString = normalizedString.replaceAll("[!]\\s+", "! ");
		}

		if (ignoreWhitespace) {
			normalizedString = removeConsecutiveWhitespace(normalizedString);
		}

		if (ignoreCase) {
			normalizedString = normalizedString.toLowerCase();
		}

		return normalizedString;
	}

	private String removeConsecutiveWhitespace(String normalizedString) {
		String newString = normalizedString;
		do {
			normalizedString = newString;
			newString = newString.replace('\n', ' ').trim();
			newString = newString.replace('\r', ' ').trim();
			newString = newString.replace('\t', ' ').trim();
			newString = newString.replaceAll("  ", " ").trim();
		} while (newString.length() != normalizedString.length());

		return newString;
	}

	public String correctAnswerSoFar(String proposedAnswer) {
		String realAnswer = getAnswer();
		String answerSoFar = correctAnswerPrefix(proposedAnswer, realAnswer);
		return answerSoFar;
	}

	/*

Levenshtein distance (TDD in python)


Feature: finding levenshtein distance between two words

    Scenario Outline:
        Given the first word is "<first>" and the second is "<second>"
        When we compute the levenshtein distance
        Then the distance is <expected>

        Examples: Known minimal distances
        | first | second | expected |
        |       |        | 0        |
        |       | t      | 1        |
        | t     |        | 1        |
        | t     | s      | 1        |
        | t     | t      | 0        |

        Examples: Letter changes
        | first | second | expected |
        | cat   | cat    | 0        |
        | cat   | oat    | 1        |
        | cat   | sat    | 1        |
        | cat   | can    | 1        |
        | cat   | sit    | 2        |

        Examples: Adding letter
        | first | second | expected |
        | cat   | cats   | 1        |
        | hero  | heroes | 2        |
        | cat   | coat   | 1        |
        | cat   | coats  | 2        |
        | cat   | scat   | 1        |

        Examples: Removing letter
        | first | second | expected |
        | cats  | cat    | 1        |
        | scat  | cat    | 1        |
        | coat  | cat    | 1        |
        | heroes| hero   | 2        |

        Examples: Letter change followed by Adding letter
        | first | second | expected |
        | cat   | cots   | 2        |

        Examples: Provided - substitution + substitution + insertion
        | first  | second  | expected |
        | kitten | sitting | 3        |

class Levenshtein:
    def answer(self, first, second):
        # Terminal cases

        # inserting is always minimal if first string is empty
        # this also handles the case when both strings are empty
        if (first == ""):
            return len(second)

        # deleting is always minimal if second string is empty
        if (second == ""):
            return len(first)

        # substituting is always minimal when both strings are different and length is 1
        if (len(first) == 1 and len(second) == 1):
            if (first[0] != second[0]):
                return 1

        # otherwise, check the first character in each string
        # if they are the same, distance so far is zero and run algorithm on remaining length
        if (first[0] == second[0]):
            return self.answer(first[1:], second[1:])

        # at this point, the distance is 1 plus the minimum of
        #   - substituting -- self.answer(first[1:], second[1:])
        distanceAfterSubstituting = self.answer(first[1:], second[1:])
        #   - deleting -- self.answer(first[1:], second)
        distanceAfterDeleting = self.answer(first[1:], second)
        #   - inserting -- self.answer(first, second[1:])
        distanceAfterInserting = self.answer(first, second[1:])
        return 1 + min(distanceAfterSubstituting, distanceAfterInserting, distanceAfterDeleting)

	 */

	protected String hint(String rawProposedAnswer, String rawRealAnswer) {
		String realAnswer = normalizeString(rawRealAnswer);
		String proposedAnswer = normalizeString(rawProposedAnswer);

		int indexOfError = indexOfError(proposedAnswer, realAnswer);
		if (-1 == indexOfError) {
			return null;
		}

		String checkCharacterTypeInProposedAnswerAtError = checkCharacterTypeInStringAtError(proposedAnswer,
				indexOfError);
		String checkCharacterTypeInRealAnswerAtError = checkCharacterTypeInStringAtError(realAnswer, indexOfError);
		if ((null != checkCharacterTypeInProposedAnswerAtError) && (null != checkCharacterTypeInRealAnswerAtError)) {
			return checkCharacterTypeInRealAnswerAtError;
		}

		if (indexOfError < proposedAnswer.length()) {
			if ((Character.isLetter(proposedAnswer.charAt(indexOfError)))) {
				if ((indexOfError < realAnswer.length()) && (Character.isLetter(realAnswer.charAt(indexOfError)))) {
					if (Character.toLowerCase(proposedAnswer.charAt(indexOfError)) == Character
							.toLowerCase(realAnswer.charAt(indexOfError))) {
						return "<case sensitivity>";
					}
				}
			}
		} else {
			String hint = findWordInStringAfterIndex(realAnswer.substring(indexOfError).trim(), 0);
			if (null != hint) {
				return hint;
			}
		}

		String proposedAnswerWord = findWordInStringNearIndex(proposedAnswer, indexOfError);
		String realAnswerWord = findWordInStringNearIndex(realAnswer, indexOfError);
		if (null != realAnswerWord) {
			return realAnswerWord;
		} else if (null != proposedAnswerWord) {
			String hint = findWordInStringBeforeIndex(realAnswer, indexOfError);
			if (null != hint) {
				return hint;
			}
		}

		if (null != checkCharacterTypeInRealAnswerAtError) {
			return checkCharacterTypeInRealAnswerAtError;
		}

		// TODO: handle case when we've added more to the answer than should be there
		// (otherwise returns null)

		return realAnswer.substring(indexOfError, indexOfError);
	}

	private String findWordInStringAfterIndex(String string, int index) {
		while ((string.length() > index) && (!Character.isLetter(string.charAt(index)))
				&& (!Character.isDigit(string.charAt(index)))) {
			++index;
		}

		return findWordInStringNearIndex(string, index);
	}

	private String findWordInStringBeforeIndex(String string, int index) {
		while ((0 < index) && (!Character.isLetter(string.charAt(index)))
				&& (!Character.isDigit(string.charAt(index)))) {
			--index;
		}

		return findWordInStringNearIndex(string, index);
	}

	private String findWordInStringNearIndex(String string, int index) {
		if (null == string) {
			return null;
		}

		if (string.length() <= index) {
			return null;
		}

		if ((!Character.isLetter(string.charAt(index))) && (!Character.isDigit(string.charAt(index)))) {
			return null;
		}

		int startIndex = index;
		int endIndex = index;

		if (Character.isLetter(string.charAt(index))) {
			while ((startIndex > 0) && (Character.isLetter(string.charAt(startIndex - 1)))) {
				startIndex--;
			}

			while ((endIndex < string.length()) && (Character.isLetter(string.charAt(endIndex)))) {
				endIndex++;
			}
		}

		if (Character.isDigit(string.charAt(index))) {
			while ((startIndex > 0) && (Character.isDigit(string.charAt(startIndex - 1)))) {
				startIndex--;
			}

			while ((endIndex < string.length()) && (Character.isDigit(string.charAt(endIndex)))) {
				endIndex++;
			}
		}

		return string.substring(startIndex, endIndex);
	}

	private String checkCharacterTypeInStringAtError(String string, int indexOfError) {
		if (indexOfError >= string.length()) {
			return null;
		}

		char errorChar = string.charAt(indexOfError);

		switch (errorChar) {
		case ' ':
			return "<white space>";
		case '.':
		case ',':
		case ';':
		case '?':
		case '-':
		case ':':
		case '!':
		case '(':
		case ')':
		case '"':
		case '\'':
			return "<punctuation: " + errorChar + ">";
		default:
			return null;
		}
	}

	private int indexOfError(String proposedAnswer, String realAnswer) {
		int i;
		for (i = 0; i < realAnswer.length(); i++) {
			char c = realAnswer.charAt(i);
			if (proposedAnswer.length() <= i) {
				break;
			}
			if (proposedAnswer.charAt(i) != c) {
				break;
			}
		}
		if (i == realAnswer.length()) {
			return -1;
		}
		return i;
	}

	public List<Verse> getVerseList() {
		return verseList;
	}

}