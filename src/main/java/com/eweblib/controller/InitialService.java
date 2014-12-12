package com.eweblib.controller;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.web.bind.annotation.RequestMapping;

import com.eweblib.annotation.column.LoginRequired;
import com.eweblib.annotation.column.Permission;
import com.eweblib.bean.BaseEntity;
import com.eweblib.dao.IMyBatisDao;
import com.eweblib.exception.LoginException;
import com.eweblib.exception.ResponseException;
import com.eweblib.util.EWeblibThreadLocal;

public class InitialService {

	public static final Set<String> loginPath = new HashSet<String>();
	public static final Map<String, String> rolesValidationMap = new HashMap<String, String>();
	private static final Logger logger = LogManager.getLogger(InitialService.class);
    
	public static final String ADMIN_USER_NAME = "admin";

	/**
	 * 初始化数据库
	 * 
	 * @param dao
	 * @throws SecurityException
	 * @throws ClassNotFoundException
	 */
	public static void initSystem(IMyBatisDao dao, String packageName) throws SecurityException, ClassNotFoundException {

		initRoleItems(dao, packageName);
		setLoginPathValidation(packageName);

	}

	public static void roleCheck(HttpServletRequest request) {
		loginCheck(request);

		if (InitialService.rolesValidationMap.get(request.getServletPath()) != null) {
			boolean find = false;

			if (EWeblibThreadLocal.getCurrentUserId() != null) {

//				DataBaseQueryBuilder builder = new DataBaseQueryBuilder(User.TABLE_NAME);
//				builder.and(User.ID, EWeblibThreadLocal.getCurrentUserId());
//
//				User user = (User) queryDao.findOneByQuery(builder, User.class);
//
//				if (user != null) {
//
//				}

			}

			if (!find) {
				throw new ResponseException("无权限操作");
			}

		}

	}
	


	public static void loginCheck(HttpServletRequest request) {

		if (InitialService.loginPath.contains(request.getServletPath())) {
			if (request.getSession().getAttribute(BaseEntity.ID) == null) {
				logger.debug("Login requried for path : " + request.getPathInfo());
				throw new LoginException();
			}
		}
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
	private static void initRoleItems(IMyBatisDao dao, String packageName) throws ClassNotFoundException {

		ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
		scanner.addIncludeFilter(new AnnotationTypeFilter(Permission.class));
		List<String> roleIds = new ArrayList<String>();

		for (BeanDefinition bd : scanner.findCandidateComponents(packageName)) {
			Class<?> classzz = Class.forName(bd.getBeanClassName());
			Method metods[] = classzz.getMethods();

			RequestMapping parent = classzz.getAnnotation(RequestMapping.class);
			String path = "";
			if (parent != null) {
				path = parent.value()[0];
			}

			for (Method m : metods) {
				Annotation annotations[] = m.getAnnotations();

				for (Annotation anno : annotations) {

					if (anno instanceof Permission) {
						Permission rv = (Permission) anno;

						RequestMapping mapping = m.getAnnotation(RequestMapping.class);

						if (!roleIds.contains(rv.permissionID())) {
							roleIds.add(rv.permissionID());
						}
						

						String validPath = path + mapping.value()[0];
						rolesValidationMap.put(validPath, rv.permissionID());
					}
				}
			}
		}

	}

}
