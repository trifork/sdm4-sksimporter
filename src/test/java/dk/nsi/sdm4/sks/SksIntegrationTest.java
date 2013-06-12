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

import dk.nsi.sdm4.sks.config.SksimporterApplicationConfig;
import dk.nsi.sdm4.testutils.TestDbConfiguration;
import org.apache.commons.io.FileUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.apache.commons.io.FileUtils.toFile;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
@ContextConfiguration(classes = {SksimporterApplicationConfig.class, TestDbConfiguration.class})
public class SksIntegrationTest
{
	@Rule
	public TemporaryFolder tmpDir = new TemporaryFolder();

	@Autowired
	JdbcTemplate jdbcTemplate;

	@Autowired
	SKSParser importer;

	@Test
	public void canImportTheCorrectNumberOfRecords() throws Throwable {
		importer.process(datasetDirWith("data/sks/SHAKCOMPLETE.TXT"), "");
		
		// FIXME: These record counts are only correct iff if duplicate keys are disregarted.
		// This is unfortunate. Keys are currently only considered based their SKSKode.
		// They should be a combination of type + kode + startdato based on the register doc.
		assertEquals(622, jdbcTemplate.queryForInt("SELECT COUNT(*) FROM Organisation WHERE Organisationstype = 'Sygehus'"));
		assertEquals(8451, jdbcTemplate.queryForInt("SELECT COUNT(*) FROM Organisation WHERE Organisationstype = 'Afdeling'"));
	}

    @Test
    public void validToAndFromInclusive() throws IOException, ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        // Rigshospitalet have the following date specified in input:
        // Valid from inclusive = 19760401
        // Valid to inclusive = 25000101

        importer.process(datasetDirWith("data/sks/SHAKCOMPLETE.TXT"), "");
        Date validTo = jdbcTemplate.queryForObject("SELECT ValidTo FROM Organisation WHERE Navn='Rigshospitalet'", Date.class);
        Date validFrom = jdbcTemplate.queryForObject("SELECT ValidFrom FROM Organisation WHERE Navn='Rigshospitalet'", Date.class);


                                                                //2500-01-02 00:00:00.0
        Date lastValidTo = formatter.parse("2500-01-01 23:59:58");
        Date firstInvalidTo = formatter.parse("2500-01-02 00:00:01");
        assertTrue(validTo.after(lastValidTo));
        assertTrue(validTo.before(firstInvalidTo));

        Date firstValidFrom = formatter.parse("1976-04-01 00:00:01");
        Date lastInvalidBefore = formatter.parse("1976-03-31 23:59:58");
        assertTrue(validFrom.after(lastInvalidBefore));
        assertTrue(validFrom.before(firstValidFrom));
    }

    @Test
    public void updatesValidToAndModifiedDate() throws IOException, InterruptedException {

        importer.process(datasetDirWith("data/sks/SHAKCOMPLETE.TXT"), "");
        Timestamp timestamp = new Timestamp((new Date()).getTime());
        Timestamp modified1 =
                jdbcTemplate.queryForObject("SELECT ModifiedDate FROM Organisation LIMIT 1", Timestamp.class);

        // Check no records are invalidated
        long cntFirstImport = jdbcTemplate.queryForLong("SELECT count(1) FROM Organisation WHERE ValidTo<=?", timestamp);

        // Check no invalid records exist

        Thread.sleep(1000);
        importer.process(datasetDirWith("data/sks2/SHAKCOMPLETE.TXT"), "");
        timestamp = new Timestamp((new Date()).getTime());

        // Check some records have been invalidated
        long cntSecondImport = jdbcTemplate.queryForLong("SELECT count(1) FROM Organisation WHERE ValidTo<=?", timestamp);
        assertTrue(cntSecondImport > cntFirstImport);

        // Check modified date has changed
        Timestamp modified2 = jdbcTemplate.queryForObject(
                "SELECT ModifiedDate FROM Organisation ORDER BY ModifiedDate DESC LIMIT 1", Timestamp.class);
        assertFalse(modified1.equals(modified2));
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
