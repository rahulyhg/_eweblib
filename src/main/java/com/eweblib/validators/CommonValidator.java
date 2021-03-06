/*
 * $Id: TestValidator.java 155434 2005-02-26 13:16:41Z dirkv $
 * $Rev: 155434 $
 * $Date: 2005-02-26 13:16:41 +0000 (Sat, 26 Feb 2005) $
 *
 * ====================================================================
 * Copyright 2001-2005 The Apache Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.eweblib.validators;

import java.util.Date;
import java.util.Locale;

import org.apache.commons.validator.Field;
import org.apache.commons.validator.GenericTypeValidator;
import org.apache.commons.validator.GenericValidator;
import org.apache.commons.validator.Validator;
import org.apache.commons.validator.ValidatorException;
import org.apache.commons.validator.util.ValidatorUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.eweblib.constants.EWebLibConstants;
import com.eweblib.exception.ResponseException;
import com.eweblib.util.EweblibUtil;

/**
 * Contains validation methods for different unit tests.
 */
public class CommonValidator {
	
	private static Logger logger = LogManager.getLogger(CommonValidator.class);

    /**
     * Throws a runtime exception if the value of the argument is "RUNTIME", an
     * exception if the value of the argument is "CHECKED", and a
     * ValidatorException otherwise.
     * 
     * @throws RuntimeException
     *             with "RUNTIME-EXCEPTION as message" if value is "RUNTIME"
     * @throws Exception
     *             with "CHECKED-EXCEPTION" as message if value is "CHECKED"
     * @throws ValidatorException
     *             with "VALIDATOR-EXCEPTION" as message otherwise
     */
    public static boolean validateRaiseException(final Object bean, final Field field) throws Exception {

        final String value = ValidatorUtils.getValueAsString(bean, field.getProperty());

        if ("RUNTIME".equals(value)) {
            throw new RuntimeException("RUNTIME-EXCEPTION");

        } else if ("CHECKED".equals(value)) {
            throw new Exception("CHECKED-EXCEPTION");

        } else {
            throw new ValidatorException("VALIDATOR-EXCEPTION");
        }
    }

    /**
     * Checks if the field is required.
     * 
     * @return boolean If the field isn't <code>null</code> and has a length
     *         greater than zero, <code>true</code> is returned. Otherwise
     *         <code>false</code>.
     */
    public static boolean validateRequired(Object bean, Field field) {
        String value = ValidatorUtils.getValueAsString(bean, field.getProperty());

        return !GenericValidator.isBlankOrNull(value);
    }
    
    /**
     * Checks the field length.
     */
	public static boolean validateMaxLength(Object bean, Field field) {
		String value = ValidatorUtils.getValueAsString(bean, field.getProperty());
		if (field.getVarValue("maxLength") != null) {
			int max = Integer.parseInt(field.getVarValue("maxLength"));
			return GenericValidator.maxLength(value, max);
		}

		return true;
	}
    
    public static boolean validateMinLength(Object bean, Field field) {
        String value = ValidatorUtils.getValueAsString(bean, field.getProperty());
        if (field.getVarValue("minLength") != null) {
            int min = Integer.parseInt(field.getVarValue("minLength"));
            return GenericValidator.minLength(value, min);
        }
        return true;
    }

    /**
     * Checks if the field can be successfully converted to a <code>byte</code>.
     * 
     * @param value
     *            The value validation is being performed on.
     * @return boolean If the field can be successfully converted to a
     *         <code>byte</code> <code>true</code> is returned. Otherwise
     *         <code>false</code>.
     */
    public static boolean validateByte(Object bean, Field field) {
        String value = ValidatorUtils.getValueAsString(bean, field.getProperty());

        return GenericValidator.isByte(value);
    }

    /**
     * Checks if the field can be successfully converted to a <code>short</code>
     * .
     * 
     * @param value
     *            The value validation is being performed on.
     * @return boolean If the field can be successfully converted to a
     *         <code>short</code> <code>true</code> is returned. Otherwise
     *         <code>false</code>.
     */
    public static boolean validateShort(Object bean, Field field) {
        String value = ValidatorUtils.getValueAsString(bean, field.getProperty());

        return GenericValidator.isShort(value);
    }

    /**
     * Checks if the field can be successfully converted to a <code>int</code>.
     * 
     * @param value
     *            The value validation is being performed on.
     * @return boolean If the field can be successfully converted to a
     *         <code>int</code> <code>true</code> is returned. Otherwise
     *         <code>false</code>.
     */
    public static boolean validateInt(Object bean, Field field) {
        String value = ValidatorUtils.getValueAsString(bean, field.getProperty());

        if (EweblibUtil.isEmpty(value)) {
            return true;
        }
        if (!GenericValidator.isInt(value)) {
            return false;
        }

        if (field.getVarValue("lt") != null) {
            int lt = Integer.parseInt(field.getVarValue("lt"));
            int old = Integer.parseInt(value);

            if (old < lt) {
                return false;
            }

        }
        return true;
    }

