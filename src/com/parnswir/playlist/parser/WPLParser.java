package com.parnswir.playlist.parser;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Xml;

import com.parnswir.playlist.MediaFile;
import com.parnswir.playlist.Playlist;

public class WPLParser {
	
	private static final String namespace = null;
	
	private String fileName;
	private String directory;
	private Playlist playlist;
	
	public WPLParser(File file) {
		fileName = file.getAbsolutePath();
		directory = file.getParentFile().getAbsolutePath() + "/";
		playlist = new Playlist();
		playlist.setName(file.getName().replace(".wpl", ""));
	}
	
	public Playlist buildPlaylist() throws IOException {
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
	   
    public ArrayList<Object> parse(InputStream in) throws XmlPullParserException, IOException {
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();
            return readXML(parser);
        } finally {
            in.close();
        }
    }
    
    private ArrayList<Object> readXML(XmlPullParser parser) throws XmlPullParserException, IOException {
        ArrayList<Object> entries = new ArrayList<Object>();

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
            
        }  
        return entries;
    }

}
