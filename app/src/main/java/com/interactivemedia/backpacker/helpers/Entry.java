package com.interactivemedia.backpacker.helpers;

/**
 * Created by Rebecca Durm on 05.01.2018.
 */

public class Entry {
        public final String title;
        public final String link;
        public final String summary;


        private Entry (String title, String summary, String link){
            this.title=title;
            this.summary=summary;
            this.link=link;
        }
    }
