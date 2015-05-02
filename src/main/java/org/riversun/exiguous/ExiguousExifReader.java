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
 * Exiguous Common Exif Reader &amp; Decoder v0.1.0<br>
 * Exif 2.1.1 Available<br>
 * 
 * Read famous Exif tags <br>
 * If you want to read Unknown tags or original tags,you can get it by using
 * ExiguousExifLowLevelDecoder.<br>
 * 
 * 
 * @author Tom Misawa (riversun.org@gmail.com)
 * 
 */
public class ExiguousExifReader {

	private boolean mIsLogging = false;

	private ExiguousExifLowLevelDecoder mLowLevelDecoder = new ExiguousExifLowLevelDecoder();

	public void setLogging(boolean loggingEnabled) {
		mIsLogging = loggingEnabled;
	}

	/**
	 * Analyze EXIF info of specified file
	 * 
	 * @param fileName
	 * @param exifInfo
	 */
	public void read(String fileName, ExifInfo exifInfo) {

		mLowLevelDecoder.setLogging(mIsLogging);
		boolean fileReadSuccessFlag = mLowLevelDecoder.read(fileName);

		if (fileReadSuccessFlag) {
			int endianMode = mLowLevelDecoder.getEndianMode();

			ExifIFDBlock IFD0 = mLowLevelDecoder.getIFD0();
			ExifIFDBlock IFD1 = mLowLevelDecoder.getIFD1();
			ExifIFDBlock exif_IFD = mLowLevelDecoder.getExif_IFD();
			ExifIFDBlock gps_IFD = mLowLevelDecoder.getGPS_IFD();

			// IFD0 ====
			// Those that contain in ASCII format, is easily take out.
			String maker = IFD0.getFieldByName("Make").getStringData();
			String model = IFD0.getFieldByName("Model").getStringData();
			String dateTime = IFD0.getFieldByName("DateTime").getStringData();

			// ExifIFD ====
			if (exif_IFD != null) {
				String exifVersion = exif_IFD.getFieldByName("ExifVersion").getStringData();

				// Contain as fraction type, first 4byte is molecules, second is
				// the 4byte denominator
				int[] rExposureTime = exif_IFD.getFieldByName("ExposureTime").Data;

				long bET1 = getLongValue(rExposureTime, 0, 4, endianMode);
				long bET2 = getLongValue(rExposureTime, 4, 4, endianMode);

				// exposureTime
				float exposureTime = (float) bET1 / (float) bET2;

				int[] rFValue = exif_IFD.getFieldByName("FNumber").Data;
				long bFV1 = getLongValue(rFValue, 0, 4, endianMode);
				long bFV2 = getLongValue(rFValue, 4, 4, endianMode);

				// F-value
				float fValue = (float) bFV1 / (float) bFV2;

				int[] rWidth = exif_IFD.getFieldByName("ExifImageWidth").getData();
				int imageWidth = (int) getLongValue(rWidth, 0, 4, endianMode);

				int[] rHeight = exif_IFD.getFieldByName("ExifImageHeight").getData();
				int imageHeight = (int) getLongValue(rHeight, 0, 4, endianMode);

				exifInfo.setEnabled(true);
				exifInfo.setMaker(maker);
				exifInfo.setModel(model);
				exifInfo.setDateTime(dateTime);
				exifInfo.setExposureTime(exposureTime);
				exifInfo.setExifVersion(exifVersion);
				exifInfo.setFValue(fValue);

				exifInfo.setImageWidth(imageWidth);
				exifInfo.setImageHeight(imageHeight);

			}
			// GPS IFD ====

			if (gps_IFD != null) {
				// GPS IFD Version
				String GPSIfdVersion = "";
				if (gps_IFD.getField("0x0000") != null) {
					int[] rGpsifd_version = gps_IFD.getField("0x0000").Data;

					for (int i = 0; i < rGpsifd_version.length; i++) {
						GPSIfdVersion += String.valueOf(rGpsifd_version[i]) + ".";
					}
					GPSIfdVersion = GPSIfdVersion.substring(0, GPSIfdVersion.length() - 1);
				}

				// GPS IFD latitude
				int[] rGpslatitude_ref = gps_IFD.getField("0x0001").Data;
				long lGpslatitude_ref = getLongValue(rGpslatitude_ref, 0, 4, 1);
				String GPSLatitudeRef = String.valueOf((char) lGpslatitude_ref);
				int[] rGpslatitude = gps_IFD.getField("0x0002").Data;

				long gps_lat_DD = getLongValue(rGpslatitude, 0, 4, mLowLevelDecoder.getEndianMode());
				long gps_lat_DD1 = getLongValue(rGpslatitude, 4, 4, mLowLevelDecoder.getEndianMode());

				long gps_lat_MM = getLongValue(rGpslatitude, 8, 4, mLowLevelDecoder.getEndianMode());
				long gps_lat_MM1 = getLongValue(rGpslatitude, 12, 4, mLowLevelDecoder.getEndianMode());

				long gps_lat_SS0 = getLongValue(rGpslatitude, 16, 4, mLowLevelDecoder.getEndianMode());
				long gps_lat_SS1 = getLongValue(rGpslatitude, 20, 4, mLowLevelDecoder.getEndianMode());

				double latitude0 = (double) gps_lat_DD / (double) gps_lat_DD1;
				double latitude1 = ((double) gps_lat_MM / (double) gps_lat_MM1) / 60d;
				double latitude2 = ((double) gps_lat_SS0 / (double) gps_lat_SS1) / 3600d;
				double GPSLatitude = latitude0 + latitude1 + latitude2;
				String DD_lat = Long.toString(gps_lat_DD / gps_lat_DD1);
				double dMM_lat = ((double) gps_lat_MM / (double) gps_lat_MM1);
				int iMM_lat = (int) dMM_lat;
				double dSS_lat = (dMM_lat - iMM_lat) * 60 + (double) gps_lat_SS0 / (double) gps_lat_SS1;
				String MM_lat = Integer.toString(iMM_lat);

				// The format is deg:minute:seconds
				String DDMMSSSSS_lat = DD_lat + ":" + MM_lat + ":" + String.format("%3.3f", dSS_lat) + "";
				exifInfo.setGpsLatitudeDDMMSSsss(DDMMSSSSS_lat);

				// GPD IFD longitude
				int[] rGpslongitude_ref = gps_IFD.getField("0x0003").Data;
				long lGpslongitude_ref = getLongValue(rGpslongitude_ref, 0, 4, 1);
				String GPSLongitudeRef = String.valueOf((char) lGpslongitude_ref);

				int[] gps_longitude = gps_IFD.getField("0x0004").Data;

				long gps_lng_DD = getLongValue(gps_longitude, 0, 4, mLowLevelDecoder.getEndianMode());
				long gps_lng_DD1 = getLongValue(gps_longitude, 4, 4, mLowLevelDecoder.getEndianMode());

				long gps_lng_MM = getLongValue(gps_longitude, 8, 4, mLowLevelDecoder.getEndianMode());
				long gps_lng_MM1 = getLongValue(gps_longitude, 12, 4, mLowLevelDecoder.getEndianMode());

				long gps_lng_SS0 = getLongValue(gps_longitude, 16, 4, mLowLevelDecoder.getEndianMode());
				long gps_lng_SS1 = getLongValue(gps_longitude, 20, 4, mLowLevelDecoder.getEndianMode());

				double longitude0 = (double) gps_lng_DD / (double) gps_lng_DD1;
				double longitude1 = ((double) gps_lng_MM / (double) gps_lng_MM1) / 60d;
				double longitude2 = ((double) gps_lng_SS0 / (double) gps_lng_SS1) / 3600d;
				double GPSLongitude = longitude0 + longitude1 + longitude2;

				String DD_lng = Long.toString(gps_lng_DD / gps_lng_DD1);
				double dMM_lng = ((double) gps_lng_MM / (double) gps_lng_MM1);
				int iMM_lng = (int) dMM_lng;
				double dSS_lng = (dMM_lng - iMM_lng) * 60 + (double) gps_lng_SS0 / (double) gps_lng_SS1;
				String MM_lng = Integer.toString(iMM_lng);

				// The format is deg:minute:seconds
				String DDMMSSSSS_lng = DD_lng + ":" + MM_lng + ":" + String.format("%3.3f", dSS_lng) + "";
				exifInfo.setGpsLongitudeDDMMSSsss(DDMMSSSSS_lng);

				exifInfo.setGpsIFDVersion(GPSIfdVersion);

				exifInfo.setGpsLatitude(GPSLatitude);
				exifInfo.setGpsLatitudeRef(GPSLatitudeRef);
				exifInfo.setGpsLongitude(GPSLongitude);
				exifInfo.setGpsLongitudeRef(GPSLongitudeRef);

				if (gps_IFD.getField("0x0011") != null) {
					int[] rGPSImageDirection = gps_IFD.getField("0x0011").Data;
					long direction0 = getLongValue(rGPSImageDirection, 0, 4, mLowLevelDecoder.getEndianMode());
					long direction1 = getLongValue(rGPSImageDirection, 4, 4, mLowLevelDecoder.getEndianMode());
					exifInfo.setGpsImageDirection((float) direction0 / (float) direction1);

				}

			}

			// MakerNote ====
			if (exif_IFD != null) {
				ExifIFDField MakerNoteField = exif_IFD.getFieldByName("MakerNote");
				if (MakerNoteField != null) {
					exifInfo.setMakerNote(MakerNoteField.Data);
				}
			}
			// **************************************************
		} else {
			// If Failed to read or File is JFIF formatted
			exifInfo.setEnabled(false);
		}
	}

	public int getEndianMode() {
		return mLowLevelDecoder.getEndianMode();
	}

	/**
	 * To extract the long value from int [] in consideration of the endian
	 * 
	 * @param data
	 * @param startPos
	 * @param len
	 * @param endian
	 * @return
	 */
	private long getLongValue(int[] data, long startPos, int len, int endian) {

		long longValue = 0;

		for (int i = 0; i < len; i++) {
			if (endian == ExiguousExifLowLevelDecoder.LITTLE_ENDIAN) {
				longValue += data[(int) (startPos + i)] * (long) Math.pow(256, i);

			} else if (endian == ExiguousExifLowLevelDecoder.BIG_ENDIAN) {
				longValue += data[(int) (startPos + len - (i + 1))] * (long) Math.pow(256, i);
			}
		}
		return longValue;
	}

}
