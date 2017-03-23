# Overview
'exiguous' is a java library for Exif (Exchangeable image file format) data.

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.riversun/exiguous/badge.svg)](https://maven-badges.herokuapp.com/maven-central/org.riversun/exiguous)

You can easy to access Exif tags from your code.

It is licensed under [The MIT License](http://opensource.org/licenses/MIT).

# Examples
- Java code here
```java
package org.example;

import org.riversun.exiguous.ExifInfo;
import org.riversun.exiguous.ExiguousExifReader;

public class Sample {

	public static void main(String[] args) {

		String jpgFilePath = "src/test/resources/test01.jpg";

		ExiguousExifReader exifReder = new ExiguousExifReader();

		ExifInfo exifInfo = new ExifInfo();

		exifReder.read(jpgFilePath, exifInfo);

		System.out.println(exifInfo);
	}
}
```


- it results in
```
ExifInfo [maker=Canon, model=Canon PowerShot SX130 IS, dateTime=2015:05:02 19:26:50, xResolution=, yResolution=, exposureTime=0.00125, exifVersion=0230, fValue=3.5, makerNote=null, imageWidth=640, imageHeight=480, GpsIFDVersion=null, GpsLongitude=0.0, GpsLongitudeRef=null, GpsLatitude=0.0, GpsLatitudeRef=null, GpsLatitudeDDMMSSsss=null, GpsLongitudeDDMMSSsss=null, GpsImageDirection=0.0]
```

# More Details
See javadoc as follows.

https://riversun.github.io/javadoc/exiguous/

# Downloads
## maven
- You can add dependencies to maven pom.xml file.
```xml
<dependency>
  <groupId>org.riversun</groupId>
  <artifactId>exiguous</artifactId>
  <version>0.3.0</version>
</dependency>
```
