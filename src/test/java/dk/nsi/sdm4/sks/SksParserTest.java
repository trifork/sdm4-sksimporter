/**
 * The MIT License
 *
 * Original work sponsored and donated by National Board of e-Health (NSI), Denmark
 * (http://www.nsi.dk)
 *
 * Copyright (C) 2011 National Board of e-Health (NSI), Denmark (http://www.nsi.dk)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package dk.nsi.sdm4.sks;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;

import static org.apache.commons.io.FileUtils.toFile;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class SksParserTest
{
    private static String completeTxt = "data/sks/SHAKCOMPLETE.TXT";
    private static String completeXml = "data/sks/SHAKCOMPLETE.XML";
    private static String delta = "data/sks/SHAKDELTA.TXT";

	private SKSParser importer;

	@Rule
	public TemporaryFolder tmpDir = new TemporaryFolder();

	@Before
	public void setup() {
		importer = new SKSParser();
	}

	@Test(expected = IllegalArgumentException.class)
    public void shouldNotAcceptEmptyInputFileSet() throws IOException {
        importer.validateInputStructure(tmpDir.newFolder());
    }
    
    @Test
    public void shouldAcceptSKSCompleteTxtFile() throws IOException {
        assertTrue(importer.validateInputStructure(datasetDirWith(completeTxt)));
    }

	@Test
    public void shouldNotAcceptSKSCompleteXmlFile() throws IOException {
        assertFalse(importer.validateInputStructure(datasetDirWith(completeXml)));
    }
    
    @Test
    public void shouldAcceptSKSDeltaFile() throws IOException {
        assertTrue(importer.validateInputStructure(datasetDirWith(delta)));
    }

	private File datasetDirWith(String filename) throws IOException {
		File datasetDir = tmpDir.newFolder();
		FileUtils.copyFileToDirectory(getFile(filename), datasetDir);
		return datasetDir;
	}

	private File getFile(String filename) {
		return toFile(getClass().getClassLoader().getResource(filename));
	}
}
