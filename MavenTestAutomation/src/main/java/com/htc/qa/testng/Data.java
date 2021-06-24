package com.htc.qa.testng;

import static java.lang.annotation.ElementType.METHOD;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Define a data file for test test method or class
 * 
 *  @author anushar
 *
 */
@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
@Target({ METHOD })

public @interface Data {

    /**
     * Specifies the name of the file to read.
     * 
     */
    public String fileName() default "";

    /**
     * Specifies the row to start using the data in the csv file.
     * 
     */
    public int startRow() default 0;

    /**
     * Specifies the row to stop using the data in the csv file.
     * 
     */
    public int endRow() default 0;

}
