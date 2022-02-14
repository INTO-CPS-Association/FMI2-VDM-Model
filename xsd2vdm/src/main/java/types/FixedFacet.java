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
package types;

public class FixedFacet extends Facet
{
	public FixedFacet(String value)
	{
		super("fixed", value);
	}

	@Override
	public String toVDM(String var, Type fieldtype)
	{
		switch (fieldtype.toString())
		{
			case "NormalizedString":
			case "AnyString":
			case "seq of char":
			case "seq1 of char":
				return var + " = \"" + value + "\"";
				
			default:
				return var + " = " + value;
		}
	}
}
