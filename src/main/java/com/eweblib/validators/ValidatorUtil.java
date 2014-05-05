package com.eweblib.validators;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import org.apache.commons.validator.Arg;
import org.apache.commons.validator.Field;
import org.apache.commons.validator.Form;
import org.apache.commons.validator.Validator;
import org.apache.commons.validator.ValidatorAction;
import org.apache.commons.validator.ValidatorException;
import org.apache.commons.validator.ValidatorResources;
import org.apache.commons.validator.ValidatorResult;
import org.apache.commons.validator.ValidatorResults;

import com.eweblib.bean.BaseEntity;
import com.eweblib.exception.ResponseException;
import com.eweblib.service.AbstractService;
import com.eweblib.util.EweblibUtil;
import com.google.gson.Gson;

public class ValidatorUtil {

	private static ResourceBundle apps = null;

	private static ValidatorResources actionResources = null;

	private static Map<String, ValidatorResources> vresources = null;

	public static void init(Class<?> classzz, String[] validatorsFiles) {
		intiBundle(classzz);
		getValidateActions();
		initValidatorResources(classzz, validatorsFiles);
	}

	public static ResourceBundle intiBundle(Class<?> classzz) {
		if (apps == null) {
			apps = ResourceBundle.getBundle("message", Locale.CHINESE, classzz.getClassLoader());
		}
		return apps;
	}

	public static ValidatorResources getValidateActions() {

		if (actionResources == null) {
			InputStream in = null;
			try {
				in = ValidatorUtil.class.getResourceAsStream("/validators/actions.xml");
				actionResources = new ValidatorResources(in);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				// Make sure we close the input stream.
				if (in != null) {
					try {
						in.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}

		return actionResources;
	}

	public static Map<String, ValidatorResources> initValidatorResources(Class<?> classzz, String[] validatorsFiles) {

		if (vresources == null) {
			vresources = new HashMap<String, ValidatorResources>();
			for (String validator : validatorsFiles) {
				InputStream in = null;
				ValidatorResources resources = null;

				try {
					String validatorPath = "/validators/".concat(validator).concat(".xml");
					in = classzz.getResourceAsStream(validatorPath);
					resources = new ValidatorResources(in);
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					// Make sure we close the input stream.
					if (in != null) {
						try {
							in.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
				Set<String> set = actionResources.getValidatorActions().keySet();
				for (String key : set) {
					resources.addValidatorAction(actionResources.getValidatorAction(key));
				}

				vresources.put(validator, resources);
			}
		}

		return vresources;
	}

	public static void validate(BaseEntity entity, String fileName, String validatorForm, String[] validatorsFiles) {
		ValidatorUtil.init(AbstractService.class, validatorsFiles);

		ValidatorResources resources = ValidatorUtil.initValidatorResources(AbstractService.class, validatorsFiles).get(fileName);

		// Create a validator with the ValidateBean actions for the bean
		// we're interested in.
		Validator validator = new Validator(resources, validatorForm);
		// Tell the validator which bean to validate against.
		validator.setParameter(Validator.BEAN_PARAM, entity);
		ValidatorResults results = null;

		try {
			results = validator.validate();
		} catch (ValidatorException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Start by getting the form for the current locale and Bean.
		Form form = resources.getForm(Locale.CHINA, validatorForm);
		// Iterate over each of the properties of the Bean which had messages.
		Iterator propertyNames = results.getPropertyNames().iterator();
		List<String> msgs = new ArrayList<String>();
		while (propertyNames.hasNext()) {
			String propertyName = (String) propertyNames.next();

			// Get the Field associated with that property in the Form
			Field field = form.getField(propertyName);

			// Get the result of validating the property.
			ValidatorResult result = results.getValidatorResult(propertyName);

			// Get all the actions run against the property, and iterate over
			// their names.
			Map actionMap = result.getActionMap();
			Iterator keys = actionMap.keySet().iterator();
			String msg = "";
			while (keys.hasNext()) {
				String actName = (String) keys.next();
				// Get the Action for that name.
				ValidatorAction action = resources.getValidatorAction(actName);
				String actionMsgKey = field.getArg(0).getKey() + "." + action.getName();
				// Look up the formatted name of the field from the Field arg0
				String prettyFieldName = ValidatorUtil.intiBundle(AbstractService.class).getString(field.getArg(0).getKey());

				boolean customMsg = false;
				if (isArgExists(actionMsgKey, field)) {
					customMsg = true;
					prettyFieldName = ValidatorUtil.intiBundle(AbstractService.class).getString(actionMsgKey);
				}

				if (!result.isValid(actName)) {
					String message = "{0}";
					if (!customMsg) {
						message = ValidatorUtil.intiBundle(AbstractService.class).getString(action.getMsg());
					}
					Object[] argsss = { prettyFieldName };
					try {
						msg = msg.concat(new String(MessageFormat.format(message, argsss).getBytes("ISO-8859-1"), "UTF-8"));
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
				}
			}
			if (!EweblibUtil.isEmpty(msg)) {
				msgs.add(msg);
			}
		}
		
		if(!msgs.isEmpty()){			
			throw new ResponseException(EweblibUtil.toJson(msgs));
		}

	}

	private static boolean isArgExists(String key, Field field) {

		Arg[] args = field.getArgs("");

		for (Arg arg : args) {
			if (arg.getKey().equalsIgnoreCase(key)) {
				return true;
			}
		}

		return false;

	}

}
