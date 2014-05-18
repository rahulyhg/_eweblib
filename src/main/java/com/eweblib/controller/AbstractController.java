package com.eweblib.controller;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.registry.infomodel.User;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.eweblib.bean.BaseEntity;
import com.eweblib.bean.EntityResults;
import com.eweblib.bean.OrderBy;
import com.eweblib.bean.Pagination;
import com.eweblib.constants.EWebLibConstants;
import com.eweblib.exception.ResponseException;
import com.eweblib.util.DataEncrypt;
import com.eweblib.util.EWeblibThreadLocal;
import com.eweblib.util.EweblibUtil;

public abstract class AbstractController {
	public static final String MSG = "msg";
	public static final String CODE = "code";
	private static Logger logger = LogManager.getLogger(AbstractController.class);

	protected <T extends BaseEntity> BaseEntity parserJsonParameters(HttpServletRequest request, boolean emptyParameter, Class<T> claszz) {
		HashMap<String, Object> parametersMap = parserJsonParameters(request, emptyParameter);
		EweblibUtil.updateJsonFieldWithType(parametersMap, claszz);
		logger.debug(String.format("--------------Client post parameters for path [%s] is [%s]", request.getServletPath(), parametersMap));

		return EweblibUtil.toEntity(parametersMap, claszz);

	}
	
	protected <T extends BaseEntity> List<T> parserListJsonParameters(HttpServletRequest request, boolean emptyParameter, Class<T> claszz) {
		Map<String, Object> params = this.parserJsonParameters(request, false);
		List<T> list = EweblibUtil.toJsonList(params, claszz);

		return list;
	}

	protected HashMap<String, Object> parserJsonParameters(HttpServletRequest request, boolean emptyParameter) {
		HashMap<String, Object> parametersMap = new HashMap<String, Object>();


		int filterLength = 0;

		Enumeration<?> parameterNames = request.getParameterNames();
		while (parameterNames.hasMoreElements()) {
			String pName = parameterNames.nextElement().toString();

			if (pName.toLowerCase().startsWith("filter[filters]".toLowerCase())) {
				filterLength++;
			} else {

				if (pName.indexOf("[]") != -1) {
					// 数组参数
					String pNameKey = pName.replace("[]", "");
					parametersMap.put(pNameKey, request.getParameterValues(pName));
				} else {
					String parameter = request.getParameter(pName).trim();
					parameter = parameter.replaceAll("<script>", "");
					parameter = parameter.replaceAll("</script>", "");
					parametersMap.put(pName, parameter);
				}
			}
		}

		if (filterLength > 0) {
			// 每三个为一组
			int filters = (int) filterLength / 3;
			for (int i = 0; i < filters; i++) {
				String key = request.getParameter("filter[filters][" + i + "][field]");
				String operator = request.getParameter("filter[filters][" + i + "][operator]");
				String value = request.getParameter("filter[filters][" + i + "][value]");
				// parametersMap.put(key, new
				// DataBaseQuery(DataBaseQueryOpertion.getOperation(operator),
				// value));
			}
		}
		if (EweblibUtil.isEmpty(parametersMap) && !emptyParameter) {
			logger.error(String.format("Parameters required for path [%s]", request.getPathInfo()));
			throw new ResponseException(EWebLibConstants.PARAMETER_REQUIRED);
		}

		parametersMap.remove("_");
		parametersMap.remove("callback");

		parametersMap.remove("filter[logic]");
		parametersMap.remove("filter");

		if (parametersMap.get(EWebLibConstants.CURRENT_PAGE) != null && parametersMap.get(EWebLibConstants.PAGE_SIZE) != null) {
			Pagination pagination = new Pagination();
			pagination.setPage(EweblibUtil.getInteger(parametersMap.get(EWebLibConstants.CURRENT_PAGE), 0));
			// default is 10;
			pagination.setRows(EweblibUtil.getInteger(parametersMap.get(EWebLibConstants.PAGE_SIZE), 10));
			EWeblibThreadLocal.set(EWebLibConstants.PAGENATION, pagination);
		}

		// FIXME: only support sort by one column
		if (parametersMap.get("sort") != null) {

			OrderBy order = new OrderBy();
			order.setOrder(parametersMap.get("order").toString());
			order.setSort(parametersMap.get("sort").toString());
			
			EWeblibThreadLocal.set(EWebLibConstants.DB_QUERY_ORDER_BY, order);

		}

		parametersMap.remove("createdOn");
		parametersMap.remove("updatedOn");
		return parametersMap;
	}
	
	protected void responseWithEntity(BaseEntity data, HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("data", data);
		responseMsg(map, ResponseStatus.SUCCESS, request, response, null);
	}

