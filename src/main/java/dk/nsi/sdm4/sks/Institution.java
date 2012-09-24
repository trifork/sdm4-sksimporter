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

import dk.nsi.sdm4.core.domain.AbstractStamdataEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Date;

@Entity(name = "Organisation")
public class Institution extends AbstractStamdataEntity
{
	private Date validFrom;
	private Date validTo;
	
	private String navn;
	private String nummer;

	private final InstitutionType type;

	public enum InstitutionType
	{
		HOSPITAL_DEPARTMENT, HOSPITAL
	}

	public Institution(InstitutionType organisationstype)
	{
		this.type = organisationstype;
	}

	public Date getValidTo()
	{
		return validTo;
	}

	public void setValidTo(Date validTo)
	{
		this.validTo = validTo;
	}

	@Column
	public String getNavn()
	{
		return navn;
	}

	public void setNavn(String navn)
	{
		this.navn = navn;
	}

	@Id
	@Column
	public String getNummer()
	{
		return nummer;
	}

	public void setNummer(String nummer)
	{
		this.nummer = nummer;
	}

	@Column
	public String getOrganisationstype()
	{
		if (type == InstitutionType.HOSPITAL_DEPARTMENT)
		{
			return "Afdeling";
		}
		else if (type == InstitutionType.HOSPITAL)
		{
			return "Sygehus";
		}

		return null;
	}

	public void setValidFrom(Date validFrom)
	{
		this.validFrom = validFrom;
	}

	@Override
	public Date getValidFrom()
	{
		return validFrom;
	}
}
