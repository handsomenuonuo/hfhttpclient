package org.hf.hfhttpclient_compiler.bean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;

/**********************************
 * @Name: HfApiInterface
 * @Copyright： CreYond
 * @CreateDate： 2021/12/9 16:16
 * @author: HuangFeng
 * @Version： 1.0
 * @Describe:
 *
 **********************************/
public class HfApiInterface {
    private TypeElement typeElement;
    private String baseUrl ="";
    private TypeElement baseUrlElement;
    private TypeElement logInterceptor;
    private TypeElement cookieJar;
    private TypeElement dns;
    private TypeElement errorResume;
    private List<TypeElement> networkInterceptor = new ArrayList<>();
    private List<TypeElement> interceptors = new ArrayList<>();
    private List<TypeElement> flatMaps = new ArrayList<>();
    private HashMap<String, HfApiMethod> map = new HashMap<>();

    public HfApiInterface(TypeElement typeElement, String baseUrl) {
        this.typeElement = typeElement;
        this.baseUrl = baseUrl;
    }

    public HfApiInterface(TypeElement typeElement, TypeElement baseUrlElement) {
        this.typeElement = typeElement;
        this.baseUrlElement = baseUrlElement;
    }

    public TypeElement getTypeElement() {
        return typeElement;
    }

    public void setTypeElement(TypeElement typeElement) {
        this.typeElement = typeElement;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public HashMap<String, HfApiMethod> getMap() {
        return map;
    }

    public void setMap(HashMap<String, HfApiMethod> map) {
        this.map = map;
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

    public TypeElement getBaseUrlElement() {
        return baseUrlElement;
    }

    public void setBaseUrlElement(TypeElement baseUrlElement) {
        this.baseUrlElement = baseUrlElement;
    }
}
