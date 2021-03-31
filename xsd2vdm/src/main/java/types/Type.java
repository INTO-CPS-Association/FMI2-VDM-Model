/**
 * This file is part of the INTO-CPS toolchain.
 *
 * Copyright (c) 2017-2021, INTO-CPS Association,
 * c/o Professor Peter Gorm Larsen, Department of Engineering
 * Finlandsgade 22, 8200 Aarhus N.
 *
 * All rights reserved.
 *
 * THIS PROGRAM IS PROVIDED UNDER THE TERMS OF GPL VERSION 3 LICENSE OR
 * THIS INTO-CPS ASSOCIATION PUBLIC LICENSE VERSION 1.0.
 * ANY USE, REPRODUCTION OR DISTRIBUTION OF THIS PROGRAM CONSTITUTES
 * RECIPIENT'S ACCEPTANCE OF THE OSMC PUBLIC LICENSE OR THE GPL 
 * VERSION 3, ACCORDING TO RECIPIENTS CHOICE.
 *
 * The INTO-CPS toolchain  and the INTO-CPS Association Public License are
 * obtained from the INTO-CPS Association, either from the above address, from
 * the URLs: http://www.into-cps.org, and in the INTO-CPS toolchain distribution.
 * GNU version 3 is obtained from: http://www.gnu.org/copyleft/gpl.html.
 *
 * This program is distributed WITHOUT ANY WARRANTY; without
 * even the implied warranty of  MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE, EXCEPT AS EXPRESSLY SET FORTH IN THE
 * BY RECIPIENT SELECTED SUBSIDIARY LICENSE CONDITIONS OF
 * THE INTO-CPS ASSOCIATION.
 *
 * See the full INTO-CPS Association Public License conditions for more details.
 */

package types;

import java.util.List;

import org.xml.sax.Locator;

import values.VDMValue;

abstract public class Type
{
	protected List<String> comments = null;
	
	public abstract String signature();
	
	public List<String> getComments()
	{
		return comments;
	}
	
	public void setComments(List<String> comments)
	{
		this.comments = comments;
	}

	public boolean matches(Type type)
	{
		return this.equals(type);
	}
	
	@Override
	public boolean equals(Object other)
	{
		return toString().equals(other.toString());
	}
	
	@Override
	public int hashCode()
	{
		return toString().hashCode();
	}
	
	@Override
	public String toString()
	{
		return signature();
	}

	abstract public VDMValue valueOf(String avalue, Locator locator);

	private static final int MAXLINE = 100;
	
	protected void appendComments(StringBuilder sb, List<String> comments, String prefix)
	{
		if (comments != null)
		{
			if (prefix.isEmpty())
			{
				sb.append("/**\n");
			}
			
			for (String comment: comments)
			{
				while (comment.length() > MAXLINE)
				{
					int space = 0;
					
					for (space = MAXLINE; space > 0; space--)
					{
						if (Character.isWhitespace(comment.charAt(space)))
						{
							break;
						}
					}
					
					
					sb.append(prefix.isEmpty() ? " * " : prefix + "-- ");
					sb.append(comment.substring(0, space).trim() + "\n");
					comment = comment.substring(space);
				}
				
				sb.append(prefix.isEmpty() ? " * " : prefix + "-- ");
				sb.append(comment.trim() + "\n");
			}
			
			if (prefix.isEmpty())
			{
				sb.append(" */\n");
			}
		}
	}

}
