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

package xsd2vdm;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.xml.sax.Attributes;
import org.xml.sax.Locator;

public class XSDElement
{
	private final int line;
	private final File file;
	private final String prefix;
	private final String type;

	private final Map<String, String> attributes = new HashMap<String, String>();;
	private final List<XSDElement> children = new Vector<XSDElement>();
	private final static Map<String, XSDElement> referenceMap = new HashMap<String, XSDElement>();
	
	private String annotation;
	
	/**
	 * Constant for xml:lang attribute.
	 */
	public final static XSDElement XML_LANG = new XSDElement("xml:lang", "xs:string");
	
	public XSDElement(String prefix, String qName, Attributes attributes, Locator locator)
	{
		this.line = locator.getLineNumber();
		this.file = getFile(locator);
		this.prefix = prefix;
		this.type = qName;
		
		for (int i=0; i<attributes.getLength(); i++)
		{
			this.attributes.put(attributes.getQName(i), attributes.getValue(i));
		}
		
		if (!qName.equals("xs:attribute"))	// Only reference non-attributes?
		{
			String name = attributes.getValue("name");
			
			if (name != null)
			{
				if (prefix != null && !prefix.isEmpty())
				{
					referenceMap.put(prefix + ":" + name, this);
				}
				else
				{
					referenceMap.put(name, this);
				}
			}
		}
	}
	
	public XSDElement(Locator locator)
	{
		this.line = locator.getLineNumber();
		this.file = getFile(locator);
		this.prefix = "";
		this.type = null;	// eg. a Content string
	}
	
	private XSDElement(String xmlName, String xmlType)
	{
		this.file = new File("xml");
		this.line = 0;
		this.prefix = "";
		this.type = "xs:attribute";
		
		attributes.put("name", xmlName);
		attributes.put("type", xmlType);
	}
	
	private File getFile(Locator locator)
	{
		try
		{
			return new File(new URI(locator.getSystemId()));
		}
		catch (URISyntaxException e)
		{
			return new File("?");
		}
	}
	
	public String getPrefix()
	{
		return prefix;
	}

	public String getType()
	{
		return type;
	}
	
	public String getAttr(String name)
	{
		return attributes.get(name);
	}
	
	public boolean hasAttr(String name)
	{
		return getAttr(name) != null;
	}
	
	public Map<String, String> getAttrs()
	{
		return attributes;
	}

	public List<XSDElement> getChildren()
	{
		return children;
	}
	
	public boolean hasChild(String string)
	{
		for (XSDElement child: children)
		{
			if (child.isType(string))
			{
				return true;
			}
		}
		
		return false;
	}

	public void add(XSDElement element)
	{
		children.add(element);
	}
	
	public static XSDElement lookup(String name)
	{
		XSDElement e = referenceMap.get(name);
		
		if (e == null)
		{
			throw new RuntimeException("Cannot find element base/ref/name " + name);
		}
		
		return e;
	}

	public boolean isReference()
	{
		return attributes.get("ref") != null;
	}

	public boolean isType(String type)
	{
		return this.type.equals(type);
	}

	public XSDElement getFirstChild()
	{
		return children.isEmpty() ? null : children.get(0);
	}

	public List<XSDElement> getOtherChildren()
	{
		return children.isEmpty() ? null : children.subList(1, children.size());
	}

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		
		sb.append("<");
		sb.append(type);
		
		for (Entry<String, String> pair: attributes.entrySet())
		{
			sb.append(" ");
			sb.append(pair.getKey());
			sb.append("=\"");
			sb.append(pair.getValue());
			sb.append("\"");
		}
		
		if (children.isEmpty())
		{
			sb.append(" />\n");
		}
		else
		{
			sb.append(">\n");
			
			for (XSDElement child: children)
			{
				sb.append(child);
			}
	
			sb.append("</");
			sb.append(type);
			sb.append(">\n");
		}

