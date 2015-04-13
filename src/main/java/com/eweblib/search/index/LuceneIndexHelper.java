package com.eweblib.search.index;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.wltea.analyzer.lucene.IKAnalyzer;

import com.eweblib.annotation.column.BooleanColumn;
import com.eweblib.annotation.column.DateColumn;
import com.eweblib.annotation.column.DoubleColumn;
import com.eweblib.annotation.column.FloatColumn;
import com.eweblib.annotation.column.IntegerColumn;
import com.eweblib.bean.BaseEntity;
import com.eweblib.bean.lucene.DocumentResult;
import com.eweblib.cfg.ConfigManager;

public class LuceneIndexHelper {

	public static final String TABLE_NAME = "tableName";

	public static <T extends BaseEntity> void addIndex(BaseEntity entity, Class<?> clz) throws IOException {

		addIndex(entity, clz, null);
	}

	public static <T extends BaseEntity> void addIndex(BaseEntity entity, Class<?> clz, Set<String> independenceKeys) {

		if (independenceKeys == null) {
			independenceKeys = new HashSet<String>();
		}

		if (independenceKeys.isEmpty()) {
			independenceKeys.add(BaseEntity.ID);
		}

		if (!independenceKeys.contains(BaseEntity.ID)) {
			independenceKeys.add(BaseEntity.ID);
		}

		Map<String, Object> params = entity.toMap();
		File file = new File(ConfigManager.getLuceneIndexDir());
		file.mkdirs();
		// doc.add(new TextField("summary",
		// "IKAnalyzer是一个开源的，基于java语言开发的轻量级的中文分词工具包。从2006年12月推出1.0版本开始，IKAnalyzer已经推出了3个大版本。",
		// Field.Store.YES));
		// Analyzer analyzer = new IKAnalyzer(true);
		Analyzer analyzer = new IKAnalyzer(true);
		IndexWriterConfig config = new IndexWriterConfig(Version.LATEST, analyzer);

		config.setOpenMode(OpenMode.CREATE_OR_APPEND);
		IndexWriter writer = null;
		try {
			writer = new IndexWriter(FSDirectory.open(file), config);
		} catch (IOException e1) {
			e1.printStackTrace();
			closeWriter(writer);
		}
		Document doc = new Document();

		String content = "";

		Set<String> ignoreKeys = new HashSet<String>();
		ignoreKeys.add(BaseEntity.CREATED_ON);
		ignoreKeys.add(BaseEntity.UPDATED_ON);
		ignoreKeys.add(BaseEntity.CREATOR_ID);

		if (params != null) {

			java.lang.reflect.Field[] fields = clz.getFields();
			for (java.lang.reflect.Field field : fields) {

				if (params.get(field.getName()) != null) {
					if (field.isAnnotationPresent(IntegerColumn.class) || field.isAnnotationPresent(DateColumn.class) || field.isAnnotationPresent(FloatColumn.class)
					        || field.isAnnotationPresent(DoubleColumn.class) || field.isAnnotationPresent(BooleanColumn.class) || ignoreKeys.contains(field.getName())) {

						// do nothing, ignore those fields
					} else {

						if (independenceKeys.contains(field.getName())) {
							
							System.out.println(field.getName());
							doc.add(new TextField(field.getName(), params.get(field.getName()).toString(), Field.Store.YES));
						}
						content = content + params.get(field.getName()).toString();
					}

				}

			}
			
			if (entity.getTable() != null) {
				doc.add(new TextField(TABLE_NAME, entity.getTable(), Field.Store.YES));
			}
			doc.add(new TextField("contents", content, Field.Store.YES));

			if (params.get(BaseEntity.ID) != null) {
				try {
					writer.updateDocument(new Term(BaseEntity.ID, params.get(BaseEntity.ID).toString()), doc);
				} catch (IOException e) {

					closeWriter(writer);
				}
			}

			closeWriter(writer);
		}

	}

	public static void closeWriter(IndexWriter writer) {

		if (writer != null) {
			try {
				writer.commit();
				writer.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}

	public static List<DocumentResult> seacher(String queryString, String queryField) {
		List<DocumentResult> results = new ArrayList<DocumentResult>();
		try {
			File file = new File(ConfigManager.getLuceneIndexDir());
			IndexReader reader = DirectoryReader.open(FSDirectory.open(file));
			IndexSearcher searcher = new IndexSearcher(reader);
			// :Post-Release-Update-Version.LUCENE_XY:

			Analyzer analyzer = new IKAnalyzer(true);

			QueryParser parser = new QueryParser(queryField, analyzer);

			Query query = parser.parse(queryString);
			System.out.println("Searching for: " + query.toString(queryField));

			Date start = new Date();
			searcher.search(query, null, 100);

			Date end = new Date();
			System.out.println("Time: " + (end.getTime() - start.getTime()) + "ms");

			TopDocs docResults = searcher.search(query, 10 * 1);
			ScoreDoc[] hits = docResults.scoreDocs;

			System.out.println("hits length: " + hits.length);

			for (ScoreDoc sd : hits) {
				DocumentResult dr = new DocumentResult();

				Document doc = searcher.doc(sd.doc);

				dr.setDocument(doc);
				dr.setScoreDoc(sd);
				results.add(dr);

			}

		} catch (Exception e) {
			System.out.print(e);
		}
		return results;
	}

	public static void printDocumentResult(DocumentResult dr, String keyField) {
		Document doc = dr.getDocument();
		ScoreDoc sd = dr.getScoreDoc();

		System.out.println("doc=" + sd.doc + " score=" + sd.score + " id=" + doc.get("id") + " " + keyField + "=" + doc.get(keyField));

	}

	public static void printDocumentResult(List<DocumentResult> drresults, String keyField) {

		for (DocumentResult dr : drresults) {

			printDocumentResult(dr, keyField);

		}

	}
}
