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
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class Book
{
	static private String books[] = {
        "Genesis",
        "Exodus",
        "Leviticus",
        "Numbers",
        "Deuteronomy",
        "Joshua",
        "Judges",
        "Ruth",
        "1 Samuel",
        "2 Samuel",
        "1 Kings",
        "2 Kings",
        "1 Chronicles",
        "2 Chronicles",
        "Ezra",
        "Nehemiah",
        "Esther",
        "Job",
        "Psalms",
        "Proverbs",
        "Ecclesiastes",
        "Song of Solomon",
        "Isaiah",
        "Jeremiah",
        "Lamentations",
        "Ezekiel",
        "Daniel",
        "Hosea",
        "Joel",
        "Amos",
        "Obadiah",
        "Jonah",
        "Micah",
        "Nahum",
        "Habakkuk",
        "Zephaniah",
        "Haggai",
        "Zechariah",
        "Malachi",
        "Matthew",
        "Mark",
        "Luke",
        "John",
        "Acts",
        "Romans",
        "1 Corinthians",
        "2 Corinthians",
        "Galatians",
        "Ephesians",
        "Philippians",
        "Colossians",
        "1 Thessalonians",
        "2 Thessalonians",
        "1 Timothy",
        "2 Timothy",
        "Titus",
        "Philemon",
        "Hebrews",
        "James",
        "1 Peter",
        "2 Peter",
        "1 John",
        "2 John",
        "3 John",
        "Jude",
        "Revelation"
	};

	private String name;
	private List<Chapter> chapterList;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Book(String name) {
		super();

		this.name = name;
	}

	static public String[] getBookArray()
	{
		return books;
	}
	
	static public List<String> getBookList()
	{
		return Arrays.asList(books);
	}
	
	static public List<String> randomBookList()
	{
		List<String> bookList = new ArrayList<String>(Book.getBookList());
		Collections.shuffle(bookList);
		return bookList;
	}
	
	static public Object[] randomBookArray()
	{
		return randomBookList().toArray();
	}

	public static Book findBookNamed(String bookName) 
	{
		if ("Psalm".equals(bookName))
		{
			bookName = "Psalms";
		}
		// TODO: implement
		return new Book(bookName);
	}

	public void addChapter(Chapter chapter) {
		// TODO: implement
		if (null == chapterList)
		{
			chapterList = new ArrayList<Chapter>();
		}
		chapterList.add(chapter);
	}

	public static String findBookNamePartInReference(String reference) {
		Iterator<String> bookIterator = getBookList().iterator();
		while (bookIterator.hasNext()) {
			String book = bookIterator.next();
			String bookName = book.toLowerCase();
			if ("psalms".equals(bookName))
			{
				bookName = "psalm";
			}
			if (reference.toLowerCase().startsWith(bookName))
			{
				return reference.substring(0, bookName.length());
			}
		}

		return null;
	}
}
