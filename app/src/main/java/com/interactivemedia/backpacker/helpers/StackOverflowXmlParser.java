package com.interactivemedia.backpacker.helpers;

import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.XMLFormatter;

/**
 * Source: https://developer.android.com/training/basics/network-ops/xml.html#choose
 * Created by Rebecca Durm on 05.01.2018.
 */

public class StackOverflowXmlParser {
    // We don't use namespaces
    private static final String ns = null;


    public static class Entry {
        public final String title;
        public final String link;
        public final String summary;


        private Entry (String title, String summary, String link){
            this.title=title;
            this.summary=summary;
            this.link=link;
        }
    }




    public List parse(InputStream in) throws XmlPullParserException, IOException {
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();
            return readFeed(parser);
        } finally {
            in.close();
        }
    }

    private List readFeed(XmlPullParser parser) throws IOException, XmlPullParserException {
        List entries = new ArrayList<Entry>();
        parser.require(XmlPullParser.START_TAG, ns, "feed");
        while (parser.next() != XmlPullParser.END_TAG){
            if (parser.getEventType() !=XmlPullParser.START_TAG){
                continue;
            }
            String name = parser.getName();
            
            //Starts by looking for the entry tag
            if (name.equals("entry")){
                entries.add(readEntry(parser));
            }
            else {
                skip(parser);
            }
        }
        return entries; 
    }




    //Parses the contents of an entry. If it encounters a title, summary or link tag,
    //hands them of to their respective "read" method for processing. Otherwise, skips the tag.
    private Entry readEntry(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "entry");
        String title = null;
        String summary = null;
        String link = null;

        while (parser.next() != XmlPullParser.END_TAG){
            if (parser.getEventType()!= XmlPullParser.START_TAG){
                continue;
            }
            String name = parser.getName();
            if (name.equals("title")){
                title= readTitle(parser);
            }
            else if(name.equals("summary")){
                summary=readSummary(parser);
            }
            else if(name.equals("link")){
                summary=readLink(parser);
            }
            else {
                skip(parser);
            }
        }
        return new Entry (title, summary, link);
    }

    //processes title tags in the feed
    private String readTitle(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "title");
        String title = readText(parser);
        parser.require (XmlPullParser.END_TAG, ns, "title");
        return title;
    }

    //processes link tags in the feed
    private String readLink(XmlPullParser parser) throws IOException, XmlPullParserException {
        String link="";
        parser.require(XmlPullParser.START_TAG, ns, "link");
        String tag = parser.getName();
        String relType = parser.getAttributeValue(null, "rel");
        if (tag.equals("link")){
            if (relType.equals("alternate")){
                link=parser.getAttributeValue(null, "href");
                parser.nextTag();
            }
        }
        parser.require(XmlPullParser.END_TAG, ns, "link");
        return link;
    }



    //processes summary tags in the feed
    private String readSummary(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "summary");
        String summary = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "summary");
        return summary;
    }




    //For the tags title and summary, extracts their text values
    private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String textresult="";
        if (parser.next()==XmlPullParser.TEXT){
            textresult=parser.getText();
            parser.nextTag();
        }
        return textresult;
    }


    private void skip (XmlPullParser parser) throws IOException, XmlPullParserException {
        if (parser.getEventType()!=XmlPullParser.START_TAG){
            throw new IllegalStateException();
        }
        //to make sure that it will stop at the correct End_Tag variable depth is used
        int depth = 1;
        while (depth !=0){
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }
}

