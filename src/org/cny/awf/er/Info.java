package org.cny.awf.er;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Info {
	/**
	 * the info name.
	 * 
	 * @return
	 */
	public String name() default "";

	/**
	 * the info.
	 * 
	 * @return
	 */
	public String[] info() default {};
}
