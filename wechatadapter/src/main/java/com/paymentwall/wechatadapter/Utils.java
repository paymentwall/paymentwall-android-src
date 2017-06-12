package com.paymentwall.wechatadapter;

import android.util.Log;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

/**
 * Created by nguyen.anh on 10/28/2016.
 */

public class Utils {

    public static String buildMapToXMLBody(Map<String, Object> map){
        if (map!=null){
            try {
                DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
                //root elements
                Document doc = docBuilder.newDocument();
                Element rootElement = doc.createElement("xml");
                doc.appendChild(rootElement);
                Iterator<String> iterator = map.keySet().iterator();
                while (iterator.hasNext()) {
                    String key = (String) iterator.next();
                    if (key != null && !key.equalsIgnoreCase("")) {
                        Element ele = doc.createElement(key);
                        ele.setTextContent((String)map.get(key));
                        rootElement.appendChild(ele);
                    }
                }
                DOMSource domSource = new DOMSource(doc);
                StringWriter writer = new StringWriter();
                StreamResult result = new StreamResult(writer);
                TransformerFactory tf = TransformerFactory.newInstance();
                Transformer transformer = tf.newTransformer();
                transformer.transform(domSource, result);
                return writer.toString();
            }catch (Exception e){
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }

    public static Map<String, Object> parseXML(String respondString) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        InputStream inputStream = getStringStream(respondString);
        org.w3c.dom.Document document = builder.parse(inputStream);

        NodeList allnodes = document.getFirstChild().getChildNodes();
        Node node;
        Map<String, Object> map = new HashMap<String, Object>();

        for (int i=0; i< allnodes.getLength(); i++){
            node = allnodes.item(i);
            if (node instanceof org.w3c.dom.Element){
                map.put(node.getNodeName(), node.getTextContent());
            }
        }

        Log.v("prepaid ID: " , map.get("prepay_id").toString());
        return map;
    }

    private static InputStream getStringStream(String inputString){
        ByteArrayInputStream tInputStringStream = null;
        if (inputString != null && !inputString.trim().equals("")) {
            tInputStringStream = new ByteArrayInputStream(inputString.getBytes());
        }
        return tInputStringStream;
    }
}
