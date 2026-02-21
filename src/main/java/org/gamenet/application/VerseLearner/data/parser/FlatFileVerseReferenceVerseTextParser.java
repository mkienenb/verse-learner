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

package org.gamenet.application.VerseLearner.data.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import org.gamenet.application.VerseLearner.data.ReferenceException;
import org.gamenet.application.VerseLearner.data.ReferenceParts;
import org.gamenet.application.VerseLearner.data.Verse;

public class FlatFileVerseReferenceVerseTextParser implements VerseParser
{

    public List<Verse> parseInputStream(InputStream inputStream)
        throws IOException, ReferenceException
    {
        List<Verse> verseList = new ArrayList<Verse>();
        Reader reader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(reader);
        String line = bufferedReader.readLine();
        while (null != line)
        {
            while (0 == line.trim().length())
            {
                line = bufferedReader.readLine();
                if (null == line)
                {
                    break;
                }
                continue;
            }
            if (null == line)
            {
                break;
            }
            
            ReferenceParts reference = new ReferenceParts(line);
    
            String verseText = bufferedReader.readLine();
            if (null == verseText)
            {
                break;
            }
            while (0 == verseText.trim().length())
            {
                verseText = bufferedReader.readLine();
                if (null == verseText)
                {
                    break;
                }
            }
            
            Verse verse = new Verse(reference, verseText);
            verseList.add(verse);
    
            line = bufferedReader.readLine();
        }
        
        return verseList;
    }

}
