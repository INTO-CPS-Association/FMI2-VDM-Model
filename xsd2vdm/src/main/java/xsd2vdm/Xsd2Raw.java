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

package xsd2vdm;

import java.io.File;
import java.io.FileInputStream;
import java.io.PrintStream;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import javax.xml.XMLConstants;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.xml.sax.SAXException;

public class Xsd2Raw
{
	public static void main(String[] args) throws Exception
	{
		File xsdFile = null;
		File xmlFile = null;
		PrintStream vdmFile = null;
		
		if (args.length == 1)
		{
			xsdFile = null;
			xmlFile = new File(args[0]);
			vdmFile = System.out;
		}
		else if (args.length == 2)
		{
			xsdFile = null;
			xmlFile = new File(args[0]);
			vdmFile = new PrintStream(new File(args[1]));
		}
		else if (args.length == 3)
		{
			xsdFile = new File(args[0]);
			xmlFile = new File(args[1]);
			vdmFile = new PrintStream(new File(args[2]));
		}
		else
		{
			System.err.println("Usage: Xsd2Raw <XML file> [<VDM out file>]");
			System.err.println("       Xsd2Raw <XSD file> <XML file> [<VDM out file>]");
			System.exit(1);
		}
		
		if (xsdFile != null && !xsdFile.exists())
		{
			System.err.println("Cannot open " + xsdFile);
			System.exit(1);
		}
		
		if (!xmlFile.exists())
		{
			System.err.println("Cannot open " + xmlFile);
			System.exit(1);
		}
		
		Xsd2Raw xsd2raw = new Xsd2Raw(xsdFile, xmlFile, vdmFile);
		xsd2raw.process();
	}

	private final File xsdFile;
	private final File xmlFile;
	private final PrintStream vdmFile;
	
	public Xsd2Raw(File xsdFile, File xmlFile, PrintStream vdmFile)
	{
		this.xsdFile = xsdFile;
		this.xmlFile = xmlFile;
		this.vdmFile = vdmFile;
	}
	
	private void validate(File xsd, File xml) throws Exception
	{
		try
		{
			// Note that we pass a stream to allow the validator to determine the
			// encoding, rather than passing a File, which seems to use default encoding.
			Source xmlFile = new StreamSource(new FileInputStream(xml));
			xmlFile.setSystemId(xml.toURI().toASCIIString());
			Source xsdFile = new StreamSource(new FileInputStream(xsd));
			xsdFile.setSystemId(xsd.toURI().toASCIIString());
			SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			
			Schema schema = schemaFactory.newSchema(xsdFile);
			Validator validator = schema.newValidator();
			validator.validate(xmlFile);
		}
		catch (SAXException e)
		{
			throw new Exception("XML validation: " + e);		// Raw exception gives file/line/col
		}
		catch (Exception e)
		{
			throw new Exception("XML validation: " + e.getMessage());
		}
	}
	
	public void process() throws Exception
	{
		if (xsdFile != null)
		{
			validate(xsdFile, xmlFile);
		}
		
		Xsd2VDM.loadProperties(xmlFile);
		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser saxParser = factory.newSAXParser();
		
		Set<File> processed = new HashSet<File>();
		List<String> includes = new Vector<String>();
		List<XSDElement> roots = new Vector<XSDElement>();

		File parent = xmlFile.getParentFile();
		includes.add(xmlFile.getName());
		
		while (!includes.isEmpty())
		{
			File file = new File(parent, includes.remove(0));
			
			if (!processed.contains(file))
			{
				XSDSaxHandler handler = new XSDSaxHandler();
				saxParser.parse(file, handler);
				
				processed.add(file);
				includes.addAll(handler.getIncludes());
				roots.addAll(handler.getRoots());
			}
		}
		
		vdmFile.println("/**");
		vdmFile.println(" * VDM created from " + xmlFile + " on " + new Date());
		vdmFile.println(" * DO NOT EDIT!");
		vdmFile.println(" */");
		vdmFile.println("values");
		
		for (XSDElement root: roots)
		{
			String varname = root.getFile().getName().replaceAll("\\.", "_");
			
			vdmFile.print("  " + varname + " =\n");
			vdmFile.print(root.toVDM());
			vdmFile.println(";\n");
		}
	}
}