    /**
     * Checks if field is positive assuming it is an integer
     * 
     * @param value
     *            The value validation is being performed on.
     * @param field
     *            Description of the field to be evaluated
     * @return boolean If the integer field is greater than zero, returns true,
     *         otherwise returns false.
     */
    public static boolean validatePositive(Object bean, Field field) {
        String value = ValidatorUtils.getValueAsString(bean, field.getProperty());

        return GenericTypeValidator.formatInt(value).intValue() > 0;
    }

    /**
     * Checks if the field can be successfully converted to a <code>long</code>.
     * 
     * @param value
     *            The value validation is being performed on.
     * @return boolean If the field can be successfully converted to a
     *         <code>long</code> <code>true</code> is returned. Otherwise
     *         <code>false</code>.
     */
    public static boolean validateLong(Object bean, Field field) {
        String value = ValidatorUtils.getValueAsString(bean, field.getProperty());

        return GenericValidator.isLong(value);
    }

    /**
     * Checks if the field can be successfully converted to a <code>float</code>
     * .
     * 
     * @param value
     *            The value validation is being performed on.
     * @return boolean If the field can be successfully converted to a
     *         <code>float</code> <code>true</code> is returned. Otherwise
     *         <code>false</code>.
     */
    public static boolean validateFloat(Object bean, Field field) {
        String value = ValidatorUtils.getValueAsString(bean, field.getProperty());

        return GenericValidator.isFloat(value);
    }

    /**
     * Checks if the field can be successfully converted to a
     * <code>double</code>.
     * 
     * @param value
     *            The value validation is being performed on.
     * @return boolean If the field can be successfully converted to a
     *         <code>double</code> <code>true</code> is returned. Otherwise
     *         <code>false</code>.
     */
    public static boolean validateDouble(Object bean, Field field) {
        String value = ValidatorUtils.getValueAsString(bean, field.getProperty());

        return GenericValidator.isDouble(value);
    }

    /**
     * Checks if the field is an e-mail address.
     * 
     * @param value
     *            The value validation is being performed on.
     * @return boolean If the field is an e-mail address <code>true</code> is
     *         returned. Otherwise <code>false</code>.
     */
    public static boolean validateEmail(Object bean, Field field) {
        String value = ValidatorUtils.getValueAsString(bean, field.getProperty());
        return GenericValidator.isEmail(value);
    }

    public final static String FIELD_TEST_NULL = "NULL";
    public final static String FIELD_TEST_NOTNULL = "NOTNULL";
    public final static String FIELD_TEST_EQUAL = "EQUAL";

