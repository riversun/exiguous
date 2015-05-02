/*  exiguous - EXIF reader for java
 *
 *  Copyright (c) 2003-2009 Tom Misawa(riversun.org@gmail.com)
 *  
 *  Permission is hereby granted, free of charge, to any person obtaining a
 *  copy of this software and associated documentation files (the "Software"),
 *  to deal in the Software without restriction, including without limitation
 *  the rights to use, copy, modify, merge, publish, distribute, sublicense,
 *  and/or sell copies of the Software, and to permit persons to whom the
 *  Software is furnished to do so, subject to the following conditions:
 *  
 *  The above copyright notice and this permission notice shall be included in
 *  all copies or substantial portions of the Software.
 *  
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 *  FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 *  DEALINGS IN THE SOFTWARE.
 *  
 */
package org.riversun.exiguous;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Structure class of IFD block
 * 
 * @author Tom Misawa (riversun.org@gmail.com)
 * 
 */
public class ExifIFDBlock {

	private HashMap<String, ExifIFDField> m_FieldMap = new LinkedHashMap<String, ExifIFDField>();

	// Property definition part: not need to do useless encapsulation
	public long Count;
	public long NextIFDOffset;
	public long NextIFDAbsoluteOffset;

	/**
	 * IFD
	 * 
	 * @param IfdField
	 */
	public void addField(ExifIFDField IfdField) {

		String key = String.format("0x%04x", IfdField.Tag);

		m_FieldMap.put(key, IfdField);
	}

	public ExifIFDField getFieldByName(String key) {
		String tagCode = ExifTagFinder.getTagCodeAsString(key);
		return m_FieldMap.get(tagCode);
	}

	public ExifIFDField getField(String tagCodeH) {
		return m_FieldMap.get(tagCodeH);
	}

	public List<ExifIFDField> getIFDList() {
		return new ArrayList<ExifIFDField>(m_FieldMap.values());
	}

}
