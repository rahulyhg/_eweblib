package com.eweblib.search.index;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
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
import com.eweblib.util.EweblibUtil;

public class LuceneIndexHelper {
	public static Logger log = LogManager.getLogger(LuceneIndexHelper.class);

	public static final String CONTENT = "content";
	public static final String TABLE_NAME = "tableName";

	public synchronized static <T extends BaseEntity> void addIndex(BaseEntity entity, Class<?> clz) throws IOException {

		addIndex(entity, clz, null);
	}

	public synchronized static <T extends BaseEntity> void addIndex(BaseEntity entity, Class<?> clz, Set<String> independenceKeys) {

		if (independenceKeys == null) {
			independenceKeys = new HashSet<String>();
		}

		if (!independenceKeys.contains(BaseEntity.ID)) {
			independenceKeys.add(BaseEntity.ID);
		}

		Map<String, Object> data = entity.toMap();

		IndexWriter writer = null;
		try {
			writer = getIndexWriter();
		} catch (IOException e1) {
			log.error(e1);

			closeWriter(writer);
		}

		if (writer != null) {
			Document doc = new Document();

//			String content = "";

			Set<String> ignoreKeys = new HashSet<String>();
			ignoreKeys.add(BaseEntity.CREATED_ON);
			ignoreKeys.add(BaseEntity.UPDATED_ON);
			ignoreKeys.add(BaseEntity.CREATOR_ID);

			if (data != null) {

				java.lang.reflect.Field[] fields = clz.getFields();
				for (java.lang.reflect.Field field : fields) {

					if (data.get(field.getName()) != null) {
						if (field.isAnnotationPresent(IntegerColumn.class) || field.isAnnotationPresent(DateColumn.class) || field.isAnnotationPresent(FloatColumn.class)
						        || field.isAnnotationPresent(DoubleColumn.class) || field.isAnnotationPresent(BooleanColumn.class) || ignoreKeys.contains(field.getName())) {

							// do nothing, ignore those fields
						} else {

							if (independenceKeys.contains(field.getName())) {

								doc.add(new TextField(field.getName(), data.get(field.getName()).toString(), Field.Store.YES));
							}
							//content = content + " " + data.get(field.getName()).toString();
						}

					}

				}

				if (entity.getTable() != null) {
					doc.add(new TextField(TABLE_NAME, entity.getTable(), Field.Store.YES));
				}
//				System.out.println(content);
				doc.add(new TextField(CONTENT, data.toString(), Field.Store.YES));

				
				if (data.get(BaseEntity.ID) != null) {
					try {
						writer.updateDocument(new Term(BaseEntity.ID, data.get(BaseEntity.ID).toString()), doc);
					} catch (IOException e) {

						closeWriter(writer);
					}
				}

			}
		}

		closeWriter(writer);

	}

	public static IndexWriter getIndexWriter() throws IOException {
		Analyzer analyzer = new IKAnalyzer(true);

		File file = new File(ConfigManager.getLuceneIndexDir());
		file.mkdirs();
		IndexWriterConfig config = new IndexWriterConfig(Version.LATEST, analyzer);

		config.setOpenMode(OpenMode.CREATE_OR_APPEND);
		FSDirectory directory = FSDirectory.open(file);

		if (IndexWriter.isLocked(directory)) {
			IndexWriter.unlock(directory);
		}
		IndexWriter writer = new IndexWriter(directory, config);

		return writer;
	}

	public static void deleteDocument(String id) {
		IndexWriter writer = null;
		try {
			writer = getIndexWriter();
			writer.deleteDocuments((new Term(BaseEntity.ID, id)));
			closeWriter(writer);

		} catch (IOException e) {
			if (writer != null) {
				closeWriter(writer);
			}
			e.printStackTrace();
		}
	}

	public static void deleteDocument(String[] ids) {

		for (String id : ids) {
			deleteDocument(id);
		}
	}

	public static void closeWriter(IndexWriter writer) {

		if (writer != null) {
			try {
				writer.commit();
				writer.close();
			} catch (IOException e) {
				try {
	                writer.close();
                } catch (IOException e1) {
	                // TODO Auto-generated catch block
	                e1.printStackTrace();
                }
			}
		}

	}

	public static List<DocumentResult> seacher(String queryString, String queryField) {
		List<DocumentResult> results = new ArrayList<DocumentResult>();
		IndexReader reader = null;
		try {
			File file = new File(ConfigManager.getLuceneIndexDir());
			reader = DirectoryReader.open(FSDirectory.open(file));
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

			reader.close();

		} catch (Exception e) {

			if (reader != null) {

				try {
					reader.close();
				} catch (IOException e1) {
					//do nothing
				}
			}
			log.error(e);
		}
		return results;
	}

	public static Set<String> seacherIds(String queryString, String queryField) {
		List<DocumentResult> results = seacher(queryString, queryField);
		Set<String> ids = new HashSet<String>();

		for (DocumentResult dr : results) {

			String id = dr.getDocument().get("id");
			if (EweblibUtil.isValid(id)) {
				ids.add(id);
			}
		}

		return ids;
	}

	public static Set<String> seacherIds(String queryString) {
		return seacherIds(queryString, CONTENT);
	}

	public static void printDocumentResult(DocumentResult dr) {
		Document doc = dr.getDocument();
		ScoreDoc sd = dr.getScoreDoc();
		System.out.println("doc=" + sd.doc + " score=" + sd.score + " id=" + doc.get("id"));
	}

	public static void printDocumentResult(List<DocumentResult> drresults) {

		for (DocumentResult dr : drresults) {

			printDocumentResult(dr);

		}

	}
}
