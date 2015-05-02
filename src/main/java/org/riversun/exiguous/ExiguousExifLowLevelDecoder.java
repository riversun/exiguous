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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Exif Low Level Decoder<br>
 * <br>
 * <br>
 * Exif specification one-point description <br>
 * to understand the code by Tom Misawa :)<br>
 * <br>
 * <br>
 * [Overview of Exif]<br>
 * First of all ,the Exif header shows the basic information.<br>
 * There are blocks to store data called IFD, it is a tree structure as a
 * directory.<br>
 * The IFD block, it can be a data storage area of ??a fixed length called a
 * field has more.<br>
 * Link to the child element of the tree structure (child become IFD block)
 * describes in the field. Specifically, it is specified by the offset value of
 * the child element<br>
 * Since the field is fixed length, since it is not suitable to hold large data,
 * when it is desired to hold large data, specify a data location in the field.<br>
 * <br>
 * [File structure of Exif]<br>
 * 0xFFD8 = begins with SOI (Start Of Image), and ends in 0xFFD9 = EOI (End Of
 * Image)<br>
 * ExifJpeg file SOI, APP1, ....., it is recorded in the order of EOI<br>
 * APP1 is in the leading role of Exif, contents of APP1 is recorded in TIFF
 * format<br>
 * <br>
 * [Prerequisite knowledge of file structure]<br>
 * First of all, to the position byte order have a specified byte order, big
 * endian. From there the specified ones.<br>
 * Although the location that specifies the location in the file are also
 * several places, the offset value is a position that specifies the byte order.<br>
 * <br>
 * [APP1 header of the structure]<br>
 * APP1 2byte: fixed value 0xFFE1<br>
 * Segment length 2byte: APP1 number of bytes<br>
 * Exif identification code 6byte: fixed value 0x45 78 69 66 00 (Exif \ 0 \ 0)<br>
 * Byte order (endian) 2byte: selection value 0x49 0x49-&gt; LittleEndian<br>
 * 0x002A 2byte: fixed value (. However, important endian is applied from here)<br>
 * IFD offset 4byte: offset of the next IFD<br>
 * <br>
 * [Structure of IFD block]<br>
 * Count 2byte: the number of fields<br>
 * Field 12byte: field body (which is present in the IFD block only counts)<br>
 * <br>
 * [Structure of IFD Field<br>
 * Tag 2byte: it shows the meaning of the field. For example, we show the
 * example, 'photo of horizontal resolution'.<br>
 * Type 2byte: field of the data format (such as BYTE or ASCII)<br>
 * Count 4byte: the length of the data -&gt; For example type is BYTE, if the count
 * is 0x00000020 1byte * 32 = 32 bytes<br>
 * Offset to the value 4byte: If the length of the data is 4byte below, the data
 * itself, equal to or greater than, the position of the value is stored<br>
 * [Note]<br>
 * While Exif is defined as extended format of the JFIF format (JPEG is the
 * compression method),<br>
 * In this library, JFIF is not treated. <br>
 * 
 * @author Tom Misawa (riversun.org@gmail.com)
 *
 */
public class ExiguousExifLowLevelDecoder {

	private final int SOI = 0xFFD8;// Start Of Image
	private final int FOI = 0xFFD9;// End Of Image

	// Starting code of APP1
	private final int APP1_START_CODE = 0xFFE1;

	// Starting code of JFIF format.Not supported APP0 starting code.
	private final int APP0_START_CODE = 0xFFE0;

	private final int[] EXIF_DEFINE_CODE = new int[] { 0x45, 0x78, 0x69, 0x66, 0x00, 0x00 };// EXIF
	private final int[] BYTE_ORDER_BIGENDIAN = new int[] { 0x4D, 0x4D };
	private final int[] BYTE_ORDER_LITTLEENDIAN = new int[] { 0x49, 0x49 };

	// ENDIAN by motorola
	public static final int BIG_ENDIAN = 0;

	// ENDIAN by intel
	public static final int LITTLE_ENDIAN = 1;

	private int m_EndianMode = LITTLE_ENDIAN;

	private RandomAccessFile m_TargetRAFile = null;

	private boolean m_IsLogging = false;

	private long m_Ptr_SOI = 0;
	private long m_Ptr_APP1_Offset = 0;
	private long m_Ptr_ExifDefineCode = 0;
	private long m_Ptr_ExifOffset = 0;
	private long m_Ptr_IFD0Offset = 0;
	private long m_Ptr_ExifIFDOffset = 0;
	private long m_Ptr_GPSIFDOffset = 0;
	private long m_APP1_Segment_Size = 0;

	private ExifIFDBlock m_IFD0 = null;
	private ExifIFDBlock m_IFD1 = null;
	private ExifIFDBlock m_Exif_IFD = null;
	private ExifIFDBlock m_GPS_IFD = null;

	// type names
	private final String[] IFD_TAG_TYPE_NAME = new String[] { "-NOTHING-", "BYTE", "ASCII", "SHORT", "LONG", "RATIONAL", "SBYTE", "UNDEFINED", "SSHORT", "SLONG", "SRATIONAL", "FLOAT", "DFLOAT" };

	// size in byte for each types
	private final int[] IFD_TAG_TYPE_SIZEOF = new int[] { -99999, 1, 1, 2, 4, 8, 1, 1, 2, 4, 8, 4, 8 };

	private void openFile(String fileName) {
		try {
			m_TargetRAFile = new RandomAccessFile(fileName, "r");
		} catch (FileNotFoundException e) {

			e.printStackTrace();
		}
	}

	public int getEndianMode() {
		return m_EndianMode;
	}

	/**
	 * Read the Exif formatted file
	 * 
	 * @param fileName
	 */
	public boolean read(String fileName) {
		openFile(fileName);
		return readIFD();
	}

	/**
	 * 
	 * read IFD if
	 * 
	 * @return success:return true/In case if the JFIF format:return false
	 */
	private boolean readIFD() {

		log(m_Ptr_IFD0Offset, "IFD0 Analyze");

		m_Ptr_IFD0Offset = getIFD0Offset();

		if (m_Ptr_IFD0Offset < 0) {
			return false;
		}

		m_IFD0 = IFDAnalyze(m_Ptr_IFD0Offset);

		// You can look up by tag name or tag code like this
		// IFDField modelNameFiled1 = m_IFD0.getFieldByName("Model");
		// IFDField modelNameFiled2 = m_IFD0.getField("0x0110");

		if (m_IFD0.NextIFDOffset > 0) {
			log(m_IFD0.NextIFDOffset, "IFD1 Analyze");

			m_IFD1 = IFDAnalyze(m_IFD0.NextIFDOffset + m_Ptr_ExifOffset);
		}

		// The address for pulling the exifIFD refers to the address that has
		// been set to "ExifIFDPointer" of ifd0.
		ExifIFDField ExifIFDPointerFiled = m_IFD0.getFieldByName("ExifIFDPointer");

		if (ExifIFDPointerFiled != null) {

			long exifIFDPointer = ExifIFDPointerFiled.LongData;
			m_Ptr_ExifIFDOffset = exifIFDPointer + m_Ptr_ExifOffset;

			log(m_Ptr_ExifIFDOffset, "Exif IFD Analyze");

			m_Exif_IFD = IFDAnalyze(m_Ptr_ExifIFDOffset);
		}

		// The address for pulling the gpdIFD refers to the address that has
		// been set to "GPSInfo" of ifd0.
		ExifIFDField GPSIFDPointerFiled = m_IFD0.getFieldByName("GPSInfo");

		if (GPSIFDPointerFiled != null) {
			long GPSIFDPointer = GPSIFDPointerFiled.LongData;
			m_Ptr_GPSIFDOffset = GPSIFDPointer + m_Ptr_ExifOffset;
			log(m_Ptr_GPSIFDOffset, "GPS IFD Analyze");

			m_GPS_IFD = IFDAnalyze(m_Ptr_GPSIFDOffset);
		}

		return true;
	}

	private String toHexStr(long val) {
		return "0x" + String.format("%04x", val) + "(" + String.format("%06d", val) + ")";

	}

	/**
	 * Get the start address of the Exif IFD0 (position in the file)
	 * 
	 * @return
	 */
	private long getIFD0Offset() {
		long retVal = 0;
		boolean analyzeInProgress = true;
		long fPtr = 0;

		while (analyzeInProgress) {

			long chunk = get2byteAsBigEndian(fPtr);

			if (chunk == SOI) {
				// Get SOI pointer ===
				log(fPtr, "This is SOI_ADDRESS");
				m_Ptr_SOI = fPtr;

				fPtr += 2;

				continue;

			} else if (chunk == APP1_START_CODE) {
				// Get APP1 starting pointer ===
				log(fPtr, "This is APP1 start address");
				m_Ptr_APP1_Offset = fPtr;

				// add 2byte to move forward
				fPtr += 2;

				// Get segment size of APP1 ===
				// The segment after APP1_POINTER(2byte) is the segment
				// length(2bytes) of APP1
				m_APP1_Segment_Size = get2byteAsBigEndian(fPtr);

				log(fPtr, "App1 segment length find. Length=" + toHexStr(m_APP1_Segment_Size) + "bytes");

				// add 2byte to move forward
				fPtr += 2;

				// Get Exif Identifying code ====

				// Stored 6 bytes like 'E'　'x'　'i'　'f'　'\0'　'\0'
				int[] rExifDefineCode = getMultiByteFromCurrentFile(fPtr, 6);

				// Confirm array of bytes 'E'　'x'　'i'　'f'　'\0'　'\0' here
				// if not, it's an error.
				if (!compareBetween(rExifDefineCode, EXIF_DEFINE_CODE)) {
					log(fPtr, "'Exif' Information Tag in file is incorrect.(>_<)");
					break;
				}

				fPtr += 6;

				// Acquisition of 0 points in the Exif (offset value)====
				// Set the offset address
				m_Ptr_ExifOffset = fPtr;
				log(fPtr, "Exif　Zero Offset Pointer find. Offset=" + m_Ptr_ExifOffset);

				// Acquisition of the byte order (or big-endian or
				// little-endian)===

				int[] rByteOrder = getMultiByteFromCurrentFile(fPtr, 2);

				if (compareBetween(rByteOrder, BYTE_ORDER_LITTLEENDIAN)) {
					m_EndianMode = LITTLE_ENDIAN;
					log(fPtr, "Byte Order Code find. This data is LITTLE ENDIAN");
				} else if (compareBetween(rByteOrder, BYTE_ORDER_BIGENDIAN)) {
					m_EndianMode = BIG_ENDIAN;
					log(fPtr, "Byte Order Code find. This data is BIG ENDIAN");
				} else {
					log(fPtr, "ENDIAN Information is incorrect.(>_<)");
				}

				// And pass through the 0x002A==
				fPtr += 2;
				// Incoming AutoText Exif "0x002A" is from this position
				// 0x002A is useless,so ignore.

				// Get a pointer to the IFD0 ====
				fPtr += 2;

				int[] ifd0OffsetData = getMultiByteFromCurrentFile(fPtr, 4);

				long Ptr_IFD0OffsetRelative = getLongValue(ifd0OffsetData, m_EndianMode);

				retVal = Ptr_IFD0OffsetRelative + m_Ptr_ExifOffset;

				log(fPtr, "IFD0　Relative Pointer:" + toHexStr(Ptr_IFD0OffsetRelative));
				log(fPtr, "IFD0　Absolute Pointer:" + toHexStr(Ptr_IFD0OffsetRelative + m_Ptr_ExifOffset));

				analyzeInProgress = false;
				break;

			}

			// Processing is not performed if the JFIF format. (Do not support.)
			// or if 128byte go ahead and at the end analysis , and I cannot not
			// found anything, just break.
			if (chunk == APP0_START_CODE || fPtr > 128) {
				retVal = -999;
				return retVal;
			}

			fPtr++;
		}

		return retVal;
	}

	/**
	 * Analyze the IFD block
	 * 
	 * @param startPtr
	 *            Start address (position in the file)
	 * @return
	 */
	private ExifIFDBlock IFDAnalyze(long startPtr) {
		ExifIFDBlock ifd = new ExifIFDBlock();

		long fPtr = startPtr;
		int[] rCount = getMultiByteFromCurrentFile(fPtr, 2);
		fPtr += 2;

		long count = getLongValue(rCount, m_EndianMode);
		log(fPtr, "IFD Field Count:" + toHexStr(count));

		for (int i = 0; i < count; i++) {
			int[] fieldByteArray = getMultiByteFromCurrentFile(fPtr, 12);

			long tag = getLongValue(new int[] { fieldByteArray[0], fieldByteArray[1] }, m_EndianMode);

			long type = getLongValue(new int[] { fieldByteArray[2], fieldByteArray[3] }, m_EndianMode);

			long sizeCount = getLongValue(new int[] { fieldByteArray[4], fieldByteArray[5], fieldByteArray[6], fieldByteArray[7] }, m_EndianMode);

			int[] dataArray = new int[] { fieldByteArray[8], fieldByteArray[9], fieldByteArray[10], fieldByteArray[11] };

			long data = getLongValue(dataArray, m_EndianMode);

			long dataSize = sizeCount * IFD_TAG_TYPE_SIZEOF[(int) type];

			log(fPtr, "IFD Field(" + i + "): Tag=" + ExifTagFinder.getTagName(tag) + "(" + toHexStr(tag) + ")" + " TypeName=" + IFD_TAG_TYPE_NAME[(int) type] + " Size->" + sizeCount + "*"
					+ IFD_TAG_TYPE_SIZEOF[(int) type] + "(byte)=" + dataSize + " data=" + toHexStr(data));

			ExifIFDField field = new ExifIFDField();
			field.Tag = tag;
			field.TagName = ExifTagFinder.getTagName(tag);
			field.DataType = type;
			field.SizeCount = sizeCount;
			field.DataSize = dataSize;

			if (dataSize > 4) {
				// In the case of the field in the data area (4byte) or more of
				// data,
				// location offset address of that data is specified
				long dataPointer = data + m_Ptr_ExifOffset;

				field.Data = getMultiByteFromCurrentFile(dataPointer, (int) field.DataSize);
				field.LongData = data;

				field.DataOffsetAddress = m_Ptr_ExifOffset;

			} else {
				// If less than 4 bytes, or data is intact data, show the offset
				// address of a certain IFD
				field.LongData = data;
				field.Data = dataArray;
			}

			ifd.addField(field);

			fPtr += 12;

		}

		// Last 2byte offset address ,it's a next IFD's address.
		int[] rNextIFDOffset = getMultiByteFromCurrentFile(fPtr, 2);

		long nextIFDOffset = getLongValue(rNextIFDOffset, m_EndianMode);
		long absoluteNextIFDOffset = nextIFDOffset + m_Ptr_ExifOffset;

		ifd.NextIFDOffset = nextIFDOffset;
		ifd.NextIFDAbsoluteOffset = absoluteNextIFDOffset;

		log(fPtr, "IFD Next-IFD Offset=" + toHexStr(ifd.NextIFDOffset));

		if (ifd.NextIFDOffset > 0) {
			log(fPtr, "IFD Next-IFD Absolute Offset=" + toHexStr(ifd.NextIFDAbsoluteOffset));
		}
		return ifd;

	}

	/**
	 * Compare byte[] and byte[]
	 * 
	 * @param src
	 * @param dest
	 * @return If both are the same,return true
	 */
	private boolean compareBetween(int[] src, int[] dest) {
		boolean ret = false;

		if (!(src == null || dest == null || src.length != dest.length)) {
			for (int i = 0; i < src.length; i++) {
				if (src[i] != dest[i]) {

					ret = false;
					break;
				}
				ret = true;
			}
		}
		return ret;
	}

	/**
	 * 
	 * Get the long value of the byte array.<br>
	 * (You can not put a byte array that exceeds the 8byte is the limit of
	 * long. )<br>
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

	/**
	 * Get 1byte
	 * 
	 * @param startPos
	 * @return
	 */
	private long get1byte(long startPos) {
		long ret = 0;
		long byte1 = 0;
		byte1 = getMultiByteFromCurrentFile(startPos, 1)[0];
		ret = byte1;
		return ret;
	}

	/**
	 * Get 2bytes as bigEndian
	 * 
	 * @param startPos
	 * @return
	 */
	private long get2byteAsBigEndian(long startPos) {
		long ret = 0;
		long firstByte = get1byte(startPos);
		long secondByte = get1byte(startPos + 1);
		ret = firstByte * 256 + secondByte;
		return ret;

	}

	/**
	 * Get multibytes
	 * 
	 * @param startPos
	 * 
	 * @param len
	 * 
	 * @return
	 */
	public int[] getMultiByteFromCurrentFile(long startPos, int len) {

		int[] ret = new int[len];
		byte[] byteArray = new byte[len];

		seek(startPos);

		try {
			m_TargetRAFile.readFully(byteArray, 0, len);
		} catch (IOException e) {
			e.printStackTrace();
		}

		for (int i = 0; i < len; i++) {
			// unsigned conversion( byte -> int conversion)
			ret[i] = byteArray[i] & 0xFF;
		}

		byteArray = null;
		return ret;
	}

	/**
	 * Set seek position of the current opening file
	 * 
	 * @param position
	 *            　absolute pos of the current opening file
	 */
	private void seek(long position) {
		try {
			m_TargetRAFile.seek(position);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public ExifIFDBlock getIFD0() {
		return m_IFD0;
	}

	public ExifIFDBlock getIFD1() {
		return m_IFD1;
	}

	public ExifIFDBlock getExif_IFD() {
		return m_Exif_IFD;
	}

	public ExifIFDBlock getGPS_IFD() {
		return m_GPS_IFD;
	}

	/**
	 * Enabling logging
	 * 
	 * @param isLogging
	 */
	public void setLogging(boolean isLogging) {
		m_IsLogging = isLogging;
	}

	/**
	 * write debug log
	 * 
	 * @param fpos
	 * @param text
	 */
	private void log(long fpos, String text) {
		if (m_IsLogging) {
			System.out.println(toHexStr(fpos) + ":" + text);
		}
	}

}
