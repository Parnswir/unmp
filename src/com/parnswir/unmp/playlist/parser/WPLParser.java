package com.parnswir.unmp.playlist.parser;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.database.sqlite.SQLiteDatabase;
import android.util.Xml;

import com.parnswir.unmp.core.C;
import com.parnswir.unmp.core.DatabaseUtils;
import com.parnswir.unmp.playlist.Filter;
import com.parnswir.unmp.playlist.MediaFile;
import com.parnswir.unmp.playlist.Playlist;

public class WPLParser {
	
	public static final String MUSIC_IN_MY_LIBRARY = "{4202947A-A563-4B05-A754-A1B4B5989849}";
	
	
	private static final String namespace = null;
	
	private static final Set<String> NOT_SUPPORTED_KEYWORDS = new HashSet<String>(Arrays.asList(
	     new String[] {"Ascending", "Descending", "Random"}
	));
	
	private static final Map<String, String> TYPE_MAPPING = new HashMap<String, String>();
	static{
		TYPE_MAPPING.put("User Rating", C.TAB_TITLES + "." + C.COL_RATING);
		TYPE_MAPPING.put("Genre", C.TAB_GENRES + "." + C.COL_GENRE);
		TYPE_MAPPING.put("Album Title", C.TAB_ALBUMS + "." + C.COL_ALBUM);
	}
	
	private static final Map<String, String> CONDITION_MAPPING = new HashMap<String, String>();
	static{
		CONDITION_MAPPING.put("Is", " IS \"%s\"");
		CONDITION_MAPPING.put("Is Not", " IS NOT \"%s\"");
		
		CONDITION_MAPPING.put("Contains", " LIKE \"%%%s%%\"");
		CONDITION_MAPPING.put("Does Not Contain", " NOT LIKE \"%%%s%%\"");

		CONDITION_MAPPING.put("Equals", " = %s");
		CONDITION_MAPPING.put("Does Not Equal", " != \"%s\"");
		
		CONDITION_MAPPING.put("Is At Least", " >= %s");
		CONDITION_MAPPING.put("Is No More Than", " <= %s");
		
		CONDITION_MAPPING.put("Is Less Than", " < %s");
		CONDITION_MAPPING.put("Is Greater Than", " > %s");
		
		CONDITION_MAPPING.put("Is Before", " < %s");
		CONDITION_MAPPING.put("Is Later Than", " > %s");
		
		CONDITION_MAPPING.put("Is More Recent Than", " >= %s");
		
		CONDITION_MAPPING.put("Above", " > %s");
		CONDITION_MAPPING.put("Below", " < %s");
	}
	
	private static final Map<String, String> UNIT_MAPPING = new HashMap<String, String>();
	static{
		UNIT_MAPPING.put("5 stars", "10");
		UNIT_MAPPING.put("4 stars", "8");
		UNIT_MAPPING.put("3 stars", "6");
		UNIT_MAPPING.put("2 stars", "4");
		UNIT_MAPPING.put("1 stars", "2");
		UNIT_MAPPING.put("0 stars", "0");
	}
	
	private String fileName;
	private String directory;
	private Playlist playlist;
	private SQLiteDatabase database;
	
	public WPLParser(File file, SQLiteDatabase db) {
		database = db;
		fileName = file.getAbsolutePath();
		directory = file.getParentFile().getAbsolutePath() + "/";
		playlist = new Playlist();
		playlist.setName(file.getName().replace(".wpl", ""));
	}
	
	public Playlist buildPlaylist() throws IOException, WPLParserException {
		InputStream in = null;
		try {
			in = new BufferedInputStream(new FileInputStream(fileName));
			parse(in);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} finally {
			if (in != null)
				in.close();
		}
		return playlist;
	}
	   
