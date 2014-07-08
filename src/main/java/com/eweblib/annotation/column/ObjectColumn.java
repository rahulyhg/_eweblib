package com.eweblib.annotation.column;

@java.lang.annotation.Target(value = { java.lang.annotation.ElementType.METHOD, java.lang.annotation.ElementType.FIELD })
@java.lang.annotation.Retention(value = java.lang.annotation.RetentionPolicy.RUNTIME)
public abstract @interface ObjectColumn {

	public abstract String className() default "";
}
