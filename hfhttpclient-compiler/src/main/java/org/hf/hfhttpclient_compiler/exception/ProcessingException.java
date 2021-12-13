package org.hf.hfhttpclient_compiler.exception;

import javax.lang.model.element.Element;

/**********************************
 * @Name: ProcessingException
 * @Copyright： CreYond
 * @CreateDate： 2021/12/9 16:25
 * @author: HuangFeng
 * @Version： 1.0
 * @Describe:
 *
 **********************************/
public class ProcessingException extends Exception{
    Element element;

    public ProcessingException(Element element, String msg, Object... args) {
        super(String.format(msg, args));
        this.element = element;
    }

    public Element getElement() {
        return element;
    }

}

