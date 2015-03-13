package com.eweblib.service;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.web.bind.annotation.RequestMapping;

import com.eweblib.annotation.controller.LoginRequired;
import com.eweblib.annotation.controller.Permission;
import com.eweblib.annotation.controller.RoleDescription;
import com.eweblib.bean.RolePath;
import com.eweblib.dao.IQueryDao;
import com.eweblib.dbhelper.DataBaseQueryBuilder;

public class InitialService {

	public static final Set<String> loginPath = new HashSet<String>();
	public static final Map<String, String> rolesPathValidationMap = new HashMap<String, String>();

	private static final Logger logger = LogManager.getLogger(InitialService.class);

	public static final String ADMIN_USER_NAME = "admin";

	/**
	 * 初始化数据库
	 * 
	 * @param dao
	 * @throws SecurityException
	 * @throws ClassNotFoundException
	 */
	public static void initSystem(IQueryDao dao, String packageName) throws SecurityException, ClassNotFoundException {

		initRoleItems(dao, packageName);
		setLoginPathValidation(packageName);

	}

	/**
	 * 初始化那些path需要登录验证，数据放到内存中
	 * 
	 * 
	 * @throws ClassNotFoundException
	 */
	private static void setLoginPathValidation(String packageName) throws ClassNotFoundException {
		ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
		scanner.resetFilters(true);
		scanner.addIncludeFilter(new AnnotationTypeFilter(LoginRequired.class));
		for (BeanDefinition bd : scanner.findCandidateComponents(packageName)) {
			Class<?> classzz = Class.forName(bd.getBeanClassName());
			Method metods[] = classzz.getMethods();

			RequestMapping parent = classzz.getAnnotation(RequestMapping.class);
			String path = "";
			if (parent != null) {
				path = parent.value()[0];
			}

			for (Method m : metods) {
				LoginRequired rv = m.getAnnotation(LoginRequired.class);
				RequestMapping mapping = m.getAnnotation(RequestMapping.class);

				if (rv == null || rv.required()) {
					if (mapping != null) {
						loginPath.add(path + mapping.value()[0]);
					}
				}
			}

		}
	}

	/**
	 * 
	 * 出事化权限表，权限来至于 @RoleValidate
	 * 
	 * @param dao
	 * @throws ClassNotFoundException
	 */
	private static void initRoleItems(IQueryDao dao, String packageName) throws ClassNotFoundException {

		Map<String, RolePath> pathSet = new HashMap<String, RolePath>();

		DataBaseQueryBuilder pathQuery = new DataBaseQueryBuilder(RolePath.TABLE_NAME);
		pathQuery.limitColumns(new String[] { RolePath.PATH, RolePath.ID });

		List<RolePath> pathList = dao.listByQuery(pathQuery, RolePath.class);
		Map<String, String> pathIdMap = new HashMap<String, String>();

		for (RolePath rp : pathList) {

			pathIdMap.put(rp.getPath(), rp.getId());
		}

		ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
		scanner.addIncludeFilter(new AnnotationTypeFilter(Permission.class));

		for (BeanDefinition bd : scanner.findCandidateComponents(packageName)) {
			Class<?> classzz = Class.forName(bd.getBeanClassName());
			Method metods[] = classzz.getMethods();

			String groupName = null;
			RequestMapping parent = classzz.getAnnotation(RequestMapping.class);
			String path = "";
			if (parent != null) {
				path = parent.value()[0];
			}

			Permission permission = classzz.getAnnotation(Permission.class);
			if (permission != null) {
				groupName = permission.groupName();
			}

			for (Method m : metods) {

				RequestMapping mapping = m.getAnnotation(RequestMapping.class);

				RoleDescription rd = m.getAnnotation(RoleDescription.class);

				if (mapping != null) {

					String validPath = path + mapping.value()[0];
					RolePath tmp = new RolePath();
					tmp.setGroupName(groupName);
					tmp.setPath(validPath);

					if (rd != null) {

						tmp.setDescription(rd.description());

						pathSet.put(validPath, tmp);
					} else {
						pathSet.put(validPath, tmp);
					}
				}

			}
		}

		for (String newPath : pathSet.keySet()) {

			if (pathIdMap.get(newPath) == null) {
				RolePath rp = new RolePath();
				rp.setPath(newPath);
				rp.setDescription(pathSet.get(newPath).getDescription());
				rp.setGroupName(pathSet.get(newPath).getGroupName());

				dao.insert(rp);
			}
		}

		for (String oldPath : pathIdMap.keySet()) {

			if (!pathSet.keySet().contains(oldPath)) {
				RolePath rp = new RolePath();
				rp.setId(pathIdMap.get(oldPath));
				dao.deleteById(rp);
			} else {
				RolePath rp = new RolePath();
				rp.setPath(oldPath);
				rp.setId(pathIdMap.get(oldPath));
				rp.setDescription(pathSet.get(oldPath).getDescription());
				rp.setGroupName(pathSet.get(oldPath).getGroupName());
				dao.updateById(rp);
			}
		}

	}
}
