/**
 * IK 中文分词  版本 5.0.1
 * IK Analyzer release 5.0.1
 * 
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * 源代码由林良益(linliangyi2005@gmail.com)提供
 * 版权声明 2012，乌龙茶工作室
 * provided by Linliangyi and copyright 2012 by Oolong studio
 * 
 * 
 */
package org.wltea.analyzer.sample;

import java.io.IOException;
import java.io.StringReader;
import java.net.URLDecoder;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;
import org.wltea.analyzer.lucene.IKAnalyzer;

/**
 * 使用IKAnalyzer进行分词的演示
 * 2012-10-22
 *
 */
public class IKAnalzyerDemo {
	
	public static void main(String[] args){
		//构建IK分词器，使用smart分词模式
		Analyzer analyzer = new IKAnalyzer(true);
		
		//获取Lucene的TokenStream对象
	    TokenStream ts = null;
		try {
			String s = URLDecoder.decode("http://58.217.200.16/rest/2.0/pcs/file?method=locatedownload&path=%2FFuther%20Study%2F%E8%8B%B1%E8%AF%AD%2F%E8%80%83%E7%A0%94%E8%8B%B1%E8%AF%AD%E3%80%90E%E8%B5%84%E6%BA%90%E5%88%86%E4%BA%AB%E5%9F%BA%E5%9C%B0%E3%80%91%2F%E8%80%83%E7%A0%94%E8%AF%8D%E6%B1%87%20%E7%9D%A1%E5%89%8D%E5%BF%85%E5%90%AC%E7%9A%84%20%E9%9F%B3%E9%A2%91%2F%E8%80%83%E7%A0%94%E7%9D%A1%E5%89%8D%E5%BF%85%E5%90%AC%E7%9A%84%E5%8D%95%E8%AF%8D%E9%9F%B3%E9%A2%91%2FS.mp3&ver=2.0&dtype=0&app_id=250528&devuid=310812520637268&check_blue=1&devuid=310812520637268&clienttype=1&channel=android_4.1.2_Lenovo+A388t_bd-netdisk_1001551c&version=7.8.1&logid=MTQyOTA2OTY2Mzc4OCxmZTgwOjo1MjNjOmM0ZmY6ZmUwNzpmMDhkJXdsYW4wLDYzMTY2Ng&cuid=BEB28A175FAB8395BD39CCD49E639FDA%257C310812520637268&bdstoken=c1b805b3ed015cb5c14071a42bb2be2e");
			ts = analyzer.tokenStream("myfield", new StringReader(s));
//			ts = analyzer.tokenStream("myfield", new StringReader("这是一个中文分词的例子，你可以直接运行它！IKAnalyer can analysis english text too"));
			//获取词元位置属性
		    OffsetAttribute  offset = ts.addAttribute(OffsetAttribute.class); 
		    //获取词元文本属性
		    CharTermAttribute term = ts.addAttribute(CharTermAttribute.class);
		    //获取词元文本属性
		    TypeAttribute type = ts.addAttribute(TypeAttribute.class);
		    
		    
		    //重置TokenStream（重置StringReader）
			ts.reset(); 
			//迭代获取分词结果
			while (ts.incrementToken()) {
			  System.out.println(offset.startOffset() + " - " + offset.endOffset() + " : " + term.toString() + " | " + type.type());
			}
			//关闭TokenStream（关闭StringReader）
			ts.end();   // Perform end-of-stream operations, e.g. set the final offset.

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			//释放TokenStream的所有资源
			if(ts != null){
		      try {
				ts.close();
		      } catch (IOException e) {
				e.printStackTrace();
		      }
			}
	    }
		
	}

}
