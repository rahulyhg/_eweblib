package com.eweblib.service;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Table;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import com.eweblib.annotation.column.DateColumn;
import com.eweblib.annotation.column.DateTimeColumn;
import com.eweblib.annotation.column.IntegerColumn;
import com.eweblib.bean.BaseEntity;
import com.eweblib.bean.vo.DBClomun;
import com.eweblib.dao.IQueryDao;
import com.eweblib.util.EweblibUtil;

public class DBInitialService {

	private static final Logger logger = LogManager.getLogger(DBInitialService.class);

	public static final String ADMIN_USER_NAME = "admin";

	/**
	 * 初始化数据库
	 * 
	 * @param dao
	 * @throws SecurityException
	 * @throws ClassNotFoundException
	 */
	public static void initDB(IQueryDao dao, String packageName) throws SecurityException, ClassNotFoundException {
		logger.info("initDB called");
		ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);

		scanner.resetFilters(true);
		scanner.addIncludeFilter(new AnnotationTypeFilter(Table.class));

		String[] packs = packageName.split(",");

		for (String pack : packs) {

			System.out.println(pack);
			if (EweblibUtil.isValid(pack)) {
				for (BeanDefinition bd : scanner.findCandidateComponents(pack.trim())) {
					Class<?> classzz = Class.forName(bd.getBeanClassName());

					createTable(dao, classzz);
					updateTable(dao, classzz);
				}

			}
		}

	}

	private static void updateTable(IQueryDao dao, Class<?> classzz) {
		Field[] fields = classzz.getFields();
		Table t = classzz.getAnnotation(Table.class);
		String create = "DESC " + t.name() + ";";

		List<String> upSqlList = new ArrayList<String>();

		List<DBClomun> columns = dao.listBySql(create, DBClomun.class);

		for (DBClomun column : columns) {
			loadFiledSql(column);
		}
		for (Field field : fields) {
			if (!ignore(field)) {
				boolean insert = true;
				String newSql = loadFiledSql(field);

				for (DBClomun column : columns) {
					if (field.getName().equalsIgnoreCase(column.getField())) {
						insert = false;
						String defineSql = loadFiledSql(column);

						if (!defineSql.replaceAll(" ", "").equalsIgnoreCase(newSql.replaceAll(" ", ""))) {
							upSqlList.add("ALTER TABLE " + t.name() + " MODIFY COLUMN " + newSql + ";");
							break;
						}
					}
				}

				if (insert) {
					upSqlList.add("ALTER TABLE " + t.name() + " ADD COLUMN " + newSql + ";");
				}

			}
		}

		for (String sql : upSqlList) {

			dao.executeSql(sql);
		}

		// System.out.println(upSqlList);

	}

	private static void createTable(IQueryDao dao, Class<?> classzz) {
		Field[] fields = classzz.getFields();
		Table t = classzz.getAnnotation(Table.class);
		String create = "CREATE TABLE IF NOT EXISTS `" + t.name() + "` ( `id` varchar(36) NOT NULL,";
		for (Field field : fields) {
			if (!ignore(field)) {
				String sql = loadFiledSql(field);
				create = create + sql + ",";
			}
		}
		create = create + "`createdOn` datetime DEFAULT NULL,";
		create = create + "`updatedOn` datetime DEFAULT NULL,";
		create = create + "`creatorId` varchar(36) DEFAULT NULL,";
		create = create + "PRIMARY KEY (`id`),";
		create = create + "UNIQUE KEY `id` (`id`)";
		create = create + ") ENGINE=MyISAM DEFAULT CHARSET=utf8;";

		dao.executeSql(create);
		System.out.println(create);
	}

	public static String loadFiledSql(DBClomun column) {
		String sql = null;

		sql = "`" + column.getField() + "` " + column.getType() + (column.getNull().equalsIgnoreCase("YES") ? "" : " NOT NULL ") + " DEFAULT " + column.getDefault();

		// System.out.println(sql);
		return sql;
	}

	public static String loadFiledSql(Field field) {
		String sql = "";
		if (field.getGenericType().toString().contains(String.class.getName())) {
			Column c = field.getAnnotation(Column.class);

			int length = 255;
			if (c.length() != 0) {
				length = c.length();
			}

			String nullable = " NOT NULL";
			if (c.nullable()) {
				nullable = " DEFAULT NULL";
			}
			sql = "`" + field.getName() + "` varchar(" + length + ")" + nullable;
		} else if (field.getGenericType().toString().contains(Integer.class.getName())) {
			Column c = field.getAnnotation(Column.class);
			IntegerColumn ic = field.getAnnotation(IntegerColumn.class);
			int length = 11;
			int defaultValue = 0;

			if (ic != null) {

			}
			if (c.length() != 0 && c.length() != 255) {
				length = c.length();
			}

			if (ic != null) {
				defaultValue = ic.defaultValue();
			}

			String nullable = " NOT NULL";
			if (c.nullable()) {
				nullable = " DEFAULT " + defaultValue + "";
			} else {
				nullable = " NOT NULL DEFAULT " + defaultValue + "";
			}
			sql = "`" + field.getName() + "` int(" + length + ")" + nullable;
		} else if (field.getGenericType().toString().contains(Date.class.getName())) {
			Column c = field.getAnnotation(Column.class);
			DateColumn dt = field.getAnnotation(DateColumn.class);

			String type = " DATETIME";

			if (dt != null) {
				type = " DATE";
			}

			String nullable = " NOT NULL";
			if (c.nullable()) {
				nullable = " DEFAULT NULL";
			}
			sql = "`" + field.getName() + "` " + type + nullable;
		}
		return sql;
	}

	public static boolean ignore(Field field) {
		Column c = field.getAnnotation(Column.class);
		return c == null || field.getName().equalsIgnoreCase(BaseEntity.ID) || field.getName().equalsIgnoreCase("createdOn") || field.getName().equalsIgnoreCase("updatedOn")
		        || field.getName().equalsIgnoreCase("creatorId");

	}

}