	protected void responseWithData(Map<String, Object> data, HttpServletRequest request, HttpServletResponse response) {
		responseMsg(data, ResponseStatus.SUCCESS, request, response, null);
	}

	protected <T extends BaseEntity> void responseWithDataPagnation(EntityResults<T> listBean, HttpServletRequest request, HttpServletResponse response) {
		if (listBean != null) {
			Map<String, Object> list = new HashMap<String, Object>();
			list.put("total", listBean.getPagnation().getTotal());
			list.put("rows", listBean.getEntityList());
			responseMsg(list, ResponseStatus.SUCCESS, request, response, null);
		} else {
			responseMsg(null, ResponseStatus.SUCCESS, request, response, null);
		}
	}
	
	
	protected <T extends BaseEntity> void responseWithDataPagnationForApp(EntityResults<T> listBean, HttpServletRequest request, HttpServletResponse response) {
		if (listBean != null) {
			Map<String, Object> list = new HashMap<String, Object>();
			list.put("total", listBean.getPagnation().getTotal());
			list.put("data", listBean.getEntityList());
			responseMsg(list, ResponseStatus.SUCCESS, request, response, null);
		} else {
			responseMsg(null, ResponseStatus.SUCCESS, request, response, null);
		}
	}
	
	

	protected <T extends BaseEntity> void responseWithDataPagnation(EntityResults<T> listBean, Map<String, Object> results, HttpServletRequest request, HttpServletResponse response) {
		if(results == null){
			results =  new HashMap<String, Object>();
		}
		if (listBean != null) {
			results.put("total", listBean.getPagnation().getTotal());
			results.put("rows", listBean.getEntityList());
			responseMsg(results, ResponseStatus.SUCCESS, request, response, null);
		} else {
			responseMsg(null, ResponseStatus.SUCCESS, request, response, null);
		}
	}
	
	protected <T extends BaseEntity> void responseWithListData(List<T> listBean, HttpServletRequest request, HttpServletResponse response) {
		if (listBean != null) {
			Map<String, Object> list = new HashMap<String, Object>();
			list.put("rows", listBean);
			responseMsg(list, ResponseStatus.SUCCESS, request, response, null);
		} else {
			responseMsg(null, ResponseStatus.SUCCESS, request, response, null);
		}
	}
	
	protected <T extends BaseEntity> void responseWithListDataForApp(List<T> listBean, HttpServletRequest request, HttpServletResponse response) {
		if (listBean != null) {
			Map<String, Object> list = new HashMap<String, Object>();
			list.put("data", listBean);
			responseMsg(list, ResponseStatus.SUCCESS, request, response, null);
		} else {
			responseMsg(null, ResponseStatus.SUCCESS, request, response, null);
		}
	}

	protected void responseWithKeyValue(String key, String value, HttpServletRequest request, HttpServletResponse response) {
		if (key == null) {
			responseWithData(null, request, response);
		} else {
			Map<String, Object> temp = new HashMap<String, Object>();
			temp.put(key, value);
			responseWithData(temp, request, response);
		}
	}

	protected void forceLogin(HttpServletRequest request, HttpServletResponse response) {
		//clearLoginSession(request, response);

//		try {
//			response.sendRedirect("/login.jsp");
//		} catch (IOException e) {
//			logger.fatal("Write response data to client failed!", e);
//		}
	}

	/**
	 * This function will return JSON data to Client
	 * 
	 * 
	 * @param data
	 *            data to return to client
	 * @param dataKey
	 *            if set dataKey, the JSON format use dataKey as the JSON key,
	 *            data as it's value, and both the dataKey and "code" key are
	 *            child of the JSON root node. If not set dataKey, the data and
	 *            the "code" node are both the child of the JSON root node
	 * @param status
	 *            0:FAIL, 1: SUCCESS
	 * @return
	 */
	private void responseMsg(Map<String, Object> data, ResponseStatus status, HttpServletRequest request, HttpServletResponse response, String msgKey) {
		if (data == null) {
			data = new HashMap<String, Object>();
		}
		data.put(CODE, EweblibUtil.getInteger(status.toString(), 1));
		response.setContentType("text/plain;charset=UTF-8");
		response.addHeader("Accept-Encoding", "gzip, deflate");
		String jsonReturn = EweblibUtil.toJson(data);
		String callback = request.getParameter("callback");


		if (callback != null) {
			if (data != null && data instanceof Map) {
				jsonReturn = callback + "(" + jsonReturn + ");";
			} else {
				// 不返回任何数据
				jsonReturn = callback + "();";
			}
		}
		try {
			response.getWriter().write(jsonReturn);
		} catch (IOException e) {
			logger.fatal("Write response data to client failed!", e);
		}

	}

