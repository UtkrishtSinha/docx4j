/*
 *  Copyright 2009, Plutext Pty Ltd.
 *   
 *  This file is part of docx4j.

    docx4j is licensed under the Apache License, Version 2.0 (the "License"); 
    you may not use this file except in compliance with the License. 

    You may obtain a copy of the License at 

        http://www.apache.org/licenses/LICENSE-2.0 

    Unless required by applicable law or agreed to in writing, software 
    distributed under the License is distributed on an "AS IS" BASIS, 
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
    See the License for the specific language governing permissions and 
    limitations under the License.

 */

package org.docx4j.model.sdt;

import java.util.HashMap;
import java.util.Iterator;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;


/**
 * There is no standard for how more than one value should
 * be stored in w:/sdtPr/w:tag
 * 
 * This class stores them like a URL query string
 * 
 * If all apps were to do that, two different applications
 * which stored data to w:tag could possibly co-exist.
 * 
 * To that end, it is recommended you use a namespace
 * (ie somenamespace: ) as part of each of your keys.
 * 
 */
public class QueryString {
	
	private static Logger log = Logger.getLogger(QueryString.class);
	
	public static String create(HashMap<String, String> map){

		StringBuffer sb = new StringBuffer();

		Iterator iterator = map.keySet().iterator();
		int pos = 0;
		while( iterator.hasNext() ) {
			String key = (String)iterator.next();
			if (pos>0) {
				sb.append("&");
			}
			sb.append(key + "=" + (String)map.get(key) );
			pos++;
		}			
		return sb.toString();

    }		
	
	/**
     *
     * Parses a query string passed from the client to the
     * server and builds a <code>HashTable</code> object
     * with key-value pairs. 
     * 
     * The query string should be in the form of a string
     * packaged by the GET or POST method, that is, it
     * should have key-value pairs in the form <i>key=value</i>,
     * with each pair separated from the next by a & character.
     *
     * <p>A key can appear more than once in the query string
     * with different values. However, the key appears only once in 
     * the hashtable, with its value being
     * an array of strings containing the multiple values sent
     * by the query string.
     * 
     * <p>When the keys and values are moved into the hashtable,
     * any + characters are converted to spaces, and characters
     * sent in hexadecimal notation (like <i>%xx</i>) are
     * converted to ASCII characters.
     * 
     * This method adapted from Java sources
     *
     * @param s		a string containing the query to be parsed
     *
     * @return		a <code>HashTable</code> object built
     * 			from the parsed key-value pairs
     *
     * @exception IllegalArgumentException	if the query string 
     *						is invalid
     *
     */
	public static HashMap<String, String> parseQueryString(String s) {

		log.debug("Query string: " + s);
		
		String valArray[] = null;

		if (s == null) {
			throw new IllegalArgumentException();
		}
		HashMap<String, String> map = new HashMap<String, String>();
		StringBuffer sb = new StringBuffer();
		StringTokenizer st = new StringTokenizer(s, "&");
		while (st.hasMoreTokens()) {
			String pair = (String) st.nextToken();
			int pos = pair.indexOf('=');
			if (pos == -1) {
				// XXX
				// should give more detail about the illegal argument
				throw new IllegalArgumentException();
			}
			String key = parseName(pair.substring(0, pos), sb);
			String val = parseName(pair.substring(pos + 1, pair.length()),
					sb);
			map.put(key, val);
		}
		return map;
	}

	/*
	 * Parse a name in the query string.
	 */

	static private String parseName(String s, StringBuffer sb) {
		sb.setLength(0);
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			switch (c) {
			case '+':
				sb.append(' ');
				break;
			case '%':
				try {
					sb.append((char) Integer.parseInt(s.substring(i + 1,
							i + 3), 16));
					i += 2;
				} catch (NumberFormatException e) {
					// XXX
					// need to be more specific about illegal arg
					throw new IllegalArgumentException();
				} catch (StringIndexOutOfBoundsException e) {
					String rest = s.substring(i);
					sb.append(rest);
					if (rest.length() == 2)
						i++;
				}

				break;
			default:
				sb.append(c);
				break;
			}
		}
		return sb.toString();
	}
	
	
}
