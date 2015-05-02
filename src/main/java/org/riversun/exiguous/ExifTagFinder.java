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

import java.util.HashMap;

/**
 * Static class that performs the conversion of the tag code and tag names<br>
 * 
 * @author Tom Misawa (riversun.org@gmail.com)
 * 
 */
public class ExifTagFinder {

	// Collection for Forward lookup (key: tag code value: tag name)
	private static HashMap<String, String> m_TagNameMap = null;

	// Collection for Reverse lookup (key: tag name value: tag code)
	private static HashMap<String, String> m_InvTagNameMap = null;

	/**
	 * 
	 * Lookup the tag name from the tag code
	 * 
	 * @param tagCode
	 * 
	 * @return
	 */
	public static String getTagName(Long tagCode) {
		String ret = null;

		// Generates the tag name x tag code Collection in the first call.
		if (m_TagNameMap == null) {
			buildTagNames();
		}

		String key = String.format("0x%04x", tagCode);

		ret = m_TagNameMap.get(key);
		if (ret == null) {
			ret = "UNKNOWN(" + key + ")";
		}
		return ret;

	}

	/**
	 * Get the tag code (hex string) from the tag name
	 * 
	 * @param tagName
	 * @return
	 */
	public static String getTagCodeAsString(String tagName) {
		String ret = null;
		if (m_InvTagNameMap == null) {
			buildTagNames();
		}

		ret = m_InvTagNameMap.get(tagName);

		return ret;

	}

