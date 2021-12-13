package org.hf.hfhttpclient_compiler.bean;

import java.util.ArrayList;
import java.util.List;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;

/**********************************
 * @Name: HfApiMethod
 * @Copyright： CreYond
 * @CreateDate： 2021/12/10 11:02
 * @author: HuangFeng
 * @Version： 1.0
 * @Describe:
 *
 **********************************/
public class HfApiMethod {
    private ExecutableElement element;
    private int connectTimeOut;
    private int readTimeOut;
    private TypeElement logInterceptor;
    private TypeElement cookieJar;
    private TypeElement dns;
    private TypeElement errorResume;
    private List<TypeElement> networkInterceptor = new ArrayList<>();
    private List<TypeElement> flatMaps = new ArrayList<>();
    private List<TypeElement> interceptors = new ArrayList<>();

    public HfApiMethod(ExecutableElement element) {
        this.element = element;
    }

    public ExecutableElement getElement() {
        return element;
    }

    public void setElement(ExecutableElement element) {
        this.element = element;
    }

    public int getConnectTimeOut() {
        return connectTimeOut;
    }

    public void setConnectTimeOut(int connectTimeOut) {
        this.connectTimeOut = connectTimeOut;
    }

    public int getReadTimeOut() {
        return readTimeOut;
    }

    public void setReadTimeOut(int readTimeOut) {
        this.readTimeOut = readTimeOut;
    }

    public TypeElement getLogInterceptor() {
        return logInterceptor;
    }

    public void setLogInterceptor(TypeElement logInterceptor) {
        this.logInterceptor = logInterceptor;
    }

    public TypeElement getErrorResume() {
        return errorResume;
    }

    public void setErrorResume(TypeElement errorResume) {
        this.errorResume = errorResume;
    }

    public TypeElement getCookieJar() {
        return cookieJar;
    }

    public void setCookieJar(TypeElement cookieJar) {
        this.cookieJar = cookieJar;
    }

    public List<TypeElement> getNetworkInterceptor() {
        return networkInterceptor;
    }

    public void addNetworkInterceptor(TypeElement typeElement) {
        this.networkInterceptor.add(typeElement);
    }

    public List<TypeElement> getInterceptors() {
        return interceptors;
    }

    public void addInterceptor(TypeElement typeElement) {
        this.interceptors.add(typeElement);
    }

    public List<TypeElement> getFlatMaps() {
        return flatMaps;
    }

    public void addFlatMap(TypeElement typeElement) {
        this.flatMaps.add(typeElement);
    }


    public TypeElement getDns() {
        return dns;
    }

    public void setDns(TypeElement dns) {
        this.dns = dns;
    }
}
