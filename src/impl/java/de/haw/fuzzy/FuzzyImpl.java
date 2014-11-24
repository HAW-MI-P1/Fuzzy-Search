package de.haw.fuzzy;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import de.haw.fuzzy.exception.ConnectionException;
import de.haw.fuzzy.exception.IllegalArgumentException;
import de.haw.fuzzy.exception.InternalErrorException;

public class FuzzyImpl implements Fuzzy {
	
	// Public static attributes
	
	public static final int ALL             = 0x0F;
	public static final int SYNSETS         = 0x01;
	public static final int SIMILARTERMS    = 0x02;
	public static final int SUBSTRINGTERMS  = 0x04;
	public static final int STARTSWITHTERMS = 0x08;

	
	// Private attributes
	
	private URL url;
	private HttpURLConnection connection;
	
	private String urlParameters;
	private String targetURL;
	
	private final String API_URL       = "https://www.openthesaurus.de/synonyme/search";
	private final String MODE_ALL      = "mode=all";
	private final String DATATYPE_JSON = "format=application/json";
	private final String DATATYPE_XML  = "format=text/xml";

	
	// Constructor
	
	public FuzzyImpl(){}

	
	// Public operations
	
	@Override
	public String[] getSynonym(String word, int opt) {
		if( word == null || word.equals("") ){ throw new IllegalArgumentException("Illegal word: "  + word); }
		
		try{
			this.targetURL = API_URL + "?q=" + this.makeUTF8(word);
		} 
		catch (Exception e)
		{throw new InternalErrorException(); }

		return this.getSynonymXML(word, opt);
//		return this.getSynonymJSON(word, opt);
		
	}

	
	// Private operations
	
	private String[] getSynonymXML(String word, int opt) {
		this.urlParameters = this.MODE_ALL + "&" + this.DATATYPE_XML;

		HashMap<String, String> results = new HashMap<String, String>();

		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db;
		Document doc = null;
		try {
			db = dbf.newDocumentBuilder();
			InputSource is = new InputSource(new StringReader(this.getSearch()));
			doc = db.parse(is);
		} catch (ParserConfigurationException | SAXException | IOException e) {
			e.printStackTrace();
		}

		if ((opt & FuzzyImpl.SYNSETS) == FuzzyImpl.SYNSETS) {
			try {
				this.getTermXML(doc.getElementsByTagName("synset"), results);
			} catch (JSONException e) {
			}
		}

		if ((opt & FuzzyImpl.SIMILARTERMS) == FuzzyImpl.SIMILARTERMS) {
			try {
				this.getTermXML(doc.getElementsByTagName("similarterms"),
						results);
			} catch (JSONException e) {
			}
		}

		if ((opt & FuzzyImpl.SUBSTRINGTERMS) == FuzzyImpl.SUBSTRINGTERMS) {
			try {
				this.getTermXML(doc.getElementsByTagName("substringterms"),
						results);
			} catch (JSONException e) {
			}
		}

		if ((opt & FuzzyImpl.STARTSWITHTERMS) == FuzzyImpl.STARTSWITHTERMS) {
			try {
				this.getTermXML(doc.getElementsByTagName("startswithterms"),
						results);
			} catch (JSONException e) {
			}
		}

		return results.keySet().toArray(new String[0]);
	}

	@SuppressWarnings("unused")
	private String[] getSynonymJSON(String word, int opt) {
		this.urlParameters = this.MODE_ALL + "&" + this.DATATYPE_JSON;

		HashMap<String, String> results = new HashMap<String, String>();

		JSONObject obj = new JSONObject(this.getSearch());

		if ((opt & FuzzyImpl.SYNSETS) == FuzzyImpl.SYNSETS) {
			try {
				this.getTermJSON(obj.getJSONArray("synsets"), results);
			} catch (JSONException e) {
			}
		}

		if ((opt & FuzzyImpl.SIMILARTERMS) == FuzzyImpl.SIMILARTERMS) {
			try {
				this.getTermJSON(obj.getJSONArray("similarterms"), results);
			} catch (JSONException e) {
			}
		}

		if ((opt & FuzzyImpl.SUBSTRINGTERMS) == FuzzyImpl.SUBSTRINGTERMS) {
			try {
				this.getTermJSON(obj.getJSONArray("substringterms"), results);
			} catch (JSONException e) {
			}
		}

		if ((opt & FuzzyImpl.STARTSWITHTERMS) == FuzzyImpl.STARTSWITHTERMS) {
			try {
				this.getTermJSON(obj.getJSONArray("startswithterms"), results);
			} catch (JSONException e) {
			}
		}

		return results.keySet().toArray(new String[0]);
	}

