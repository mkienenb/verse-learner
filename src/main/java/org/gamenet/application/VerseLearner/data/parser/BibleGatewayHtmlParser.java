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
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.gamenet.application.VerseLearner.data.ReferenceException;
import org.gamenet.application.VerseLearner.data.ReferenceParts;
import org.gamenet.application.VerseLearner.data.Verse;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class BibleGatewayHtmlParser implements VerseParser
{
    private static final String BIBLE_GATEWAY_VERSE_MARKER_BEFORE = "<div class=\"result-text-style-normal\">";
    private static final String BIBLE_GATEWAY_VERSE_MARKER_AFTER = "<div id=\"result-options-info2\"";

    private static final String BIBLE_GATEWAY_VERSE_MARKER_AFTER2 = "<div class=\"passage-scroller\">";

    private static final String HEADERS[] = {
        "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">",
        "<html xmlns=\"http://www.w3.org/1999/xhtml\">",
        "<head>",
        "<meta http-equiv=\"content-type\" content=\"text/html; charset=utf-8\" />"
    };
    
    private static final String PARTIAL_HEADER_STARTS_WITH =
        "<title>BibleGateway.com - Passage&nbsp;Lookup: ";
    
    private static final String PARTIAL_HEADER_ENDS_WITH =
        " - Passage&nbsp;Lookup - New King James Version - BibleGateway.com</title>";
    
    private static final String PARTIAL_HEADER_STARTS_WITH2 =
        "<title>";
    
    private static final String PARTIAL_HEADER_CONTAINS2 =
        " NKJV - ";
    
    private static final String PARTIAL_HEADER_ENDS_WITH2 =
        " - Bible Gateway</title>";
    
    public List<Verse> parseInputStream(InputStream inputStream)
        throws IOException, ReferenceException, WrongFormatException
    {
        List<Verse> verseList = new ArrayList<Verse>();
        Reader reader = new InputStreamReader(inputStream, "utf-8");
        BufferedReader bufferedReader = new BufferedReader(reader);
        
        for (int i = 0; i < HEADERS.length; i++)
        {
            String header = HEADERS[i];
            String line = bufferedReader.readLine();
            if (null == line)
            {
                throw new WrongFormatException("BibleGatewayHtmlParser: null at line " + i);
            }
            if (! header.equals(line))
            {
                throw new WrongFormatException("BibleGatewayHtmlParser: unexpect input at line " + i + ": " + line);
            }
        }

        String line = bufferedReader.readLine();
        if (null == line)
        {
            throw new WrongFormatException("BibleGatewayHtmlParser: null at PARTIAL_HEADER_STARTS_WITH line ");
        }
        if  ( ((! line.startsWith(PARTIAL_HEADER_STARTS_WITH)) && (! line.endsWith(PARTIAL_HEADER_ENDS_WITH)))
            	&& ((! line.startsWith(PARTIAL_HEADER_STARTS_WITH2)) && (! line.contains(PARTIAL_HEADER_CONTAINS2)) && (! line.endsWith(PARTIAL_HEADER_ENDS_WITH2))) ) {
            throw new WrongFormatException("BibleGatewayHtmlParser: unexpect input at PARTIAL_HEADER_STARTS_WITH/PARTIAL_HEADER_ENDS_WITH line: " + line);
        }

        line = bufferedReader.readLine();
        while ((null != line) && (! line.contains(BIBLE_GATEWAY_VERSE_MARKER_BEFORE)))
        {
            line = bufferedReader.readLine();
        }

        if (null != line) {
            line = line.substring(line.indexOf(BIBLE_GATEWAY_VERSE_MARKER_BEFORE));
        }

        StringBuffer stringBuffer = new StringBuffer();
        while ((null != line) && ((! line.startsWith(BIBLE_GATEWAY_VERSE_MARKER_AFTER)) && (! line.contains(BIBLE_GATEWAY_VERSE_MARKER_AFTER2))))
        {
            stringBuffer.append(line);
            stringBuffer.append("\n");
             line = bufferedReader.readLine();
       }
        
        if ((null != line) && (line.contains(BIBLE_GATEWAY_VERSE_MARKER_AFTER2))) {
            stringBuffer.append("</div>");
            stringBuffer.append("\n");
        }

        bufferedReader.close();
        reader.close();
    
        String xmlStringData = prefixDataWithEntityResolverInfo(removeIllegalSequences(stringBuffer.toString()));
        InputSource inputSource = new InputSource( new StringReader(xmlStringData) );
        
        System.out.println(xmlStringData);
        
//      Use an instance of ourselves as the SAX event handler
        DefaultHandler handler = new HtmlHandler(verseList); 
        // Use the default (non-validating) parser
        SAXParserFactory factory = SAXParserFactory.newInstance();
        try {
          // Set up output stream
          // Parse the input 
          SAXParser saxParser = factory.newSAXParser();
          
//          try
//          {
//        	  saxParser.setProperty ("http://xml.org/sax/features/validation", true); 
//          }
//          catch (SAXException e) 
//          { 
//        	  System.out.println("error in setting up parser property");
//          }

          saxParser.parse( inputSource, handler); 
        } catch (Throwable t) {
          t.printStackTrace();
        }

        return verseList;
    }

    private String removeIllegalSequences(String string)
    {
    	StringBuffer buffer = new StringBuffer(string.length());
    	int lastPos = 0;
    	int pos = string.indexOf("value='", lastPos);
    	while (-1 != pos)
    	{
    		pos = pos + "value='".length();
        	buffer.append(string.substring(lastPos, pos));
    		
        	while((pos < string.length()) && (string.charAt(pos) != '\''))
        	{
        		char c = string.charAt(pos);
        		if (c == '<')
        		{
                	buffer.append("&lt;");
        		}
        		else if (c == '>')
        		{
                	buffer.append("&gt;");
        		}
        		else
        		{
                	buffer.append(c);
        		}
        		pos++;
        	}
        	
    		lastPos = pos;
    		pos = string.indexOf("value='", lastPos);
    	}
    	
    	buffer.append(string.substring(lastPos));
		return buffer.toString();
	}

	private String prefixDataWithEntityResolverInfo(String string)
    {
        String nbsp = "<!ENTITY nbsp \"&#x00A0;\">";
        String docType = "<!DOCTYPE x [" + nbsp + "]>";
        
        String enclosingStartTag = "<startingtag>";
        String enclosingEndTag = "</startingtag>";
        String cleanString = string.replaceAll("\\<br \\\\/\\>", "<br/>");
        
        return docType
         	+ enclosingStartTag
            + cleanString
            + enclosingEndTag;
        
        
//        static String template =
//            "<!DOCTYPE x [ "
//          + "<!ENTITY  % entities SYSTEM \"{0}\"> "
//          + "<!ENTITY file SYSTEM \"{1}\" >" + "%entities;" + "]>"
//          + "<x>&file;</x>";
//
//        private static String createFromTemplate(File entityFile, File xmlFile) {
//          return MessageFormat.format(template, entityFile.getAbsolutePath(),
//                  xmlFile.getAbsolutePath());
//        }
        //
//      private static File entityFile = new File(<ENTITYFILE>);
      //
//      public Document parse(File f) throws JDOMException, IOException {
//        String xml = createFromTemplate(entityFile, f);
//        SAXBuilder builder = new SAXBuilder();
//        Document doc = builder.build(new StringReader(xml));
//        Element e = (Element) doc.getRootElement().getChildren().get(0);
//        e.detach();
//        Document doc2 = new Document(e);
//        return doc2;
//      }
    }


    class HtmlHandler extends DefaultHandler
    {
        public class VersePiece
        {
            private String reference;
            private String text;

            public VersePiece(String reference)
            {
                this.reference = reference;
            }

            public String getText()
            {
                return text;
            }

            public void setText(String text)
            {
                this.text = text;
            }

            public String getReference()
            {
                return reference;
            }

        }

        StringBuffer textBuffer;
        Stack<Object> elementStack = new Stack<Object>();
        
        String referenceString;
        Integer verseNumber;
        StringBuffer verseTextBuffer;
        Stack<Object> insideIgnoredElementStack = new Stack<Object>();
        
        VersePiece potentialVersePiece;
        List<VersePiece> versePieceList = new ArrayList<VersePiece>();
        List<Verse> verseList = null;
        
        public HtmlHandler(List<Verse> verseList)
        {
            this.verseList = verseList;
        }
        
        public void emit(String s)
        {
            System.out.println(s);
        }
        private void echoText()
            throws SAXException
        {
          if (textBuffer == null) return;
          String s = ""+textBuffer;
          emit(s);
          textBuffer = null;
        }
        
        private void appendToVerseText()
        {
            if (insideIgnoredElementStack.isEmpty())
            {
                if (null != verseNumber)
                {
                    if (null != textBuffer)
                        verseTextBuffer.append(textBuffer);
                }
            }
        } 

        public void endDocument() throws SAXException
        {
            appendToVerseText();
            if (null != verseTextBuffer)
            {
                potentialVersePiece.setText(verseTextBuffer.toString());
                System.err.println(verseTextBuffer);
                System.err.println();
            }

            Iterator<VersePiece> versePieceIterator = versePieceList.iterator();
            while (versePieceIterator.hasNext())
            {
                VersePiece versePiece = versePieceIterator.next();
                
                if (null != versePiece.getText())
                {
                    try
                    {
                        ReferenceParts referenceParts = new ReferenceParts(versePiece.getReference());
                        Verse verse = new Verse(referenceParts, versePiece.getText().trim());
                        verseList.add(verse);
                    }
                    catch (ReferenceException e)
                    {
                        throw new SAXException(e);
                    }
                }
            }
        }
        
        public void startElement(String namespaceURI,
                String sName, // simple name
                String qName, // qualified name
                Attributes attrs)
        throws SAXException
        {
            String eName = sName; // element name
          if ("".equals(eName)) eName = qName; // not namespace-aware
          
          String rootEName = null;
          if (!elementStack.isEmpty())
          {
              rootEName = (String)elementStack.peek();
          }
          
          elementStack.push(attrs);
          elementStack.push(eName);

          if ("strong".equals(eName))
          {
              insideIgnoredElementStack.push(this);
          }
          else if ("ol".equals(eName))
          {
              insideIgnoredElementStack.push(this);
          }

          if ("a".equals(rootEName))
          {
          }
          else if ("sup".equals(rootEName))
          {
          }
          else if ("li".equals(rootEName))
          {
          }
          else if ("div".equals(eName))
          {
              String classValue = attrs.getValue("class");
              if (!"result-text-style-normal".equals(classValue))
              {
            	  emit("start skipping div class='" + classValue + "'");
                  insideIgnoredElementStack.push(this);
              }
          }
          else
          {
              appendToVerseText();
          }

          echoText();

//          emit("<"+eName);
//          if (attrs != null) {
//            for (int i = 0; i < attrs.getLength(); i++) {
//              String aName = attrs.getLocalName(i); // Attr name
//              if ("".equals(aName)) aName = attrs.getQName(i);
//              emit(" ");
//              emit(aName+"=\""+attrs.getValue(i)+"\"");
//            }
//          }
//          emit(">");
        }

        public void endElement(String namespaceURI,
                String sName, // simple name
                String qName  // qualified name
                )
        throws SAXException
        {
          String eName = sName; // element name
          if ("".equals(eName)) eName = qName; // not namespace-aware

          String startEname = (String)elementStack.pop();
          Attributes startAttributes = (Attributes)elementStack.pop();
          
          if (! startEname.equals(eName))
          {
              throw new SAXException("eName '" + eName + "' != startEname '" + startEname + "'");
          }
          
          emit("<"+eName);
          if (startAttributes != null) {
              for (int i = 0; i < startAttributes.getLength(); i++) {
                String aName = startAttributes.getLocalName(i); // Attr name
                if ("".equals(aName)) aName = startAttributes.getQName(i);
                emit(" ");
                emit(aName+"=\""+startAttributes.getValue(i)+"\"");
              }
            }
            emit(">");

            if ("strong".equals(eName))
            {
                insideIgnoredElementStack.pop();
            }
            else if ("ol".equals(eName))
            {
                insideIgnoredElementStack.pop();
            }
            // TODO: This code isn't ever getting called.  Probably not an issue since only the first div contains information we care about.
            else if ("startingtag".equals(eName))
            {
	             if ("div".equals(eName))
	            {
	          	  String classValue = startAttributes.getValue("class");
	                if (!"result-text-style-normal".equals(classValue))
	                {
	              	  emit("end skipping div class='" + classValue + "'");
	                    insideIgnoredElementStack.pop();
	                }
	            }
            }
          if ("h4".equals(eName))
          {
              if (textBuffer == null) referenceString = "";
              referenceString = "" + textBuffer;
          }
          else if ("span".equals(eName))
          {
              if ("sup".equals(startAttributes.getValue("class")))
              {
                  if (null != startAttributes.getValue("id"))
                  {
                      if (textBuffer != null)
                      {
                          verseNumber = new Integer(textBuffer.toString());
                          if (null != verseTextBuffer)
                          {
                              potentialVersePiece.setText(verseTextBuffer.toString());
                              System.err.println(verseTextBuffer);
                              System.err.println();
                          }
                          verseTextBuffer = new StringBuffer();
                          System.err.println(referenceString + ":" + verseNumber);
                          potentialVersePiece = new VersePiece(referenceString + ":" + verseNumber);
                          versePieceList.add(potentialVersePiece);
                      }
                  }
              }
          }
          else if ("sup".equals(eName))
          {
              {
                  if (null != startAttributes.getValue("id"))
                  {
                      if (textBuffer != null)
                      {
                          verseNumber = new Integer(textBuffer.toString());
                          if (null != verseTextBuffer)
                          {
                              potentialVersePiece.setText(verseTextBuffer.toString());
                              System.err.println(verseTextBuffer);
                              System.err.println();
                          }
                          verseTextBuffer = new StringBuffer();
                          System.err.println(referenceString + ":" + verseNumber);
                          potentialVersePiece = new VersePiece(referenceString + ":" + verseNumber);
                          versePieceList.add(potentialVersePiece);
                      }
                  }
              }
          }
          else if ("i".equals(eName))
          {
              appendToVerseText();
          }
          else if ("a".equals(eName))
          {
          }
          // TODO: not sure if this is going to work right
          else if ("br".equals(eName))
          {
        	  verseTextBuffer.append(" ");
          }
          else if ("sup".equals(eName))
          {
          }
          else if ("h5".equals(eName))
          {
          }
          else if ("strong".equals(eName))
          {
          }
          else if ("ol".equals(eName))
          {
          }
          
          else
          {
              appendToVerseText();
          }
              
          

          echoText();
          
          emit("</"+eName+">");
        } 
        
        public void characters(char buf[], int offset, int len)
        throws SAXException
        {
          String s = new String(buf, offset, len);
          if (textBuffer == null) {
            textBuffer = new StringBuffer(s);
          } else {
            textBuffer.append(s);
          }
        } 
    }

}
