//
//  Copyright (c) Motorola, Inc. 1998-2001.  All rights reserved.
//
//  Author(s):
//		John Menarek
//

package com.mot.icsd.voxml.framework;


import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.io.StringReader;
import java.io.IOException;
import java.util.Stack;

import org.xml.sax.Attributes;
import org.xml.sax.HandlerBase;
import org.xml.sax.Parser;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.ParserFactory;
import org.xml.sax.InputSource;
import org.xml.sax.helpers.DefaultHandler;

import com.mot.icsd.voxml.core.CoreException;


import java.io.BufferedReader;
import java.io.ByteArrayInputStream;

public class XMLtoGSL
extends DefaultHandler {


    //
    // Public static methods
    //

	/** Convert a source grammar rule to a target grammar String
	 *  @param t The AST to convert
	 *  @return "NOT IMLEMENTED".
	 */
	public static String neutral2gslRule( String r ) {
		return( "NOT IMPLEMENTED" );
	}

	/** Convert a XML grammar or expression to a GSL grammar or expression.
	 *  @param	src		the JSGF version of the rule.
	 *  @param	topRuleName		the grammar rule to be recognized.
	 *  @return The GSL version of the rule.
	 */
	public static String convert( String src, String topRuleName ) throws CoreException 
	{

        try 
        {
            XMLtoGSL transform = new XMLtoGSL();

            XMLReader parser = (XMLReader)Class.forName(DEFAULT_PARSER_NAME).newInstance();
            parser.setContentHandler(transform);
            parser.setErrorHandler(transform);

            long before = System.currentTimeMillis();

            parser.parse( new InputSource( new ByteArrayInputStream( src.getBytes() ) ) );
            long after = System.currentTimeMillis();
			if( fError == true )
			{
				throw new CoreException( sError );
			}
        }
        catch (org.xml.sax.SAXParseException spe)
        {
			throw new CoreException( spe, "SAX parse exception:"+ spe.getMessage()  );
        }
        catch (org.xml.sax.SAXException se) 
        {
			throw new CoreException( se, "SAX exception:" + se.getMessage() );
        }
		catch( InstantiationException instantation )
		{
			throw new CoreException( instantation, "Could not instantiate:" + DEFAULT_PARSER_NAME );
		}
		catch( IllegalAccessException access )
		{
			throw new CoreException( access, "Could not access:" + DEFAULT_PARSER_NAME );
		}
		catch( ClassNotFoundException notFound )
		{
			throw new CoreException( notFound, "Could not find:" + DEFAULT_PARSER_NAME );
		}
		catch( IOException io )
		{
			throw new CoreException( io, "I/O exception during parsing:" + io.getMessage() );
		}
		return( sABNF );
    }


    //
    // DocumentHandler methods
    //

    /** Start document. */
    public void startDocument() 
    {
//System.out.println("startDocument()");
		fError = false;
    } // startDocument()
    public void endDocument() 
    {
//System.out.println("endDocument()");
    } // startDocument()


    /** Start element. */
    public void startElement(String uri, String elementName, String raw, Attributes atts)
    {
		if( fError == true )
		{
			return;
		}
//System.out.println("startElement() elementName:" + elementName + " uri:" + uri );
		curElement = elementName;
		
		if( elementName.equals("grammar") )
		{
			String version = null;
			String language = null;
			String mode = null;
			String rootRule = null;

		   	for (int i = 0; i < atts.getLength(); i++) 
			{
	    		String attName = atts.getLocalName(i);
			 	if( attName.equals("lang") )
			 	{
					language = atts.getValue(i);
				}
			 	else if( attName.equals("version") )
			 	{
					version = atts.getValue(i);
				}
			 	else if( attName.equals("mode") )
			 	{
					mode = atts.getValue(i);
				}
			 	else if( attName.equals("root") )
			 	{
					rootRule = atts.getValue(i);
				}
			 	else if( attName.equals("xmlns") )
			 	{
					//Since the first pass won't do external references why worry about name spaces?
				}
			 	else
			 	{
					fError = true;
					sError = new String( "Element grammar, error do not recognize attribute:" + attName );
					return;
			 	}
		   	}
			sABNF = new String("#ABNF " + version + " ISO8859-1;\n");
			if( language != null )
			{
				sABNF = sABNF.concat( "language " + language + ";\n");
			}
			if( mode != null )
			{
				sABNF = sABNF.concat( "mode " + mode + ";\n");
			}
			if( rootRule != null )
			{
				sABNF = sABNF.concat( "root $" + rootRule + ";\n");
			}
		}
		else if( elementName.equals("alias") )
		{
			String aliasName = null;
			String attributeUri = null;

		   	for (int i = 0; i < atts.getLength(); i++) 
			{
	    		String attName = atts.getLocalName(i);
			 	if( attName.equals("name") )
			 	{
					aliasName = atts.getValue(i);
				}
			 	else if( attName.equals("uri") )
			 	{
					attributeUri = atts.getValue(i);
				}
			 	else
			 	{
					fError = true;
					sError = new String("Element alias, error do not recognize attribute:" + attName );
					return;
			 	}
		   	}
			sABNF = sABNF.concat("alias $(" + attributeUri + ") $$" + aliasName );
		}
		else if( elementName.equals("rule") )
		{
			String ruleId = null;
			String ruleScope = null;

		   	for (int i = 0; i < atts.getLength(); i++) 
			{
	    		String attName = atts.getLocalName(i);
			 	if( attName.equals("id") )
			 	{
					ruleId = atts.getValue(i);
				}
			 	else if( attName.equals("scope") )
			 	{
					ruleScope = atts.getValue(i);
				}
			 	else
			 	{
					fError = true;
					sError = new String("Element rule, error do not recognize attribute:" + attName);
					return;
			 	}
		   	}
			if( ruleScope == null )
			{
				sABNF = sABNF.concat("$" + ruleId + " = ");
			}
			else
			{
				sABNF = sABNF.concat( ruleScope + " $" + ruleId + " = ");
			}
		}
		else if( elementName.equals("ruleref") )
		{
		   	for (int i = 0; i < atts.getLength(); i++) 
			{
	    		String attName = atts.getLocalName(i);
//System.out.println("ruleref attribute:" + attName );
			 	if( attName.equals("alias") )
			 	{
					sABNF = sABNF.concat( "$$" + atts.getValue(i) + " " );
				}
			 	else if( attName.equals("uri") )
			 	{
	    			String attributeUri = atts.getValue(i);
					if( attributeUri.indexOf( "http" ) != -1 )
					{
						sABNF = sABNF.concat( "$(" + attributeUri + ")" );
					}
					else
					{
						attributeUri = attributeUri.substring(1);
						sABNF = sABNF.concat( "$" + attributeUri + " " );
					}
				}
			 	else
			 	{
					fError = true;
					sError = new String("Element ruleref, error do not recognize attribute:" + attName );
					return;
			 	}
		   	}
		}
		else if( elementName.equals("example") ) 
		{
			//Dump examples
		}
		else if( elementName.equals("one-of") ) 
		{
			fFirstItem = true;
			fIsOneOf = true;
			sABNF = sABNF.concat( "(" );
			szRepeatStack = repeatStack.size();
		}
		else if( elementName.equals("item") ) 
		{
			String repeatValue = undefined;

			/*
			* Are we inside an One of element?
			*/
			if( fIsOneOf == true )
			{
				/* 
				* We are inside of a "one-of" element. Now
				* we have to check to see if its the second element
				* so we can put the "|" in front of it
				*
				* The call to size on the repeat stack is to make
				* sure the first "item" element has been finished
				* 
				* example:
				*	 <one-of>
				*	   <item>
				*	     <item repeat="4"><ruleref uri="#digit"/></item>
				*	     #
				*	   </item>
				*	   <item>
				*	     * 9
				*	   </item>
				*	 </one-of>
				* is ($digit <4>  #  | * 9); in abnf
				* "* 9" needs the "|" infront of it not the rule reference
				*/
				if( ( fFirstItem == false ) && 
					( repeatStack.size() == szRepeatStack ) )
				{
					sABNF = sABNF.concat( " | " );
				}

				fFirstItem = false;
			   	for (int i = 0; i < atts.getLength(); i++) 
				{
		    		String attName = atts.getLocalName(i);
				 	if( attName.equals("weight") )
				 	{
						sABNF = sABNF.concat( " /" + atts.getValue(i) + "/ " );
					}
				 	else if( attName.equals("repeat") )
				 	{
						String repeatAttribute = atts.getValue(i);
						if( ( repeatAttribute.equals("?") )	||
							( repeatAttribute.equals("optional") ) )
						{
							repeatValue = new String( "<0-1> " );
						}
						else if( repeatAttribute.equals("*") )
						{
							repeatValue = new String( "<0-> " );
						}
						else if(repeatAttribute.equals("+") )
						{
							repeatValue = new String( "<1-> " );
						}
						else
						{
							repeatValue = new String( "<" + repeatAttribute + "> " );
						}
					}
				 	else
				 	{
						fError = true;
						sError = new String("Element item inside of one-of, error do not recognize attribute:" + attName );
						return;
				 	}
			   	}
			}
			else
			{
			   	for (int i = 0; i < atts.getLength(); i++) 
				{
		    		String attName = atts.getLocalName(i);
				 	if( attName.equals("repeat") )
				 	{
						String repeatAttribute = atts.getValue(i);
						if( ( repeatAttribute.equals("?") )	||
							( repeatAttribute.equals("optional") ) )
						{
							repeatValue = new String( "<0-1> " );
						}
						else if( repeatAttribute.equals("*") )
						{
							repeatValue = new String( "<0-> " );
						}
						else if(repeatAttribute.equals("+") )
						{
							repeatValue = new String( "<1-> " );
						}
						else
						{
							repeatValue = new String( "<" + repeatAttribute + "> " );
						}
					}
				 	else
				 	{
						fError = true;
						sError = new String("Element item, error do not recognize attribute:" + attName  );
						return;
				 	}
			   	}
			}
			repeatStack.push( repeatValue );
		}
		else if( elementName.equals("tag") ) 			  
		{
		}
		else if( elementName.equals("meta") ) 			  
		{
			//dump meta data
		}
		else if( elementName.equals("import") ) 			  
		{
			fError = true;
			sError = new String("\nLexicons are not supported at this time" );
			return;
		}
		else
		{
			fError = true;
			sError = new String("\nError do not recognize element:" + elementName );
			return;
		}
    } 

    /** Characters. */
    public void characters(char ch[], int start, int length)  
    {

		if( fError == true )
		{
			return;
		}
		String elementText = new String( ch, 0, length );
//System.out.println("characters() string:" + elementText );
		elementText = elementText.trim();
		if( 0 < elementText.length() )
		{
			if( curElement.equals( "tag" ) )
			{
				sABNF = sABNF.concat( "{" + elementText + "}" );
			}
			else if( curElement.equals( "item" ) )
			{
				sABNF = sABNF.concat( elementText );
			}
			else if( curElement.equals( "example" ) )
			{
				//Eat example text
			}
			else if( curElement.equals( "rule" ) )
			{
				sABNF = sABNF.concat( elementText + " " );
			}
			else if( curElement.equals( "ruleref" ) )
			{
				sABNF = sABNF.concat( " " + elementText + " " );
			}
			else
			{
				if( elementText.indexOf( "<?" ) == -1 )
				{
					fError = true;
					sError = new String("Unknown element:"  + curElement +
						" element text:" + elementText);
					return;
				}
			}
		}
    } // characters(char[],int,int);

    public void endElement(String uri, String elementName, String qName )
    {
		if( fError == true )
		{
			return;
		}
//System.out.println("endElement() elementName:" + elementName );

		if( ( elementName.equals( "alias" ) ) ||
			( elementName.equals( "rule" ) ) )
		{
			//Clean state
			sABNF = sABNF.concat(";\n");
		}
		if( elementName.equals( "one-of" ) ) 
		{
			fIsOneOf = false;
			sABNF = sABNF.concat( ")" );
		}
		if( elementName.equals( "item" ) ) 
		{
			String repeatValue = ( String )repeatStack.pop();
									 
			if( repeatValue.equals( undefined ) == false )
			{
				sABNF = sABNF.concat( repeatValue );
			}
		}
	}


    /** Ignorable whitespace. */
    public void ignorableWhitespace(char ch[], int start, int length) 
    {
//System.out.println("ignorableWhitespace()");
    } // ignorableWhitespace(char[],int,int);


    //
    // ErrorHandler methods
    //

    /** Warning. */
    public void warning(SAXParseException ex) {

		fError = true;
		sError = new String("[Warning] "+
                           getLocationString(ex)+": "+
                           ex.getMessage());
    }

    /** Error. */
    public void error(SAXParseException ex) {

		fError = true;
		sError = new String("[Error] "+
                           getLocationString(ex)+": "+
                           ex.getMessage());
    }

    /** Fatal error. */
    public void fatalError(SAXParseException ex) throws SAXException {

		fError = true;
		sError = new String("[Fatal Error] "+
                           getLocationString(ex)+": "+
                           ex.getMessage());
//        throw ex;
    }

    /** Returns a string of the location. */
    private String getLocationString(SAXParseException ex) {
        StringBuffer str = new StringBuffer();

        String systemId = ex.getSystemId();
        if (systemId != null) {
            int index = systemId.lastIndexOf('/');
            if (index != -1)
                systemId = systemId.substring(index + 1);
            str.append(systemId);
        }
        str.append(':');
        str.append(ex.getLineNumber());
        str.append(':');
        str.append(ex.getColumnNumber());

        return str.toString();

    } // getLocationString(SAXParseException):String


    //
    // Constants
    //

    /** Default parser name. */
    public static final String
    DEFAULT_PARSER_NAME = "org.apache.xerces.parsers.SAXParser";
	
	private static boolean fError;
	private static String sError;
	private static String sABNF;

	boolean fIsOneOf = false;
	boolean fFirstItem = false;
	final String undefined = new String( "UNDEFINED" );
	String curElement = null;
	Stack repeatStack = new Stack();
	int szRepeatStack = 0;
} 

