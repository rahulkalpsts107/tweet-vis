package com.twitter.sentimental;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.alchemyapi.api.AlchemyAPI;
import com.alchemyapi.api.AlchemyAPI_KeywordParams;
import com.alchemyapi.api.AlchemyAPI_NamedEntityParams;
import com.alchemyapi.api.AlchemyAPI_Params;

public class SentimentAnalyzer 
{
	public AlchemyAPI alchemyRef;
	public boolean isAuthenticated;
	private String _apiKey;
	
	public SentimentAnalyzer()
	{
		isAuthenticated = false;
		try
		{
			alchemyRef = AlchemyAPI.GetInstanceFromFile(Constants.fileLoc);
			isAuthenticated = true;
			_apiKey = null;
			loadAPIKey(Constants.fileLoc);
		}
		catch(IOException ioe)
		{
			System.out.println(ioe.getMessage());
		}
	}
	
	public String analyzeText(String text)
	{
		if(isAuthenticated)
		{
			try
			{
				Document d = alchemyRef.TextGetTextSentiment(text);
				if(d == null)
					return null;
				else
					return getStringFromDocument(d);
			} 
			catch (Exception exp)
			{
				System.out.println(exp.getMessage());
			}
		}
		return null;
	}
	
    private String getStringFromDocument(Document doc) 
    {
        try {
            DOMSource domSource = new DOMSource(doc);
            StringWriter writer = new StringWriter();
            StreamResult result = new StreamResult(writer);

            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            transformer.transform(domSource, result);

            return writer.toString();
        } catch (TransformerException ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    public String getEntities(String input)
    {
    	AlchemyAPI_NamedEntityParams entityParams = new AlchemyAPI_NamedEntityParams();
		entityParams.setSentiment(true);
		try
		{
			Document d = alchemyRef.TextGetRankedNamedEntities(input, entityParams);
			return getStringFromDocument(d);
		}
		catch(Exception exp)
		{
			System.out.println(exp.getMessage());
		}
		return null;
    }
	
    public String getTargettedSentiment(String input)
    {
    	AlchemyAPI_KeywordParams keywordParams = new AlchemyAPI_KeywordParams();
    	keywordParams.setSentiment(true);
    	try
    	{
    		Document d = alchemyRef.TextGetRankedKeywords(input, keywordParams);
    		return getStringFromDocument(d);
    	}
    	catch(Exception exp)
    	{
    		
    	}
    	return null;
    }
    
    public void loadAPIKey(String filename) throws IOException, FileNotFoundException
    {
        if (null == filename || 0 == filename.length())
            throw new IllegalArgumentException("Empty API key file specified.");

        File file = new File(filename);
        FileInputStream fis = new FileInputStream(file);

        BufferedReader breader = new BufferedReader(new InputStreamReader(fis));

        _apiKey = breader.readLine().replaceAll("\\n", "").replaceAll("\\r", "");

        fis.close();
        breader.close();

        if (null == _apiKey || _apiKey.length() < 5)
            throw new IllegalArgumentException("Too short API key.");
    }
    
    private Document doRequest(HttpURLConnection handle, String outputMode)
            throws IOException, SAXException,
                   ParserConfigurationException, XPathExpressionException
        {
            DataInputStream istream = new DataInputStream(handle.getInputStream());
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(istream);

            istream.close();
            handle.disconnect();

            XPathFactory factory = XPathFactory.newInstance();

            if(AlchemyAPI_Params.OUTPUT_XML.equals(outputMode)) {
            	String statusStr = getNodeValue(factory, doc, "/results/status/text()");
            	if (null == statusStr || !statusStr.equals("OK")) {
            		String statusInfoStr = getNodeValue(factory, doc, "/results/statusInfo/text()");
            		if (null != statusInfoStr && statusInfoStr.length() > 0)
            			throw new IOException("Error making API call: " + statusInfoStr + '.');

            		throw new IOException("Error making API call: " + statusStr + '.');
            	}
            }
            else if(AlchemyAPI_Params.OUTPUT_RDF.equals(outputMode)) {
            	String statusStr = getNodeValue(factory, doc, "//RDF/Description/ResultStatus/text()");
            	if (null == statusStr || !statusStr.equals("OK")) {
            		String statusInfoStr = getNodeValue(factory, doc, "//RDF/Description/ResultStatus/text()");
            		if (null != statusInfoStr && statusInfoStr.length() > 0)
            			throw new IOException("Error making API call: " + statusInfoStr + '.');

            		throw new IOException("Error making API call: " + statusStr + '.');
            	}
            }

            return doc;
        }

        private String getNodeValue(XPathFactory factory, Document doc, String xpathStr)
            throws XPathExpressionException
        {
            XPath xpath = factory.newXPath();
            XPathExpression expr = xpath.compile(xpathStr);
            Object result = expr.evaluate(doc, XPathConstants.NODESET);
            NodeList results = (NodeList) result;

            if (results.getLength() > 0 && null != results.item(0))
                return results.item(0).getNodeValue();

            return null;
        }
        
    public String getEmotions(String input)
    {
    	String _requestUri = Constants.watsonEmotionLink;
    	try 
    	{
			URL url = new URL(_requestUri);
			HttpURLConnection handle = (HttpURLConnection) url.openConnection();
	        handle.setDoOutput(true);
	        
    		AlchemyAPI_Params params = new AlchemyAPI_Params();
    		StringBuilder data = new StringBuilder();
            data.append("apikey=").append(this._apiKey);
            params.setText(input);
            data.append(params.getParameterString());
            
            handle.addRequestProperty("Content-Length", Integer.toString(data.length()));
	        DataOutputStream ostream = new DataOutputStream(handle.getOutputStream());
	        ostream.write(data.toString().getBytes());
	        ostream.close();
	        Document d=null;
			try 
			{
				d = doRequest(handle, params.getOutputMode());
			} 
			catch (Exception exp)
			{
				System.out.println(exp.getMessage());
			}
			String s =getStringFromDocument(d);
			try 
			{
				JAXBContext jaxbContext = JAXBContext.newInstance(EmotionResults.class);
				Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
				StringReader reader = new StringReader(s);
				EmotionResults tweetEmo = (EmotionResults) unmarshaller.unmarshal(reader);
				System.out.println(tweetEmo);
			} 
			catch (JAXBException e) 
			{
				System.out.println(e.getMessage());
			}
	        return s;
		} 
    	catch (IOException ioe)
    	{
    		System.out.println(ioe.getMessage());
    	}
    	return null;
    }
}
