package org.richfaces.resource;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.xml.parsers.ParserConfigurationException;

import org.junit.Test;
import org.xml.sax.SAXException;

import static junit.framework.Assert.assertEquals;

public class Xcss2EcssConverterTest {

    @Test
    public void testImport() {
        String xcss = "<f:importResource src=\"META-INF/page.xcss\" />";
        assertEcssEquals("@import url(\"#{resource['META-INF/page.xcss']}\");", convertFragment(xcss));
    }

    @Test
    public void testStyle() {
        String xcss = "\t<u:selector name=\"html > body input[type=checkbox]\">\n"
                + "\t\t<u:style name=\"border\" value=\"none\" />\n"
                + "\t</u:selector>";
        assertEcssEquals("html > body input[type=checkbox]{border:none;}", convertFragment(xcss));
    }

    @Test
    public void testStyleSkin() {
        String xcss = "\t<u:selector name=\"html\">\n"
                + "\t\t<u:style name=\"border\" skin=\"myProperty\" />\n"
                + "\t</u:selector>";
        assertEcssEquals("html{border:'#{richSkin.myProperty}';}", convertFragment(xcss));
    }

    @Test
    public void testStyleSkinDefault() {
        String xcss = "\t<u:selector name=\"html\">\n"
                + "\t\t<u:style name=\"border\" skin=\"myProperty\" default=\"0\"/>\n"
                + "\t</u:selector>";
        assertEcssEquals("html{border:\"#{notemptyrichSkin.myProperty?richSkin.myProperty:'0'}\";}", convertFragment(xcss));
    }

    @Test
    public void testResource() {
        String xcss = "<u:selector name=\"li\">"
                + "<u:style name=\"background-image\">\n"
                + "<f:resource f:key=\"META-INF/puce.png\" />\n"
                + "</u:style>"
                + "</u:selector>";
        assertEcssEquals("li{background-image:\"url(#{resource['META-INF/puce.png']})\";}",
                convertFragment(xcss));
    }

    @Test
    public void testAttribute() {
        String xcss = "<u:selector name=\".myDiv\">\n" +
                "<u:style name=\"background-image\">\n" +
                "<f:resource f:key=\"test\">\n" +
                "<f:attribute name=\"gradientHeight\" value=\"100px\"/>\n" +
                "<f:attribute name=\"height\" value=\"150px\"/>\n" +
                "<f:attribute name=\"gradientColor\" skin=\"myskin\"/>\n" +
                "</f:resource>\n" +
                "</u:style>\n" +
                "</u:selector>";
        assertEcssEquals(".myDiv{"
                + "background-image:\"url(#{resource['test?gradientHeight=100px&height=150px&gradientColor=Skin.myskin']})\";"
                + "}",
                convertFragment(xcss));
    }

    @Test
    public void testIfEmpty() {
        String xcss = "<u:selector name=\".menu\">\n" +
                "<f:if skin=\"mySkin\">\n" +
                "</f:if>\n" +
                "</u:selector>";
        assertEcssEquals(".menu{}", convertFragment(xcss));
    }

    @Test
    public void testIfWithStyle() {
        String xcss = "<u:selector name=\".menu\">"
                + "<f:if skin=\"menu1\">"
                + "<u:style name=\"left\" value=\"10px\"/>"
                + "</f:if>"
                + "</u:selector>";
        assertEcssEquals(".menu{left:\"#{notemptyrichSkin.menu1?'10px':''}\";}",
                convertFragment(xcss));
    }

    @Test
    public void testIfWithStyleSkin() {
        String xcss = "<u:selector name=\".menu\">"
                + "<f:if skin=\"menu1\">"
                + "<u:style name=\"background-position\" skin=\"menu2\"/>"
                + "</f:if>"
                + "</u:selector>";
        assertEcssEquals(".menu{background-position:\"#{notemptyrichSkin.menu1?richSkin.menu2:''}\";}",
                convertFragment(xcss));
    }

