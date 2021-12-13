package org.hf.hfhttpclient_annotations;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.CLASS)
@Target(ElementType.METHOD)
public @interface HfApi {
    int connectTimeSec()default 30;
    int readTimeSec()default 30;
}