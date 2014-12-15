package com.eweblib.controller.interceptor;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileUploadBase.SizeLimitExceededException;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.util.NestedServletException;

import com.eweblib.bean.BaseEntity;
import com.eweblib.controller.AbstractController;
import com.eweblib.dao.IQueryDao;
import com.eweblib.exception.LoginException;
import com.eweblib.exception.ResponseException;
import com.eweblib.service.InitialService;
import com.eweblib.util.EWeblibThreadLocal;

public class ControllerFilter extends AbstractController implements Filter {

	public static final String URL_PATH = "urlPath";
	@Autowired
	private IQueryDao queryDao;

	@Override
	public void destroy() {

	}

	private static Logger logger = LogManager.getLogger(ControllerFilter.class);

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {

		HttpServletRequest srequest = (HttpServletRequest) request;
		HttpServletResponse sresponse = (HttpServletResponse) response;
		EWeblibThreadLocal.set(URL_PATH, srequest.getServletPath());
		if (srequest.getSession().getAttribute(BaseEntity.ID) != null) {
			EWeblibThreadLocal.set(BaseEntity.ID, srequest.getSession().getAttribute(BaseEntity.ID));
		}

		try {

			roleCheck((HttpServletRequest) request);
			filterChain.doFilter(request, response);
		} catch (Exception e) {

			if (e instanceof NestedServletException) {
				Throwable t = e.getCause();

				if (t instanceof ResponseException) {
					responseServerError(t, (HttpServletRequest) request, (HttpServletResponse) response);
				} else if (e.getCause() instanceof SizeLimitExceededException || e.getCause() instanceof MaxUploadSizeExceededException) {
					responseWithKeyValue("msg", "上传文件不能超过10M", srequest, sresponse);
				} else {
					logger.error("Fatal error when user try to call API ", e);
					responseServerError(e, (HttpServletRequest) request, (HttpServletResponse) response);

				}
			} else if (e instanceof LoginException) {
				forceLogin((HttpServletRequest) request, (HttpServletResponse) response);
			} else {
				logger.error("Fatal error when user try to call API ", e);
				responseServerError(e, (HttpServletRequest) request, (HttpServletResponse) response);
			}

		}

		EWeblibThreadLocal.removeAll();
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {

	}

	private void cookieCheck(HttpServletRequest request, HttpServletResponse response) {
		// if (request.getSession().getAttribute(BaseEntity.ID) == null &&
		// request.getCookies() != null) {
		// String account = null;
		// String ssid = null;
		// for (Cookie cookie : request.getCookies()) {
		// if (cookie.getName().equals("account")) {
		// try {
		// account = URLDecoder.decode(cookie.getValue(), "UTF-8");
		// } catch (UnsupportedEncodingException e) {
		// e.printStackTrace();
		// }
		// } else if (cookie.getName().equals("ssid")) {
		// ssid = cookie.getValue();
		// }
		// }
		// if (EweblibUtil.isValid(account) && EweblibUtil.isValid(ssid)) {
		// User user = (User) queryDao.findByKeyValue(User.USER_NAME, account,
		// User.TABLE_NAME, User.class);
		// if (user != null && DataEncrypt.generatePassword(user.getUserName() +
		// user.getPassword()).equals(ssid)) {
		// request.setAttribute("remember", "on");
		// setLoginSessionInfo(request, response, user);
		// }
		// }
		// }
	}

	@Override
	public void setLoginSessionInfo(HttpServletRequest request, HttpServletResponse response, BaseEntity user) {

	}
	
	

	public static void roleCheck(HttpServletRequest request) {
		loginCheck(request);

		if (InitialService.rolesPathValidationMap.get(request.getServletPath()) != null) {
			boolean find = false;

			if (EWeblibThreadLocal.getCurrentUserId() != null) {

				// DataBaseQueryBuilder builder = new
				// DataBaseQueryBuilder(User.TABLE_NAME);
				// builder.and(User.ID, EWeblibThreadLocal.getCurrentUserId());
				//
				// User user = (User) queryDao.findOneByQuery(builder,
				// User.class);
				//
				// if (user != null) {
				//
				// }

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

}