    public void parse(InputStream in) throws XmlPullParserException, IOException, WPLParserException {
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();
            readXML(parser);
        } finally {
            in.close();
        }
    }
    
    private void readXML(XmlPullParser parser) throws XmlPullParserException, IOException, WPLParserException {
        String condition = "(1=0)";

        parser.require(XmlPullParser.START_TAG, namespace, "smil");
        while (parser.next() != XmlPullParser.END_DOCUMENT) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            
            String name = parser.getName();
            
            if (name.equals("title")) {
            	if (parser.next() == XmlPullParser.TEXT)
            		playlist.setName(parser.getText());
            }
            
            if (name.equals("media")) {
            	String path = parser.getAttributeValue(namespace, "src");
            	path = path.replace("\\", "/");
            	playlist.children.add(new MediaFile(directory + path));
            }
            
            if (name.equals("sourceFilter")) {
            	String guid = parser.getAttributeValue(namespace, "id");
            	if (guid.equals(MUSIC_IN_MY_LIBRARY)) {
            		
            		condition += " OR ((1=1)";
            		
            		while (parser.next() != XmlPullParser.END_DOCUMENT) {
            			
            			if (parser.getEventType() == XmlPullParser.END_TAG) {
            				String innerName = parser.getName();
            				if (innerName.equals("sourceFilter")) {
            					break;
            				} else {
            					continue;
            				}
                        }
            			
            			if (parser.getEventType() == XmlPullParser.START_TAG) {
            				String innerName = parser.getName();
            				
            				if (innerName.equals("fragment")) {
            					String fragmentName = parser.getAttributeValue(namespace, "name");
            					condition += " AND " + TYPE_MAPPING.get(fragmentName);
            					
            					int argumentCount = 0;
            					String argument = "";
            					String value = "";
            					
            					while (parser.next() != XmlPullParser.END_DOCUMENT) {
                        			
                        			if (parser.getEventType() == XmlPullParser.END_TAG) {
                        				String nodeName = parser.getName();
                        				if (nodeName.equals("fragment")) {
                        					condition += String.format(argument, value);
                        					break;
                        				} else {
                        					continue;
                        				}
                        			}
                        			
                        			if (parser.getEventType() == XmlPullParser.START_TAG) {
                        				String nodeName = parser.getName();
                        				
                        				if (nodeName.equals("argument")) {
                        					parser.next();
                        					if (argumentCount == 0) {
                        						argument = parser.getText();
                        						if (CONDITION_MAPPING.containsKey(argument)) {
                        							argument = CONDITION_MAPPING.get(argument);
                        						} else {
                        							throw new WPLParserException("Used unsupported condition " + argument);
                        						}
                        						
                        						argumentCount++;
                        					} else {
                        						value = parser.getText();
                        						if (UNIT_MAPPING.containsKey(value)) {
                        							value = UNIT_MAPPING.get(value);
                        						}
                        						
                        						if (NOT_SUPPORTED_KEYWORDS.contains(value)) {
                        							throw new WPLParserException("Used unsupported keyword " + value);
                        						}
                        					}
                        				}
                        				
                        			}
            					}
            					
            				}
            				
                        }
            			
            		}
            		
            		condition += ")";
            		
            	}
            }
            
        } 
        
        if (condition.length() > 0) {
	        String query = String.format("SELECT %s.%s FROM %s WHERE %s", C.TAB_TITLES, C.COL_FILE, getJoinNecessaryFor(condition), condition);
	        playlist.children.add(new Filter(database, query));
        }
    }
    
    
    private String getJoinNecessaryFor(String condition) {
    	ArrayList<String> tables = new ArrayList<String>();
    	for (String table : C.TABLENAMES)
    		if (condition.indexOf(table) > -1)
    			tables.add(table);
    	String[] array = new String[tables.size()];
    	array = tables.toArray(array);
    	return DatabaseUtils.getJoinForTables(array);
    }
    
    
    public static final class WPLParserException extends Exception {
    	
		private static final long serialVersionUID = 1345253070867225300L;

		public WPLParserException(String message) {
			super(message);
		}
    	
    }

}
