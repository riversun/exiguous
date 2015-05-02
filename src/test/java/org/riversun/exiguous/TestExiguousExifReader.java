/*  finbin - Hi-speed search byte[] data from big byte[]
 *
 *  Copyright (c) 2015 Tom Misawa(riversun.org@gmail.com)
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

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

public class TestExiguousExifReader extends TestBase {
	@Rule
	public TestName name = new TestName();

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	static final String TEST01_JPG_FILEPATH = "src/test/resources/test01.jpg";

	@Test
	public void test_01_BasicIFD() {
		ExiguousExifReader exifReder = new ExiguousExifReader();

		ExifInfo exifInfo = new ExifInfo();

		exifReder.read(TEST01_JPG_FILEPATH, exifInfo);

		float fDelta = 0.01f;

		assertEquals("Canon", exifInfo.getMaker());
		assertEquals("Canon PowerShot SX130 IS", exifInfo.getModel());
		assertEquals("2015:05:02 19:26:50", exifInfo.getDateTime());
		assertEquals(0.00125f, exifInfo.getExposureTime(), fDelta);
		assertEquals("0230", exifInfo.getExifVersion());
		assertEquals(3.5, exifInfo.getFValue(), fDelta);
		assertEquals(640, exifInfo.getImageWidth());
		assertEquals(480, exifInfo.getImageHeight());

	}

}
