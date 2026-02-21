/*
 * Copyright (c) 2013 Maurice Kienenberger
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

// http://forum.java.sun.com/thread.jspa?threadID=260100&messageID=1124265
// posted by noah.w

package org.gamenet.swing;

import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.Vector;

import javax.swing.JList;
import javax.swing.border.MatteBorder;

public class DraggableJList<E> extends JList {
	private static final long serialVersionUID = -5739484013154330826L;
	
	int from;

	public DraggableJList(final Vector<E> con) {
		super(con);
		
		setBorder(new MatteBorder(1, 1, 1, 1, Color.orange));
		
		addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent m) {
				from = getSelectedIndex();
			}
		});
		
		addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent m) {
				int to = getSelectedIndex();
				if (to == from)
					return;
				E s = con.remove(from);
				con.add(to, s);
				from = to;
			}
		});
	}
}
