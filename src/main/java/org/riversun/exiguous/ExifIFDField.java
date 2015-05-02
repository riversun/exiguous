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

/**
 * Structure class of IFD field
 * 
 * @author Tom Misawa (riversun.org@gmail.com)
 * 
 */
public class ExifIFDField {

	public String TagName;
	public long Tag;
	public long DataType;
	public long SizeCount;
	public long DataSize;
	public long DataOffsetAddress;

	// If the data is larger than 4byte, it'll enter into this.
	public int[] Data;
	// If the data is smaller than 4byte, it'll enter into this.
	public long LongData;

	/**
	 * Retrieve the referenced data of IFD of field<br>
	 * (It equals to specify the IFDField#Data properties directly)
	 * 
	 * @return
	 */
	public int[] getData() {
		return Data;
	}

	/**
	 * Take out the referenced data of IFD of the field as a String
	 * 
	 * @return
	 */
	public String getStringData() {
		String ret = null;
		if (Data == null) {
			ret = String.valueOf(LongData);
		} else {
			StringBuffer sb = new StringBuffer();

			for (int i = 0; i < Data.length; i++) {
				char c = (char) Data[i];
				// cancel null string
				if (c != '\0') {
					sb.append(c);
				}
			}

			ret = sb.toString();
		}
		return ret;
	}

}
