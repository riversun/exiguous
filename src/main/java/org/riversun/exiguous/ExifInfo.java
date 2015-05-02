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

/**
 * Model of Exif Information
 * 
 * @author Tom Misawa (riversun.org@gmail.com)
 * 
 */
public class ExifInfo {

	private String maker = "";
	private String model = "";
	private String dateTime;
	private String xResolution = "";
	private String yResolution = "";
	private float exposureTime;
	private String exifVersion = "";
	private float fValue;
	private int[] makerNote;

	private int imageWidth;
	private int imageHeight;

	private String GpsIFDVersion;
	private double GpsLongitude;
	private String GpsLongitudeRef;
	private double GpsLatitude;
	private String GpsLatitudeRef;
	private String GpsLatitudeDDMMSSsss;
	private String GpsLongitudeDDMMSSsss;
	private float GpsImageDirection;

	private boolean enabled = false;

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public String getMaker() {
		return maker;
	}

	public void setMaker(String maker) {
		this.maker = maker;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public String getDateTime() {
		return dateTime;
	}

	public void setDateTime(String dateTime) {
		this.dateTime = dateTime;
	}

	public String getXResolution() {
		return xResolution;
	}

	public void setXResolution(String resolution) {
		xResolution = resolution;
	}

	public String getYResolution() {
		return yResolution;
	}

	public void setYResolution(String resolution) {
		yResolution = resolution;
	}

	public float getExposureTime() {
		return exposureTime;
	}

	public void setExposureTime(float exposureTime) {
		this.exposureTime = exposureTime;
	}

	public String getExifVersion() {
		return exifVersion;
	}

	public void setExifVersion(String exifVersion) {
		this.exifVersion = exifVersion;
	}

	public float getFValue() {
		return fValue;
	}

	public void setFValue(float value) {
		fValue = value;
	}

	public int[] getMakerNote() {
		return makerNote;
	}

	public void setMakerNote(int[] makerNote) {
		this.makerNote = makerNote;
	}

	public double getGpsLongitude() {
		return GpsLongitude;
	}

	public void setGpsLongitude(double longitude) {
		GpsLongitude = longitude;
	}

	public String getGpsLongitudeRef() {
		return GpsLongitudeRef;
	}

	public void setGpsLongitudeRef(String longitudeRef) {
		GpsLongitudeRef = longitudeRef;
	}

	public double getGpsLatitude() {
		return GpsLatitude;
	}

	public void setGpsLatitude(double latitude) {
		GpsLatitude = latitude;
	}

	public String getGpsLatitudeRef() {
		return GpsLatitudeRef;
	}

	public void setGpsLatitudeRef(String latitudeRef) {
		GpsLatitudeRef = latitudeRef;
	}

	public String getGpsIFDVersion() {
		return GpsIFDVersion;
	}

	public void setGpsIFDVersion(String version) {
		GpsIFDVersion = version;
	}

	public float getGpsImageDirection() {
		return GpsImageDirection;
	}

	public void setGpsImageDirection(float imageDirection) {
		GpsImageDirection = imageDirection;
	}

	public String getGpsLatitudeDDMMSSsss() {
		return GpsLatitudeDDMMSSsss;
	}

	public void setGpsLatitudeDDMMSSsss(String latitudeDDMMSSsss) {
		GpsLatitudeDDMMSSsss = latitudeDDMMSSsss;
	}

	public String getGpsLongitudeDDMMSSsss() {
		return GpsLongitudeDDMMSSsss;
	}

	public void setGpsLongitudeDDMMSSsss(String longitudeDDMMSSsss) {
		GpsLongitudeDDMMSSsss = longitudeDDMMSSsss;
	}

	public int getImageWidth() {
		return imageWidth;
	}

	public void setImageWidth(int imageWidth) {
		this.imageWidth = imageWidth;
	}

	public int getImageHeight() {
		return imageHeight;
	}

	public void setImageHeight(int imageHeight) {
		this.imageHeight = imageHeight;
	}

	@Override
	public String toString() {
		return "ExifInfo [maker=" + maker + ", model=" + model + ", dateTime=" + dateTime + ", xResolution=" + xResolution + ", yResolution=" + yResolution + ", exposureTime=" + exposureTime
				+ ", exifVersion=" + exifVersion + ", fValue=" + fValue + ", makerNote=" + Arrays.toString(makerNote) + ", imageWidth=" + imageWidth + ", imageHeight=" + imageHeight
				+ ", GpsIFDVersion=" + GpsIFDVersion + ", GpsLongitude=" + GpsLongitude + ", GpsLongitudeRef=" + GpsLongitudeRef + ", GpsLatitude=" + GpsLatitude + ", GpsLatitudeRef="
				+ GpsLatitudeRef + ", GpsLatitudeDDMMSSsss=" + GpsLatitudeDDMMSSsss + ", GpsLongitudeDDMMSSsss=" + GpsLongitudeDDMMSSsss + ", GpsImageDirection=" + GpsImageDirection + "]";
	}

}