	private static void buildTagNames() {

		m_TagNameMap = new HashMap<String, String>();

		m_InvTagNameMap = new HashMap<String, String>();

		String[][] IFD0Tags = new String[][] { { "0x010e", "ImageDescription" }, { "0x010f", "Make" }, { "0x0110", "Model" }, { "0x0112", "Orientation" }, { "0x011a", "XResolution" },
				{ "0x011b", "YResolution" }, { "0x0128", "ResolutionUnit" }, { "0x0131", "Software" }, { "0x0132", "DateTime" }, { "0x013e", "WhitePoint" }, { "0x013f", "PrimaryChromaticities" },
				{ "0x0211", "YCbCrCoefficients" }, { "0x0213", "YCbCrPositioning" }, { "0x0214", "ReferenceBlackWhite" }, { "0x8298", "Copyright" }, { "0x8769", "ExifIFDPointer" } };

		String[][] SubIFDTags = new String[][] { { "0x829a", "ExposureTime" }, { "0x829d", "FNumber" }, { "0x8822", "ExposureProgram" }, { "0x8827", "ISOSpeedRatings" }, { "0x9000", "ExifVersion" },
				{ "0x9003", "DateTimeOriginal" }, { "0x9004", "DateTimeDigitized" }, { "0x9101", "ComponentsConfiguration" }, { "0x9102", "CompressedBitsPerPixel" },
				{ "0x9201", "ShutterSpeedValue" }, { "0x9202", "ApertureValue" }, { "0x9203", "BrightnessValue" }, { "0x9204", "ExposureBiasValue" }, { "0x9205", "MaxApertureValue" },
				{ "0x9206", "SubjectDistance" }, { "0x9207", "MeteringMode" }, { "0x9208", "LightSource" }, { "0x9209", "Flash" }, { "0x920a", "FocalLength" }, { "0x927c", "MakerNote" },
				{ "0x9286", "UserComment" }, { "0x9290", "SubsecTime" }, { "0x9291", "SubsecTimeOriginal" }, { "0x9292", "SubsecTimeDigitized" }, { "0xa000", "FlashPixVersion" },
				{ "0xa001", "ColorSpace" }, { "0xa002", "ExifImageWidth" }, { "0xa003", "ExifImageHeight" }, { "0xa004", "RelatedSoundFile" }, { "0xa005", "InteroperabilityIFDPointer" },
				{ "0xa20e", "FocalPlaneXResolution" }, { "0xa20f", "FocalPlaneYResolution" }, { "0xa210", "FocalPlaneResolutionUnit" }, { "0xa215", "ExposureIndex" }, { "0xa217", "SensingMethod" },
				{ "0xa300", "FileSource" }, { "0xa301", "SceneType" }, { "0xa302", "CFAPattern" } };

		String[][] InteroperabilityIFDTags = new String[][] { { "x0001", "InteroperabilityIndex" }, { "0x0002", "InteroperabilityVersion" }, { "0x1000", "RelatedImageFileFormat" },
				{ "0x1001", "RelatedImageWidth" }, { "0x1001", "RelatedImageLength" } };

		String[][] IFD1Tags = new String[][] { { "0x0100", "ImageWidth" }, { "0x0101", "ImageLength" }, { "0x0102", "BitsPerSample" }, { "0x0103", "Compression" },
				{ "0x0106", "PhotometricInterpretation" }, { "0x0111", "StripOffsets" }, { "0x0112", "Orientation" }, { "0x0115", "SamplesPerPixel" }, { "0x0116", "RowsPerStrip" },
				{ "0x0117", "StripByteConunts" }, { "0x011a", "XResolution" }, { "0x011b", "YResolution" }, { "0x011c", "PlanarConfiguration" }, { "0x0128", "ResolutionUnit" },
				{ "0x0201", "JpegInterchangeFormat" }, { "0x0202", "JpegInterchangeFormatLength" }, { "0x0211", "YCbCrCoefficients" }, { "0x0212", "YCbCrSubSampling" },
				{ "0x0213", "YCbCrPositioning" }, { "0x0214", "ReferenceBlackWhite" } };

		String[][] OtherIFDTags = new String[][] { { "0x00fe", "NewSubfileType" }, { "0x00ff", "SubfileType" }, { "0x012d", "TransferFunction" }, { "0x013b", "Artist" }, { "0x013d", "Predictor" },
				{ "0x013e", "WhitePoint" }, { "0x013f", "PrimaryChromaticities" }, { "0x0142", "TileWidth" }, { "0x0143", "TileLength" }, { "0x0144", "TileOffsets" }, { "0x0145", "TileByteCounts" },
				{ "0x014a", "SubIFDs" }, { "0x015b", "JPEGTables" }, { "0x828d", "CFARepeatPatternDim" }, { "0x828e", "CFAPattern" }, { "0x828f", "BatteryLevel" }, { "0x83bb", "IPTC/NAA" },
				{ "0x8773", "InterColorProfile" }, { "0x8824", "SpectralSensitivity" }, { "0x8825", "GPSInfo" }, { "0x8828", "OECF" }, { "0x8829", "Interlace" }, { "0x882a", "TimeZoneOffset" },
				{ "0x882b", "SelfTimerMode" }, { "0x920b", "FlashEnergy" }, { "0x920c", "SpatialFrequencyResponse" }, { "0x920d", "Noise" }, { "0x9211", "ImageNumber" },
				{ "0x9212", "SecurityClassification" }, { "0x9213", "ImageHistory" }, { "0x9214", "SubjectLocation" }, { "0x9215", "ExposureIndex" }, { "0x9216", "TIFF/EPStandardID" },
				{ "0x9290", "SubSecTime" }, { "0x9291", "SubSecTimeOriginal" }, { "0x9292", "SubSecTimeDigitized" }, { "0xa20b", "FlashEnergy" }, { "0xa20c", "SpatialFrequencyResponse" },
				{ "0xa214", "SubjectLocation" } };

		// IFD0Tags
		for (int i = 0; i < IFD0Tags.length; i++) {
			String tagCode = IFD0Tags[i][0];
			String tagDetail = IFD0Tags[i][1];
			m_TagNameMap.put(tagCode, tagDetail);
			m_InvTagNameMap.put(tagDetail, tagCode);

		}

		// SubIFDTags
		for (int i = 0; i < SubIFDTags.length; i++) {
			String tagCode = SubIFDTags[i][0];
			String tagDetail = SubIFDTags[i][1];
			m_TagNameMap.put(tagCode, tagDetail);
			m_InvTagNameMap.put(tagDetail, tagCode);

		}

		// InteroperabilityIFDTags
		for (int i = 0; i < InteroperabilityIFDTags.length; i++) {
			String tagCode = InteroperabilityIFDTags[i][0];
			String tagDetail = InteroperabilityIFDTags[i][1];
			m_TagNameMap.put(tagCode, tagDetail);
			m_InvTagNameMap.put(tagDetail, tagCode);

		}
		// IFD1Tags
		for (int i = 0; i < IFD1Tags.length; i++) {
			String tagCode = IFD1Tags[i][0];
			String tagDetail = IFD1Tags[i][1];
			m_TagNameMap.put(tagCode, tagDetail);
			m_InvTagNameMap.put(tagDetail, tagCode);

		}

		// OtherIFDTags
		for (int i = 0; i < OtherIFDTags.length; i++) {
			String tagCode = OtherIFDTags[i][0];
			String tagDetail = OtherIFDTags[i][1];
			m_TagNameMap.put(tagCode, tagDetail);
			m_InvTagNameMap.put(tagDetail, tagCode);

		}

	}
}