    @Test
    public void testIfWithStyleSkinDefault() {
        String xcss = "<u:selector name=\".menu\">"
                + "<f:if skin=\"menu1\">"
                + "<u:style name=\"background-position\" skin=\"menu1\" default=\"0 0\"/>"
                + "</f:if>"
                + "</u:selector>";
        assertEcssEquals(".menu{background-position:\"#{(notemptyrichSkin.menu1)and(notemptyrichSkin.menu1)?richSkin.menu1:'00'}\";}",
                convertFragment(xcss));
    }

    @Test
    public void testIfWithResourceSkin() {
        String xcss = "<u:selector name=\".menu\">"
                + "<f:if skin=\"menu1\">"
                + "<u:style name=\"background-image\">"
                + "<f:resource f:skin=\"menu1\"/>"
                + "</u:style>"
                + "</f:if>"
                + "</u:selector>";
        assertEcssEquals(".menu{background-image:\"#{notemptyrichSkin.menu1?'url(':''}" +
                "#{notemptyrichSkin.menu1?resource[richSkin.menu1]:''}#{notemptyrichSkin.menu1?')':''}\";}",
                convertFragment(xcss));
    }

    @Test
    public void testIfWithResourceKey() {
        String xcss = "<u:selector name=\".menu\">"
                + "<f:if skin=\"menu1\">"
                + "<u:style name=\"background-image\">"
                + "<f:resource f:key=\"META-INF/lien.gif\" />"
                + "</u:style>"
                + "</f:if>"
                + "</u:selector>";
        assertEcssEquals(".menu{background-image:\"#{notemptyrichSkin.menu1?'url(':''}" +
                "#{notemptyrichSkin.menu1?resource['META-INF/lien.gif']:''}#{notemptyrichSkin.menu1?')':''}\";}",
                convertFragment(xcss));
    }

    @Test
    public void testVerbatim() {
        String xcss = "<f:verbatim>"
                + ".footer {"
                + "clear:none;"
                + "}"
                + "#header {"
                + "margin-bottom:10px;"
                + "}"
                + "</f:verbatim>";
        assertEcssEquals(".footer{clear:none;}#header{margin-bottom:10px;}",
                convertFragment(xcss));
    }

    @Test
    public void testVerbatimSkin() {
        String xcss = "<u:selector name=\"th\">"
                + "<u:style name=\"border-bottom\" value=\"solid\">"
                + "<f:verbatim skin=\"prop1\" />"
                + "<f:verbatim skin=\"prop2\" />"
                + "</u:style>"
                + "</u:selector>";
        assertEcssEquals("th{border-bottom:solid'#{richSkin.prop1}''#{richSkin.prop2}';}",
                convertFragment(xcss));
    }

    private void assertEcssEquals(String expected, String result) {
        expected = expected.replaceAll("[\\s]", "");
        result = result.replaceAll("[\\s]", "");
        assertEquals(expected, result);
    }

    private String convertFragment(String xcssFragment) {
        xcssFragment = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                + "<f:template xmlns:f='http:/jsf.exadel.com/template'\n"
                + "\t   xmlns:u='http:/jsf.exadel.com/template/util' \n"
                + "\t   xmlns=\"http://www.w3.org/1999/xhtml\" >"
                + xcssFragment
                + "</f:template>";
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Xcss2EcssConverter.Handler handler = new Xcss2EcssConverter.Handler(baos);
        Xcss2EcssConverter.CreateParser parser;
        try {
            parser = new Xcss2EcssConverter.CreateParser(handler);
        } catch (SAXException e) {
            throw new RuntimeException(e);
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        }
        try {
            parser.parse(new ByteArrayInputStream(xcssFragment.getBytes("utf-8")));
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (SAXException e) {
            throw new RuntimeException(e);
        }
        try {
            return new String(baos.toByteArray(), "utf-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
}