	private void getTermXML(NodeList nodes, HashMap<String, String> results) {
		for (int j = 0; j < nodes.getLength(); j++) {
			NodeList children = ((Element) nodes.item(j))
					.getElementsByTagName("term");

			for (int i = 0; i < children.getLength(); i++) {
				Element element = (Element) children.item(i);

				String tmp = element.getAttribute("term");

				tmp = cleanBraces(tmp);
				tmp = cleanSpaces(tmp);
				
				results.put(tmp, tmp);
			}
		}
	}

	private void getTermJSON(JSONArray arr, HashMap<String, String> results) {
		for (int i = 0; i < arr.length(); i++) {
			JSONObject obj = arr.getJSONObject(i);
			try {
				String tmp = obj.getString("term");

				tmp = cleanBraces(tmp);
				tmp = cleanSpaces(tmp);

				results.put(tmp, tmp);

			} catch (JSONException e) {
			} // term not exist
			try {
				JSONArray terms = obj.getJSONArray("terms");
				this.getTermJSON(terms, results);
			} catch (JSONException e) {
			} // term not exist
		}
	}

	private String getSearch() {

		try {
			// Create connection
			this.url = new URL(this.makeUTF8(this.targetURL));
			this.connection = (HttpURLConnection) url.openConnection();
			this.connection.setRequestMethod("POST");
			this.connection.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");

			this.connection.setRequestProperty("Content-Length",
					"" + Integer.toString(urlParameters.getBytes().length));
			this.connection.setRequestProperty("Content-Language", "en-US");

			this.connection.setUseCaches(false);
			this.connection.setDoInput(true);
			this.connection.setDoOutput(true);

			// Send request
			
			DataOutputStream wr = null;
			
			try{
				wr = new DataOutputStream(
						this.connection.getOutputStream());
			}
			catch(Exception e){ throw new ConnectionException(); }
			
			wr.writeBytes(this.urlParameters);
			wr.flush();
			wr.close();

			// Get Response
			InputStream is = this.connection.getInputStream();
			BufferedReader rd = new BufferedReader(new InputStreamReader(is,
					"UTF-8"));
			String line;
			StringBuffer response = new StringBuffer();
			while ((line = rd.readLine()) != null) {
				response.append(line);
				response.append('\r');
			}
			rd.close();
			return response.toString();

		} catch (Exception e) {
			e.printStackTrace();
			return null;

		} finally {
			if (this.connection != null) {
				this.connection.disconnect();
			}
		}
	}

	private String makeUTF8(final String toConvert) {
		return toConvert.replaceAll("Ä", "%c3%84").replaceAll("Ö", "%c3%96")
				.replaceAll("Ü", "%c3%9c").replaceAll("ä", "%c3%a4")
				.replaceAll("ö", "%c3%b6").replaceAll("ü", "%c3%bc");
	}

	private String cleanBraces(String words) {
		while (words.indexOf("(") > -1) {
			String replStr = words.substring(words.indexOf("("),
					words.indexOf(")") + 1);
			words = words.replace(replStr, "");
		}
		return words;
	}

	private String cleanSpaces(String words) {
		if (words.equals("")) {
			return "";
		}
		if (words.indexOf(" ") == 0) {
			words = words.replaceFirst(" ", "");
		}
		if (words.substring(words.length() - 1, words.length()).equals(" ")) {
			words = words.substring(0, words.length() - 1);
		}
		return words;
	}

}