	protected void responseServerError(Throwable throwable, HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> temp = new HashMap<String, Object>();
		temp.put(CODE, ResponseStatus.ERROR.toString());
		if (throwable instanceof ResponseException) {
			ResponseException apiException = (ResponseException) throwable;
			temp.put(MSG, apiException.getMessage());
			logger.error(apiException.getMessage());
		} else {
			temp.put(CODE, ResponseStatus.FAIL.toString());
			temp.put(MSG, "服务器错误，请稍后再试");
		}
		responseMsg(temp, ResponseStatus.FAIL, request, response, null);

	}

	protected void setSessionValue(HttpServletRequest request, String key, Object value) {

		request.getSession().setAttribute(key, value);
	}

	protected String getSessionValue(HttpServletRequest request, String key) {

		if (request.getSession().getAttribute(key) != null) {
			return request.getSession().getAttribute(key).toString();
		}

		return null;
	}

	protected void removeSessionInfo(HttpServletRequest request) {
	
		Enumeration<String> e =  request.getSession().getAttributeNames();
		while (e.hasMoreElements()) {
			String nextElement = e.nextElement();
			request.getSession().removeAttribute(nextElement);
		}
	}
	

    public abstract void setLoginSessionInfo(HttpServletRequest request, HttpServletResponse response, BaseEntity user);

	protected void clearLoginSession(HttpServletRequest request, HttpServletResponse response){
		removeSessionInfo(request);

		String path = EweblibUtil.isEmpty(request.getContextPath()) ? "/" : request.getContextPath();
		Cookie account = new Cookie("account", null);
		account.setMaxAge(0);
		account.setPath(path);
		
		Cookie ssid = new Cookie("ssid", null);
		ssid.setMaxAge(0);
		ssid.setPath(path);
		
		response.addCookie(account);
		response.addCookie(ssid);
	}
	
	

	

	protected String uploadFile(HttpServletRequest request, String relativeFilePath, String parameterName) {
		String webPath = request.getSession().getServletContext().getRealPath("/");

	    MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
		MultipartFile uploadFile = multipartRequest.getFile(parameterName);
		String imgFileName = uploadFile.getOriginalFilename().toLowerCase().trim().replaceAll(" ", "");
		
		String ms = Long.toString(new Date().getTime());
		imgFileName = ms + imgFileName;
		if (EweblibUtil.isEmpty(imgFileName)){

		}else{
			
			if( uploadFile.getSize() > 512 * 1024){
				throw new ResponseException("图片大小不能超过512K");
			}
			if (imgFileName.endsWith("gif") || imgFileName.endsWith("jpg") || imgFileName.endsWith("png") | imgFileName.endsWith("jpeg")) {

				InputStream inputStream = null;
				try {
					inputStream = uploadFile.getInputStream();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("inputStream", inputStream);
				map.put("webPath",  webPath);
				String fileName = uploadFile.getOriginalFilename().trim().replaceAll(" ", "");
				map.put("fileName", fileName);

				return createFile(map, relativeFilePath + fileName);
			} else {
				throw new ResponseException("请上传店铺图片照片! 只支持（GIF|JPG|PNG|JPEG）格式!");
			}
		}
		
		return null;
		
    }
	
	
    public String createFile(Map<String, Object> params, String relativeFilePath) {
	    InputStream is = (InputStream) params.get("inputStream");
	    String wwwPath = (String) params.get("webPath");
	    BufferedInputStream bis = null;
	    FileOutputStream fos = null;
	    try {
	        bis = new BufferedInputStream(is);
	        
	        File file = new File(wwwPath + relativeFilePath);
	        File folder = file.getParentFile();
			if (!folder.exists()){
				folder.mkdirs();
			}
	        fos = new FileOutputStream(file);
	        
	        byte[] buf = new byte[1024];    
	        int size = 0;
	        
	        while ( (size = bis.read(buf)) != -1){
	        	fos.write(buf, 0, size);
	        }
	        
	        if(bis != null)
        		bis.close();
        	
        	if (fos != null)
        		fos.close();
	        
        } catch (FileNotFoundException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
        } catch (IOException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
        }
	    return relativeFilePath;
    }
	
	protected String genRandomRelativePath(String userId){
		StringBuffer sb = new StringBuffer("/");
		sb.append("upload/").append(userId).append("/");
		return sb.toString();
	}

	


	public enum ResponseStatus {

		SUCCESS {
			public String toString() {
				return "1";

			}
		},

		FAIL {
			public String toString() {
				return "0";

			}
		},

		ERROR {
			public String toString() {
				return "-1";

			}
		}
	}

}
