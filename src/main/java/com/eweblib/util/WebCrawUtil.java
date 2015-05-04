package com.eweblib.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import javax.xml.transform.TransformerException;

import org.apache.http.HttpResponse;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.xpath.XPathAPI;
import org.cyberneko.html.parsers.DOMParser;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class WebCrawUtil {
	public static Logger logger = LogManager.getLogger(UrlUtil.class);

	public static String extractNodeValue(String url, String xpath, String encoding){
		
		NodeList nlist = extractNodeList(url, "//TITLE/text()", encoding);

//		System.out.println(nlist.getLength());
		if (nlist != null && nlist.getLength() > 0) {

			return nlist.item(0).getNodeValue();

		}
		
		return null;
	}


	public static NodeList extractNodeList(String url, String xpath, String encoding) {
		logger.info("Craw" + url);
		DOMParser parser = new DOMParser();
		try {

			if (encoding == null) {
				encoding = HttpClientUtil.getResponseContentType(url);
			}
			

			// 设置网页的默认编码
			parser.setProperty("http://cyberneko.org/html/properties/default-encoding", encoding);

			parser.setFeature("http://xml.org/sax/features/namespaces", false);


//			
//			BufferedReader in = new BufferedReader(new InputStreamReader(new URL(baiduUrl).openStream()));
//			parser.parse(new InputSource(in));
//			in.close();
//			
			HttpResponse response = HttpClientUtil.doGetResponse(url, null, encoding, false);

			if (response != null && response.getFirstHeader("Location") != null) {
				url = response.getFirstHeader("Location").getValue();

//				encoding = HttpClientUtil.getResponseContentType(url);

				// 设置网页的默认编码
				parser.setProperty("http://cyberneko.org/html/properties/default-encoding", encoding);
				parser.setFeature("http://xml.org/sax/features/namespaces", false);

				response = HttpClientUtil.doGetResponse(url, null, null, false);

			}
			
			if (response != null && response.getEntity() != null) {
				InputStreamReader in = new InputStreamReader(response.getEntity().getContent(), encoding);
				BufferedReader reader = new BufferedReader(in);
				parser.parse(new InputSource(reader));
				Document doc = parser.getDocument();

				NodeList list = XPathAPI.selectNodeList(doc, xpath);
				reader.close();
				in.close();
				response.getEntity().getContent().close();
				return list;
			}

		} catch (TransformerException e) {
			logger.error("Parser " + url + " error: " + e.getMessage());
		} catch (Exception e) {			
			logger.error("Parser " + url + " error: " + e.getMessage());
		}
		return null;
	}
}
