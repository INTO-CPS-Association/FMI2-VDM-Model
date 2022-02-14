/******************************************************************************
 *
 *	Copyright (c) 2017-2022, INTO-CPS Association,
 *	c/o Professor Peter Gorm Larsen, Department of Engineering
 *	Finlandsgade 22, 8200 Aarhus N.
 *
 *	This file is part of the INTO-CPS toolchain.
 *
 *	xsd2vdm is free software: you can redistribute it and/or modify
 *	it under the terms of the GNU General Public License as published by
 *	the Free Software Foundation, either version 3 of the License, or
 *	(at your option) any later version.
 *
 *	xsd2vdm is distributed in the hope that it will be useful,
 *	but WITHOUT ANY WARRANTY; without even the implied warranty of
 *	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *	GNU General Public License for more details.
 *
 *	You should have received a copy of the GNU General Public License
 *	along with xsd2vdm. If not, see <http://www.gnu.org/licenses/>.
 *	SPDX-License-Identifier: GPL-3.0-or-later
 *
 ******************************************************************************/
package values;

import org.xml.sax.Attributes;
import org.xml.sax.Locator;

import types.BasicType;

public class AnyValue extends VDMValue
{
	private String token;
	
	public AnyValue(String qName, Attributes attributes, Locator locator)
	{
		super(new BasicType("token"), locator);
		
		StringBuilder sb = new StringBuilder();
		String sep = "";
		sb.append(qName);
		
		for (int i=0; i<attributes.getLength(); i++)
		{
			sb.append(sep);
			sb.append(attributes.getType(i));
			sb.append("=");
			sb.append(attributes.getValue(i));
			sep = " ";
		}
		
		this.token = sb.toString();
	}
	
	@Override
	public String toString()
	{
		return "mk_token(\"" + token + "\")";
	}

	@Override
	public String toVDM(String indent)
	{
		return indent + toString();
	}

	public void setField(String qName, VDMValue value)
	{
		token = token + "; " + qName;
	}
	
	@Override
	public boolean hasAny()
	{
		return true;
	}
}