		return sb.toString();
	}
	
	/**
	 * The toVDM method produces a raw VDM model of XML document elements.
	 *
	 *	Element ::
	 *		$loc		: seq1 of char
	 *		type		: seq1 of char
	 *		attrs		: seq of Attribute
	 *		children	: seq of Element | Content;
	 *
	 *	Attribute ::
	 *		$loc		: seq1 of char
	 *		name		: seq1 of char
	 *		value		: seq1 of char;
	 *
	 *	Content = seq of char;
	 */

	private static String INDENT = "  ";
	
	public String toVDM()
	{
		return toVDM(INDENT);
	}
	
	protected String toVDM(String indent)
	{
		StringBuilder sb = new StringBuilder();
		String indent2 = indent + INDENT;
		String indent3 = indent2 + INDENT;
		
		sb.append(indent);
		sb.append("mk_Element\n");
		sb.append(indent);
		sb.append("(\n");
		sb.append(indent2);
		sb.append("\"");
		sb.append(file.getName());
		sb.append(":");
		sb.append(line);
		sb.append("\",\n");
		sb.append(indent2);
		sb.append("\"");
		sb.append(type);
		sb.append("\",\n");

		if (attributes.isEmpty())
		{
			sb.append(indent2);
			sb.append("[],\n");
		}
		else
		{
			sb.append(indent2);
			sb.append("[\n");
	
			String sep = "";
	
			for (Entry<String, String> pair: attributes.entrySet())
			{
				sb.append(sep);
				sb.append(indent3);
				sb.append("mk_Attribute(\"");
				sb.append(file.getName());
				sb.append(":");
				sb.append(line);
				sb.append("\", \"");
				sb.append(pair.getKey());
				sb.append("\", ");
				sb.append(valueToVDM(pair.getValue()));
				sb.append(")");
				
				sep = ",\n";
			}
	
			sb.append("\n");
			sb.append(indent2);
			sb.append("],\n");
		}
		
		if (children.isEmpty())
		{
			sb.append(indent2);
			sb.append("[]\n");
		}
		else
		{
			sb.append(indent2);
			sb.append("[\n");
			
			String sep = "";
			
			for (XSDElement child: children)
			{
				sb.append(sep);
				sb.append(child.toVDM(indent3));
				sep = ",\n";
			}
	
			sb.append("\n");
			sb.append(indent2);
			sb.append("]\n");
		}
		
		sb.append(indent);
		sb.append(")");

		return sb.toString();
	}
	
	/**
	 * Most XML content string are just turned into VDM "quoted strings". But if the content
	 * parses as a number or sequence of numbers, it is turned into -1.23 or [1, 2, 3] etc.  
	 */
	protected String valueToVDM(String value)
	{
		StringBuilder sb = new StringBuilder();
		
		try
		{
			if (value.matches("^([+-.0123456789eE]+\\s*)+$"))
			{
				List<String> nums = new Vector<String>();

				Pattern p = Pattern.compile("[+-.0123456789eE]+");
				Matcher m = p.matcher(value);
				
				while (m.find())
				{
					double num = Double.parseDouble(m.group());
					nums.add(Double.toString(num));
				}
				
				if (nums.size() == 1)
				{
					sb.append(nums.get(0));
				}
				else
				{
					String comma = "";
					sb.append("[");
					
					for (String num: nums)
					{
						sb.append(comma);
						sb.append(num);
						comma = ", ";
					}
					
					sb.append("]");
				}
			}
			else
			{
				double num = Double.parseDouble(value);
				sb.append(num);
			}
		}
		catch (NumberFormatException e)
		{
			sb.append("\"");
			sb.append(value);
			sb.append("\"");
		}
		
		return sb.toString();
	}
	
	@Override
	public boolean equals(Object other)
	{
		if (other instanceof XSDElement)
		{
			XSDElement e = (XSDElement)other;
			return type.equals(e.type) && attributes.equals(e.attributes);
		}
		else
		{
			return false;
		}
	}
	
	@Override
	public int hashCode()
	{
		return type.hashCode();
	}

	public void setAnnotation(String text)
	{
		this.annotation = text;
	}
	
	public String getAnnotation()
	{
		return annotation;
	}
	
	public int getLineNumber()
	{
		return line;
	}
	
	public File getFile()
	{
		return file;
	}
}