    public static boolean validateRequiredIf(Object bean, Field field, Validator validator) {

        Object form = validator.getParameterValue(Validator.BEAN_PARAM);
        String value = null;
        boolean required = false;
        if (isString(bean)) {
            value = (String) bean;
        } else {
            value = ValidatorUtils.getValueAsString(bean, field.getProperty());
        }
        int i = 0;
        String fieldJoin = "AND";
        if (!GenericValidator.isBlankOrNull(field.getVarValue("fieldJoin"))) {
            fieldJoin = field.getVarValue("fieldJoin");
        }
        if (fieldJoin.equalsIgnoreCase("AND")) {
            required = true;
        }
        while (!GenericValidator.isBlankOrNull(field.getVarValue("field[" + i + "]"))) {
            String dependProp = field.getVarValue("field[" + i + "]");
            String dependTest = field.getVarValue("fieldTest[" + i + "]");
            String dependTestValue = field.getVarValue("fieldValue[" + i + "]");
            String dependIndexed = field.getVarValue("fieldIndexed[" + i + "]");
            if (dependIndexed == null)
                dependIndexed = "false";
            String dependVal = null;
            boolean this_required = false;
            if (field.isIndexed() && dependIndexed.equalsIgnoreCase("true")) {
                String key = field.getKey();
                if ((key.indexOf("[") > -1) && (key.indexOf("]") > -1)) {
                    String ind = key.substring(0, key.indexOf(".") + 1);
                    dependProp = ind + dependProp;
                }
            }
            dependVal = ValidatorUtils.getValueAsString(form, dependProp);
            if (dependTest.equals(FIELD_TEST_NULL)) {
                if ((dependVal != null) && (dependVal.length() > 0)) {
                    this_required = false;
                } else {
                    this_required = true;
                }
            }
            if (dependTest.equals(FIELD_TEST_NOTNULL)) {
                if ((dependVal != null) && (dependVal.length() > 0)) {
                    this_required = true;
                } else {
                    this_required = false;
                }
            }
            if (dependTest.equals(FIELD_TEST_EQUAL)) {
                this_required = dependTestValue.equalsIgnoreCase(dependVal);
            }
            if (fieldJoin.equalsIgnoreCase("AND")) {
                required = required && this_required;
            } else {
                required = required || this_required;
            }
            i++;
        }
        if (required) {
            if ((value != null) && (value.length() > 0)) {
                return true;
            } else {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if the field can be successfully converted to a <code>byte</code>.
     * 
     * @param value
     *            The value validation is being performed on.
     * @return boolean If the field can be successfully converted to a
     *         <code>byte</code> <code>true</code> is returned. Otherwise
     *         <code>false</code>.
     */
    public static Byte validateByte(Object bean, Field field, Locale locale) {
        String value = ValidatorUtils.getValueAsString(bean, field.getProperty());

        return GenericTypeValidator.formatByte(value, locale);
    }

    /**
     * Checks if the field can be successfully converted to a <code>short</code>
     * .
     * 
     * @param value
     *            The value validation is being performed on.
     * @return boolean If the field can be successfully converted to a
     *         <code>short</code> <code>true</code> is returned. Otherwise
     *         <code>false</code>.
     */
    public static Short validateShort(Object bean, Field field, Locale locale) {
        String value = ValidatorUtils.getValueAsString(bean, field.getProperty());

        return GenericTypeValidator.formatShort(value, locale);
    }

    /**
     * Checks if the field can be successfully converted to a <code>int</code>.
     * 
     * @param value
     *            The value validation is being performed on.
     * @return boolean If the field can be successfully converted to a
     *         <code>int</code> <code>true</code> is returned. Otherwise
     *         <code>false</code>.
     */
    public static Integer validateInt(Object bean, Field field, Locale locale) {
        String value = ValidatorUtils.getValueAsString(bean, field.getProperty());

        return GenericTypeValidator.formatInt(value, locale);
    }

    /**
     * Checks if the field can be successfully converted to a <code>long</code>.
     * 
     * @param value
     *            The value validation is being performed on.
     * @return boolean If the field can be successfully converted to a
     *         <code>long</code> <code>true</code> is returned. Otherwise
     *         <code>false</code>.
     */
    public static Long validateLong(Object bean, Field field, Locale locale) {
        String value = ValidatorUtils.getValueAsString(bean, field.getProperty());

        return GenericTypeValidator.formatLong(value, locale);
    }

    /**
     * Checks if the field can be successfully converted to a <code>float</code>
     * .
     * 
     * @param value
     *            The value validation is being performed on.
     * @return boolean If the field can be successfully converted to a
     *         <code>float</code> <code>true</code> is returned. Otherwise
     *         <code>false</code>.
     */
    public static Float validateFloat(Object bean, Field field, Locale locale) {
        String value = ValidatorUtils.getValueAsString(bean, field.getProperty());

        return GenericTypeValidator.formatFloat(value, locale);
    }

    /**
     * Checks if the field can be successfully converted to a
     * <code>double</code>.
     * 
     * @param value
     *            The value validation is being performed on.
     * @return boolean If the field can be successfully converted to a
     *         <code>double</code> <code>true</code> is returned. Otherwise
     *         <code>false</code>.
     */
    public static Double validateDouble(Object bean, Field field, Locale locale) {
        String value = ValidatorUtils.getValueAsString(bean, field.getProperty());

        return GenericTypeValidator.formatDouble(value, locale);
    }

    /**
     * Checks if the field can be successfully converted to a <code>date</code>.
     * 
     * @param value
     *            The value validation is being performed on.
     * @return boolean If the field can be successfully converted to a
     *         <code>date</code> <code>true</code> is returned. Otherwise
     *         <code>false</code>.
     */
    public static Date validateDate(Object bean, Field field, Locale locale) {
        String value = ValidatorUtils.getValueAsString(bean, field.getProperty());

        return GenericTypeValidator.formatDate(value, locale);
    }

    /**
     * Checks if the field can be successfully converted to a <code>date</code>.
     * 
     * @param value
     *            The value validation is being performed on.
     * @return boolean If the field can be successfully converted to a
     *         <code>date</code> <code>true</code> is returned. Otherwise
     *         <code>false</code>.
     */
    public static Date validateDate(Object bean, Field field) {
        String value = ValidatorUtils.getValueAsString(bean, field.getProperty());
        String datePattern = field.getVarValue("datePattern");
        String datePatternStrict = field.getVarValue("datePatternStrict");

        Date result = null;
        if (datePattern != null && datePattern.length() > 0) {
            result = GenericTypeValidator.formatDate(value, datePattern, false);
        } else if (datePatternStrict != null && datePatternStrict.length() > 0) {
            result = GenericTypeValidator.formatDate(value, datePatternStrict, true);
        }

        return result;
    }

    public static boolean validateInValue(Object bean, Field field) {
        String value = ValidatorUtils.getValueAsString(bean, field.getProperty());

        if (value == null) {
            return true;
        }
        String inValue = field.getVarValue("in");
        if (inValue != null) {
            String[] inValues = inValue.split(",");
            for (String v : inValues) {
                if (v.trim().equalsIgnoreCase(value.trim())) {
                    return true;
                }
            }

        } else {
			logger.error(String.format("Must set in values into configuration", bean));
            throw new ResponseException(EWebLibConstants.NOT_SET_IN_VALID_FILE);
        }

        return false;
    }

    private static Class<?> stringClass = new String().getClass();

    private static boolean isString(Object o) {
        if (o == null)
            return true;
        return (stringClass.isInstance(o));
    }

}
