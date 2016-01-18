package com.cvberry.berrypim;

import javax.swing.text.MutableAttributeSet;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.parser.DTD;
import javax.swing.text.html.parser.DocumentParser;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by vancan1ty on 1/18/2016.
 */
public class SGMLParseDemo {

    HTMLEditorKit.ParserCallback callee = new HTMLEditorKit.ParserCallback() {
        @Override
        public void handleStartTag(HTML.Tag t, MutableAttributeSet a, int pos) {
            super.handleStartTag(t, a, pos);
        }
    };
    public static void main(String[] args) throws IOException {
        DocumentParser mParser = new DocumentParser(DTD.getDTD("html32"));
        FileReader fReader = new FileReader(args[0]);
        mParser.parse(fReader);
    }
}
