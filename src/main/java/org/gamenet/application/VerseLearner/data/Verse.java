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

package org.gamenet.application.VerseLearner.data;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Verse
{
    private Book book;
	private Chapter chapter;
	private Integer startingVerseNumber;
	private Integer endingVerseNumber;
	
	private String text;

	static public List<Verse> findVerseByReference(List<Verse> verseList, String reference)
		throws ReferenceException
	{
		reference = reference.trim();
		String bookNamePart = Book.findBookNamePartInReference(reference);
		if (null == bookNamePart)
		{
			throw new ReferenceException("No valid book found in " + reference);
		}
		if (reference.length() > bookNamePart.length() + 1)
		{
			if (' ' != reference.charAt(bookNamePart.length()))
			{
				throw new ReferenceException("Illegal book: position=" + bookNamePart.length() + " in reference "+ reference);
			}
			String rest = reference.substring(bookNamePart.length()+ 1).trim();
			
			Pattern pattern = Pattern.compile("^(\\d+)(\\:(\\d+)(\\-(\\d+))?)?$");
		    Matcher m = pattern.matcher(rest);
		    if (m.matches())
	    	{
		    	String chapterNumberString = m.group(1);
                Integer chapterNumber = null;
                if (null != chapterNumberString)
                {
                    chapterNumber = new Integer(chapterNumberString);
                }
                
		    	String startingVerseNumberString = m.group(3);
                Integer startingVerseNumber = null;
                if (null != startingVerseNumberString)
                {
                    startingVerseNumber = new Integer(startingVerseNumberString);
                }
                
		    	String endingVerseNumberString = m.group(5);
                Integer endingVerseNumber = null;
                if (null != endingVerseNumberString)
                {
                    endingVerseNumber = new Integer(endingVerseNumberString);
                }
                
                return Verse.getVerseList(verseList, bookNamePart, chapterNumber, startingVerseNumber, endingVerseNumber);
	    	}
		}
		
		return null;
	}

	public static List<Verse> getVerseList(List<Verse> verseList, String bookName, Integer chapterNumber,
            Integer startingVerseNumber, Integer endingVerseNumber) 
		throws ReferenceException
	{
        List<Verse> list = new ArrayList<Verse>();
        Iterator<Verse> verseIterator = verseList.iterator();
        while (verseIterator.hasNext())
        {
            Verse verse = verseIterator.next();
            
            boolean bookMatches = verse.book.getName().equals(bookName);
            boolean chapterMatches = (null == chapterNumber) ? true :
                verse.chapter.getChapterNumber() == chapterNumber.intValue();
            boolean startingVerseMatches = (null == startingVerseNumber) ? true :
                startingVerseNumber.equals(verse.startingVerseNumber);
            boolean endingVerseMatches = (null == endingVerseNumber) ? true :
                endingVerseNumber.equals(verse.endingVerseNumber);
            
            if ( bookMatches && chapterMatches && startingVerseMatches && endingVerseMatches)
            {
                list.add(verse);
            }
        }
        
        if (list.isEmpty())
        {
            return null;
        }
        
        return list;
    }

	public Verse(Book book, Chapter chapter, int verseNumber, String text) {
		super();

		this.book = book;
		this.chapter = chapter;
		this.startingVerseNumber = new Integer(verseNumber);
		this.text = text;
	}

	public Verse(Book book, Chapter chapter, int startingVerseNumber, int endingVerseNumber, String text) {
		this(book, chapter, startingVerseNumber, text);
		this.endingVerseNumber = new Integer(endingVerseNumber);
	}

	public Verse(String bookName, int chapterNumber, int verseNumber, String text) {
		this(Book.findBookNamed(bookName), Chapter.findChapter(Book.findBookNamed(bookName), chapterNumber), verseNumber, text);
	}

	public Verse(String bookName, int chapterNumber, int startingVerseNumber, int endingVerseNumber, String text) {
		this(bookName, chapterNumber, startingVerseNumber, text);
		this.endingVerseNumber = new Integer(endingVerseNumber);
	}

	public Verse(ReferenceParts reference, String verseText) {
		this.book = Book.findBookNamed(reference.getBookName());
		if (null == reference.getChapterNumber())
		{
			this.chapter = null;
		}
		else
		{
			this.chapter = Chapter.findChapter(Book.findBookNamed(reference.getBookName()),
					reference.getChapterNumber().intValue());
		}
		this.startingVerseNumber = reference.getStartingVerseNumber();
		this.endingVerseNumber = reference.getEndingVerseNumber();
        
//        System.err.println("Verse=" + reference.getStartingVerseNumber());
//        for (int i = verseText.length() - 4; i < verseText.length(); i++)
//        {
//            char c = verseText.charAt(i);
//            System.err.println(String.valueOf(i) + "="+ String.valueOf((int)c));
//        }
  
        String convertedVerse = verseText;

        // Get rid of nbsp;
        convertedVerse = convertedVerse.replace((char)160, ' ').trim();

        char emdash[] = new char[] { 226, 8364, 8221};
        convertedVerse = convertedVerse.replaceAll(new String(emdash), "--");
        
        char quoteStart[] = new char[] { 226, 8364, 339};
        convertedVerse = convertedVerse.replaceAll(new String(quoteStart), "\"");

        char quoteEnd[] = new char[] { 226, 8364, 65533};
        convertedVerse = convertedVerse.replaceAll(new String(quoteEnd), "\"");
      
        char singleQuote[] = new char[] { 226, 8364, 8482};
        convertedVerse = convertedVerse.replaceAll(new String(singleQuote), "'");

        convertedVerse = convertedVerse.replaceAll("�", "--");
        convertedVerse = convertedVerse.replaceAll("—", "--");
        convertedVerse = convertedVerse.replace('�', '"');
        convertedVerse = convertedVerse.replace('�', '"');
        
        // starting and ending double quotes
		convertedVerse = convertedVerse.replaceAll("“", "\"");
		convertedVerse = convertedVerse.replaceAll("”", "\"");
		
        convertedVerse = convertedVerse.replace('�', '\'');

        convertedVerse = convertedVerse.replace('’', '\'');

        char ugh[] = new char[] { 226, 8364};
        int badchars = convertedVerse.indexOf(new String(ugh));
        if (-1 != badchars)
        {
            System.err.println("bad characters after '" + convertedVerse.substring(0, badchars) + "'");
            for (int i = badchars; i < convertedVerse.length(); i++)
            {
                char c = convertedVerse.charAt(i);
                System.err.println(String.valueOf(i) + "="+ String.valueOf((int)c));
            }
        }

        this.text = convertedVerse;
        
    }

	public Book getBook() {
		return book;
	}

	public void setBook(Book book) {
		this.book = book;
	}

	public Chapter getChapter() {
		return chapter;
	}

	public void setChapter(Chapter chapter) {
		this.chapter = chapter;
	}

	public int getVerseNumber() {
		return startingVerseNumber.intValue();
	}

	public void setVerseNumber(int verseNumber) {
		this.startingVerseNumber = new Integer(verseNumber);
	}

	public Integer getEndingVerseNumber() {
		return endingVerseNumber;
	}

	public void setEndingVerseNumber(Integer endingVerseNumber) {
		this.endingVerseNumber = endingVerseNumber;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getReference() {
		String answer = getBook().getName()
		+ " "
		+ getChapter().getChapterNumber()
		+ ":"
		+ getVerseNumber();
		
		Integer endingVerseNumber = getEndingVerseNumber();
		if (null != endingVerseNumber)
		{
			answer += "-" + endingVerseNumber;
		}
		
		return answer;
	}
}
