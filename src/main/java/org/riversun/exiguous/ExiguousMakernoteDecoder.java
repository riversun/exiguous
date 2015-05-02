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

import java.util.Arrays;

public class ExiguousMakernoteDecoder {
	public static final int BIG_ENDIAN = 0;
	public static final int LITTLE_ENDIAN = 1;

	private int m_EndianMode = LITTLE_ENDIAN;

	private final int[] IFD_TAG_TYPE_SIZEOF = new int[] { -99999, 1, 1, 2, 4, 8, 1, 1, 2, 4, 8, 4, 8 };

	public ExiguousMakernoteDecoder(int endianMode) {
		m_EndianMode = endianMode;
	}

	public ExifIFDBlock analyzeMakerNote(int[] rMakerNote) {
		ExifIFDBlock ifd = new ExifIFDBlock();
		int ptr = 0;

		ptr += 12;

		int ifdCount = rMakerNote[ptr + 0] + rMakerNote[ptr + 1] * 16;

		ptr += 2;

		int makerNoteLength = rMakerNote.length;

		for (int j = 0; j < ifdCount; j++) {

			int[] fieldByteArray = Arrays.copyOfRange(rMakerNote, ptr, ptr + 12);

			long tag = getLongValue(new int[] { fieldByteArray[0], fieldByteArray[1] }, m_EndianMode);

			long type = getLongValue(new int[] { fieldByteArray[2], fieldByteArray[3] }, m_EndianMode);

			long sizeCount = getLongValue(new int[] { fieldByteArray[4], fieldByteArray[5], fieldByteArray[6], fieldByteArray[7] }, m_EndianMode);

			int[] dataArray = new int[] { fieldByteArray[8], fieldByteArray[9], fieldByteArray[10], fieldByteArray[11] };

			long dataOrOffsetValue = getLongValue(dataArray, m_EndianMode);

			long dataSize = sizeCount * IFD_TAG_TYPE_SIZEOF[(int) type];

			ExifIFDField field = new ExifIFDField();
			field.Tag = tag;
			field.TagName = ExifTagFinder.getTagName(tag);
			field.DataType = type;
			field.SizeCount = sizeCount;
			field.DataSize = dataSize;

			if (dataSize > 4) {

				// If data size is greater than 4bytes(greater than the data
				// area size),
				// it means the offset address of data contents.
				int dataPointer = (int) dataOrOffsetValue;

				if (dataPointer + field.DataSize <= makerNoteLength) {

					field.Data = Arrays.copyOfRange(rMakerNote, dataPointer, (int) (dataPointer + field.DataSize));
					field.LongData = dataOrOffsetValue;
					field.DataOffsetAddress = dataPointer;

				} else {
					System.err.println("Makernote is incorrect. " + "tag=" + toHex(tag) + " tagName=" + field.TagName + "dataOffset(start)=" + dataPointer + " dataEnd(end)="
							+ (dataPointer + field.DataSize) + " makerNoteLeng=" + makerNoteLength);
					continue;
				}

			} else {
				// If less eq than 4 bytes,it the byte sequence means data or
				// means IFD offset address.
				field.LongData = dataOrOffsetValue;
				field.Data = dataArray;
			}

			ifd.addField(field);

			ptr += 12;
		}

		return ifd;
	}

	private static String toHex(long val) {
		return "0x" + String.format("%04x", val);

	}

	/**
	 * Get the long value of the byte array.
	 * 
	 * @param srcByteArray
	 * @param endianMode
	 *            (BIG_ENDIAN=0 / LITTLE_ENDIAN=1)
	 * @return
	 */
	public long getLongValue(int[] srcByteArray, int endianMode) {
		long retValue = 0;

		long[] buff = new long[srcByteArray.length];

		int leng = buff.length;

		for (int i = 0; i < leng; i++) {

			if (endianMode == BIG_ENDIAN) {
				retValue += srcByteArray[i] * (long) (Math.pow(256, (leng - (i + 1))));
			}
			if (endianMode == LITTLE_ENDIAN) {
				retValue += srcByteArray[i] * (long) (Math.pow(256, i));

			}
		}
		return retValue;
	}

}
