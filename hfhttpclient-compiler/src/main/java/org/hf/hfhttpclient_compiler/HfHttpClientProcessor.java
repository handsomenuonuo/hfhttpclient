package org.hf.hfhttpclient_compiler;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeSpec;

import org.hf.hfhttpclient_annotations.HfApi;
import org.hf.hfhttpclient_annotations.HfCookieJar;
import org.hf.hfhttpclient_annotations.HfDns;
import org.hf.hfhttpclient_annotations.HfErrorResume;
import org.hf.hfhttpclient_annotations.HfFlatMap;
import org.hf.hfhttpclient_annotations.HfInterceptor;
import org.hf.hfhttpclient_annotations.HfLogInterceptor;
import org.hf.hfhttpclient_annotations.HfNetWorkInterceptor;
import org.hf.hfhttpclient_annotations.HfInterface;
import org.hf.hfhttpclient_compiler.bean.HfApiInterface;
import org.hf.hfhttpclient_compiler.bean.HfApiMethod;
import org.hf.hfhttpclient_compiler.exception.ProcessingException;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

@AutoService(Processor.class)
public class HfHttpClientProcessor extends AbstractProcessor{

    private Messager messager;
    private Elements elementsUtils;
    private Filer filer;
    private Types typeUtils;


    Map<String, HfApiInterface> targetMap = new HashMap<>();
    ClassName hfHttpClient = ClassName.bestGuess("org.hf.hfhttpclient.HfHttpClient");
    ClassName schedulers = ClassName.bestGuess("io.reactivex.schedulers.Schedulers");
    ClassName androidSchedulers = ClassName.bestGuess("io.reactivex.android.schedulers.AndroidSchedulers");
    ClassName interceptor = ClassName.bestGuess("okhttp3.Interceptor");
    ClassName cookieJar = ClassName.bestGuess("okhttp3.CookieJar");

    TypeMirror elementInterceptor ,elementFlatMap,elementHfUrl,elementNONE;
    /**
     *
     *
     *
     * @param processingEnv
     */
    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        typeUtils = processingEnv.getTypeUtils();
        elementsUtils = processingEnv.getElementUtils();
        filer = processingEnv.getFiler();
        messager = processingEnv.getMessager();
        elementInterceptor = elementsUtils.getTypeElement("okhttp3.Interceptor").asType();
        elementFlatMap = elementsUtils.getTypeElement("io.reactivex.functions.Function").asType();
        elementHfUrl = elementsUtils.getTypeElement("org.hf.hfhttpclient.interfaces.HfUrl").asType();
        elementNONE = elementsUtils.getTypeElement("org.hf.hfhttpclient_annotations.HfInterface.NONE").asType();
    }

    /**
     * 返回此Porcessor可以处理的注解操作
     *
     * @return
     */
    @Override
    public Set<String> getSupportedOptions() {
        return super.getSupportedOptions();
    }

    /**
     * 返回此注释 Processor 支持的最新的源版本
     * <p>
     * 该方法可以通过注解@SupportedSourceVersion指定
     *
     * @return
     */
    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
        // return super.getSupportedSourceVersion();
    }

    /**
     * 返回此 Processor 支持的注释类型的名称。结果元素可能是某一受支持注释类型的规范（完全限定）名称。
     * 它也可能是 " name.*" 形式的名称，表示所有以 " name." 开头的规范名称的注释类型集合。
     * 最后， "*" 自身表示所有注释类型的集合，包括空集。
     * 注意，Processor 不应声明 "*"，除非它实际处理了所有文件；声明不必要的注释可能导致在某些环境中的性能下降。
     * <p>
     * 该方法可以通过注解@getSupportedAnnotationTypes指定
     *
     * @return
     */
    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> set = new LinkedHashSet<>();
        set.add(HfInterface.class.getCanonicalName());
        set.add(HfApi.class.getCanonicalName());
        set.add(HfLogInterceptor.class.getCanonicalName());
        set.add(HfCookieJar.class.getCanonicalName());
        set.add(HfDns.class.getCanonicalName());
        set.add(HfNetWorkInterceptor.class.getCanonicalName());
        set.add(HfInterceptor.class.getCanonicalName());
        set.add(HfFlatMap.class.getCanonicalName());
        set.add(HfErrorResume.class.getCanonicalName());
        return set;
    }

    /**
     * 注解处理器的核心方法，处理具体的注解
     *
     * @param set
     * @param roundEnvironment
     * @return
     */
    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment){

        // 通过roundEnvironment扫描所有的类文件，获取所有存在指定注解的字段
        try {
            getBaseUrl(roundEnvironment);
            getTimeOut(roundEnvironment);
            getLogInterceptor(roundEnvironment);
            getCookieJar(roundEnvironment);
            getDns(roundEnvironment);
            getNetworkInterceptor(roundEnvironment);
            getInterceptor(roundEnvironment);
            getFlatMap(roundEnvironment);
            getErrorResume(roundEnvironment);
        } catch (ProcessingException e) {

            e.printStackTrace();
        }

          createJavaFile(targetMap.entrySet());
        return true;
    }

    /**
     * 获取所有存在注解的类
     *
     * @param roundEnvironment
     * @return
     */
    private void getBaseUrl(RoundEnvironment roundEnvironment) throws ProcessingException {
        // 1、获取代码中所有使用@HfBaseUrl注解修饰的类
        Set<? extends Element> annotatedElements = roundEnvironment.getElementsAnnotatedWith(HfInterface.class);
        for (Element element : annotatedElements) {
            //必须是public修饰得类
            if (!element.getModifiers().contains(Modifier.PUBLIC)) {
                throw new ProcessingException(element, "The class %s is not public.",
                        ((TypeElement)element).getQualifiedName().toString());
            }
            if (element.getKind() != ElementKind.INTERFACE) {
                throw new ProcessingException(element, "Only interface can be annotated with @%s",
                        HfInterface.class.getSimpleName());
            }
           if(((TypeElement)element).getEnclosingElement().getKind() != ElementKind.PACKAGE){
               throw new ProcessingException(element,  "%s必须是最外部类",
                       ((TypeElement)element).getQualifiedName().toString());
           }

           String baseUrl = element.getAnnotation(HfInterface.class).baseUrl();
           if(!baseUrl.equals("")){//如果设置了baseUrl就直接设置
               HfApiInterface hfApiInterface = new HfApiInterface((TypeElement)element,element.getAnnotation(HfInterface.class).baseUrl());
               targetMap.put(element.getSimpleName().toString(),hfApiInterface);
               continue;
           }
           //没设置Url就从class获取
            TypeMirror tm = null;
            try {
                element.getAnnotation(HfInterface.class).getUrl();
            }catch (MirroredTypeException e){
                tm = e.getTypeMirror();
            }
            if(tm!=null){
                TypeElement typeElement = (TypeElement) typeUtils.asElement(tm);
                checkOnlyVoidConstruct(typeElement);
                if(!typeElement.getInterfaces().contains(elementHfUrl)){
                    if(typeUtils.isSameType(typeElement.asType(),elementNONE)){
                        throw new ProcessingException(element,  "%s没有设置HfInterface的baseurl或者getUrl注解",
                                ((TypeElement)element).getQualifiedName().toString());
                    }
                    throw new ProcessingException(element,  "%s必须是实现了org.hf.hfhttpclient.interfaces.HfUrl方法",
                                typeElement.getQualifiedName().toString());
                }
                HfApiInterface hfApiInterface = new HfApiInterface((TypeElement)element,typeElement);
                targetMap.put(element.getSimpleName().toString(),hfApiInterface);
            }
        }
    }

    private void getTimeOut(RoundEnvironment roundEnvironment) throws ProcessingException {
        // 1、获取代码中所有使用@HfBaseUrl注解修饰的类
        Set<? extends Element> annotatedElements = roundEnvironment.getElementsAnnotatedWith(HfApi.class);
        for (Element element : annotatedElements) {
            //必须是接口下的方法
            if(element.getEnclosingElement().getKind() != ElementKind.INTERFACE){
                throw new ProcessingException(element,  "%s必须是接口下的方法",
                        element.getSimpleName().toString());
            }
            //必须是最外层接口下的直接子方法
            if(element.getEnclosingElement().getEnclosingElement().getKind() != ElementKind.PACKAGE){
                throw new ProcessingException(element,  "%s必须是最外层接口下的直接子方法",
                        element.getSimpleName().toString());
            }
            String superName = element.getEnclosingElement().getSimpleName().toString();
            HfApiInterface apiInterface = targetMap.get(superName);
            if(apiInterface!=null){
                Map<String, HfApiMethod> map = apiInterface.getMap();
                HfApiMethod hfApiMethod = map.get(element.getSimpleName().toString());
                if(hfApiMethod == null){
                    hfApiMethod = new HfApiMethod((ExecutableElement) element);
                    map.put(element.getSimpleName().toString(), hfApiMethod);
                }
                hfApiMethod.setConnectTimeOut(element.getAnnotation(HfApi.class).connectTimeSec());
                hfApiMethod.setReadTimeOut(element.getAnnotation(HfApi.class).readTimeSec());
            }
        }
    }

    private void getLogInterceptor(RoundEnvironment roundEnvironment) throws ProcessingException {
        // 1、获取代码中所有使用@HfBaseUrl注解修饰的类
        Set<? extends Element> annotatedElements = roundEnvironment.getElementsAnnotatedWith(HfLogInterceptor.class);
        for (Element element : annotatedElements) {
            if(element.getKind() == ElementKind.INTERFACE){
                //如果是在接口上
                //必须是最外层接口
                if(element.getEnclosingElement().getKind() != ElementKind.PACKAGE){
                    throw new ProcessingException(element,  "%s必须是最外层接口",
                            ((TypeElement)element).getQualifiedName().toString());
                }

                String superName = element.getSimpleName().toString();
                HfApiInterface apiInterface = targetMap.get(superName);
                if(apiInterface!=null){
                    TypeMirror tm = null;
                    try {
                        element.getAnnotation(HfLogInterceptor.class).value();
                    }catch (MirroredTypeException e){
                        tm = e.getTypeMirror();
                    }
                    if(tm!=null){
                        TypeElement typeElement = (TypeElement) typeUtils.asElement(tm);
                        checkOnlyVoidConstruct(typeElement);
                        if(!typeElement.getInterfaces().contains(elementInterceptor)){
                            throw new ProcessingException(element,  "%s必须是实现了okhttp3.Interceptor方法",
                                    typeElement.getQualifiedName().toString());
                        }
                        apiInterface.setLogInterceptor(typeElement);
                    }

                }
            }else if(element.getKind() == ElementKind.METHOD){
                //如果是在方法上
                //必须是接口下的方法
                if(element.getEnclosingElement().getKind() != ElementKind.INTERFACE){
                    throw new ProcessingException(element,  "%s必须是接口下的方法",
                            element.getSimpleName().toString());
                }
                //必须是最外层接口下的直接子方法
                if(element.getEnclosingElement().getEnclosingElement().getKind() != ElementKind.PACKAGE){
                    throw new ProcessingException(element,  "%s必须是最外层接口下的直接子方法",
                            element.getSimpleName().toString());
                }

                String superName = element.getEnclosingElement().getSimpleName().toString();
                HfApiInterface apiInterface = targetMap.get(superName);
                if(apiInterface!=null){
                    Map<String, HfApiMethod> map = apiInterface.getMap();
                    HfApiMethod hfApiMethod = map.get(element.getSimpleName().toString());
                    if(hfApiMethod != null){
                        TypeMirror tm = null;
                        try {
                            element.getAnnotation(HfLogInterceptor.class).value();
                        }catch (MirroredTypeException e){
                            tm = e.getTypeMirror();
                        }
                        if(tm!=null){
                            TypeElement typeElement = (TypeElement) typeUtils.asElement(tm);
                            checkOnlyVoidConstruct(typeElement);
                            if(!typeElement.getInterfaces().contains(elementInterceptor)){
                                throw new ProcessingException(element,  "%s必须是实现了okhttp3.Interceptor方法",
                                        typeElement.getQualifiedName().toString());
                            }
                            hfApiMethod.setLogInterceptor(typeElement);
                        }

                    }

                }
            }

        }
    }

    private void getCookieJar(RoundEnvironment roundEnvironment) throws ProcessingException {
        // 1、获取代码中所有使用@HfBaseUrl注解修饰的类
        Set<? extends Element> annotatedElements = roundEnvironment.getElementsAnnotatedWith(HfCookieJar.class);
        for (Element element : annotatedElements) {
            if(element.getKind() == ElementKind.INTERFACE) {
                //如果是在接口上
                //必须是最外层接口
                if(element.getEnclosingElement().getKind() != ElementKind.PACKAGE){
                    throw new ProcessingException(element,  "%s必须是最外层接口",
                            ((TypeElement)element).getQualifiedName().toString());
                }

                String superName = element.getSimpleName().toString();
                HfApiInterface apiInterface = targetMap.get(superName);
                if(apiInterface!=null){
                    TypeMirror tm = null;
                    try {
                        element.getAnnotation(HfCookieJar.class).value();
                    }catch (MirroredTypeException e){
                        tm = e.getTypeMirror();
                    }
                    if(tm!=null){
                        TypeElement typeElement = (TypeElement) typeUtils.asElement(tm);
                        checkOnlyVoidConstruct(typeElement);
                        TypeMirror tmm = elementsUtils.getTypeElement("okhttp3.CookieJar").asType();
                        if(!typeElement.getInterfaces().contains(tmm)){
                            throw new ProcessingException(element,  "%s必须是实现了okhttp3.CookieJar",
                                    typeElement.getQualifiedName().toString());
                        }
                        apiInterface.setCookieJar(typeElement);
                    }

                }
            }else if(element.getKind() == ElementKind.METHOD) {
                //如果是在方法上
                //必须是接口下的方法
                if(element.getEnclosingElement().getKind() != ElementKind.INTERFACE){
                    throw new ProcessingException(element,  "%s必须是接口下的方法",
                            element.getSimpleName().toString());
                }
                //必须是最外层接口下的直接子方法
                if(element.getEnclosingElement().getEnclosingElement().getKind() != ElementKind.PACKAGE){
                    throw new ProcessingException(element,  "%s必须是最外层接口下的直接子方法",
                            element.getSimpleName().toString());
                }

                String superName = element.getEnclosingElement().getSimpleName().toString();
                HfApiInterface apiInterface = targetMap.get(superName);
                if(apiInterface!=null){
                    Map<String, HfApiMethod> map = apiInterface.getMap();
                    HfApiMethod hfApiMethod = map.get(element.getSimpleName().toString());
                    if(hfApiMethod != null){
                        TypeMirror tm = null;
                        try {
                            element.getAnnotation(HfCookieJar.class).value();
                        }catch (MirroredTypeException e){
                            tm = e.getTypeMirror();
                        }
                        if(tm!=null){
                            TypeElement typeElement = (TypeElement) typeUtils.asElement(tm);
                            checkOnlyVoidConstruct(typeElement);
                            TypeMirror tmm = elementsUtils.getTypeElement("okhttp3.CookieJar").asType();
                            if(!typeElement.getInterfaces().contains(tmm)){
                                throw new ProcessingException(element,  "%s必须是实现了okhttp3.CookieJar",
                                        typeElement.getQualifiedName().toString());
                            }
                            hfApiMethod.setCookieJar(typeElement);
                        }

                    }

                }
            }

        }
    }

    private void getDns(RoundEnvironment roundEnvironment) throws ProcessingException {
        // 1、获取代码中所有使用@HfBaseUrl注解修饰的类
        Set<? extends Element> annotatedElements = roundEnvironment.getElementsAnnotatedWith(HfDns.class);
        for (Element element : annotatedElements) {
            if(element.getKind() == ElementKind.INTERFACE) {
                //如果是在接口上
                //必须是最外层接口
                if(element.getEnclosingElement().getKind() != ElementKind.PACKAGE){
                    throw new ProcessingException(element,  "%s必须是最外层接口",
                            ((TypeElement)element).getQualifiedName().toString());
                }

                String superName = element.getSimpleName().toString();
                HfApiInterface apiInterface = targetMap.get(superName);
                if(apiInterface!=null){
                    TypeMirror tm = null;
                    try {
                        element.getAnnotation(HfDns.class).value();
                    }catch (MirroredTypeException e){
                        tm = e.getTypeMirror();
                    }
                    if(tm!=null){
                        TypeElement typeElement = (TypeElement) typeUtils.asElement(tm);
                        checkOnlyVoidConstruct(typeElement);
                        TypeMirror tmm = elementsUtils.getTypeElement("okhttp3.Dns").asType();
                        if(!typeElement.getInterfaces().contains(tmm)){
                            throw new ProcessingException(element,  "%s必须是实现了okhttp3.Dns",
                                    typeElement.getQualifiedName().toString());
                        }
                        apiInterface.setDns(typeElement);
                    }

                }
            }else if(element.getKind() == ElementKind.METHOD) {
                //如果是在方法上
                //必须是接口下的方法
                if(element.getEnclosingElement().getKind() != ElementKind.INTERFACE){
                    throw new ProcessingException(element,  "%s必须是接口下的方法",
                            element.getSimpleName().toString());
                }
                //必须是最外层接口下的直接子方法
                if(element.getEnclosingElement().getEnclosingElement().getKind() != ElementKind.PACKAGE){
                    throw new ProcessingException(element,  "%s必须是最外层接口下的直接子方法",
                            element.getSimpleName().toString());
                }

                String superName = element.getEnclosingElement().getSimpleName().toString();
                HfApiInterface apiInterface = targetMap.get(superName);
                if(apiInterface!=null){
                    Map<String, HfApiMethod> map = apiInterface.getMap();
                    HfApiMethod hfApiMethod = map.get(element.getSimpleName().toString());
                    if(hfApiMethod != null){
                        TypeMirror tm = null;
                        try {
                            element.getAnnotation(HfDns.class).value();
                        }catch (MirroredTypeException e){
                            tm = e.getTypeMirror();
                        }
                        if(tm!=null){
                            TypeElement typeElement = (TypeElement) typeUtils.asElement(tm);
                            checkOnlyVoidConstruct(typeElement);
                            TypeMirror tmm = elementsUtils.getTypeElement("okhttp3.Dns").asType();
                            if(!typeElement.getInterfaces().contains(tmm)){
                                throw new ProcessingException(element,  "%s必须是实现了okhttp3.Dns",
                                        typeElement.getQualifiedName().toString());
                            }
                            hfApiMethod.setDns(typeElement);
                        }

                    }

                }
            }

        }
    }

    private void getErrorResume(RoundEnvironment roundEnvironment) throws ProcessingException {
        // 1、获取代码中所有使用@HfBaseUrl注解修饰的类
        Set<? extends Element> annotatedElements = roundEnvironment.getElementsAnnotatedWith(HfErrorResume.class);
        for (Element element : annotatedElements) {
            if(element.getKind() == ElementKind.INTERFACE) {
                //如果是在接口上
                //必须是最外层接口
                if(element.getEnclosingElement().getKind() != ElementKind.PACKAGE){
                    throw new ProcessingException(element,  "%s必须是最外层接口",
                            ((TypeElement)element).getQualifiedName().toString());
                }

                String superName = element.getSimpleName().toString();
                HfApiInterface apiInterface = targetMap.get(superName);
                if(apiInterface!=null){
                    TypeMirror tm = null;
                    try {
                        element.getAnnotation(HfErrorResume.class).value();
                    }catch (MirroredTypeException e){
                        tm = e.getTypeMirror();
                    }
                    if(tm!=null){
                        TypeElement typeElement = (TypeElement) typeUtils.asElement(tm);
                        checkOnlyVoidConstruct(typeElement);
                        boolean isFun = false;
                        for (TypeMirror typeMirror:typeElement.getInterfaces()){
                            TypeElement et = (TypeElement) typeUtils.asElement(typeMirror);
                            if(et.getQualifiedName().toString().equals("io.reactivex.functions.Function")){
                                isFun = true;
                            }
                        }
                        if(!isFun){
                            throw new ProcessingException(element,  "%s必须是实现了io.reactivex.functions.Function方法",
                                    typeElement.getSimpleName().toString());
                        }
                        apiInterface.setErrorResume(typeElement);
                    }

                }
            }else if(element.getKind() == ElementKind.METHOD) {
                //如果是在方法上
                //必须是接口下的方法
                if(element.getEnclosingElement().getKind() != ElementKind.INTERFACE){
                    throw new ProcessingException(element,  "%s必须是接口下的方法",
                            element.getSimpleName().toString());
                }
                //必须是最外层接口下的直接子方法
                if(element.getEnclosingElement().getEnclosingElement().getKind() != ElementKind.PACKAGE){
                    throw new ProcessingException(element,  "%s必须是最外层接口下的直接子方法",
                            element.getSimpleName().toString());
                }

                String superName = element.getEnclosingElement().getSimpleName().toString();
                HfApiInterface apiInterface = targetMap.get(superName);
                if(apiInterface!=null){
                    Map<String, HfApiMethod> map = apiInterface.getMap();
                    HfApiMethod hfApiMethod = map.get(element.getSimpleName().toString());
                    if(hfApiMethod != null){
                        TypeMirror tm = null;
                        try {
                            element.getAnnotation(HfErrorResume.class).value();
                        }catch (MirroredTypeException e){
                            tm = e.getTypeMirror();
                        }
                        if(tm!=null){
                            TypeElement typeElement = (TypeElement) typeUtils.asElement(tm);
                            checkOnlyVoidConstruct(typeElement);
                            boolean isFun = false;
                            for (TypeMirror typeMirror:typeElement.getInterfaces()){
                                TypeElement et = (TypeElement) typeUtils.asElement(typeMirror);
                                if(et.getQualifiedName().toString().equals("io.reactivex.functions.Function")){
                                    isFun = true;
                                }
                            }
                            if(!isFun){
                                throw new ProcessingException(element,  "%s必须是实现了io.reactivex.functions.Function方法",
                                        typeElement.getSimpleName().toString());
                            }
                            hfApiMethod.setErrorResume(typeElement);
                        }

                    }

                }
            }

        }
    }


    private void getNetworkInterceptor(RoundEnvironment roundEnvironment) throws ProcessingException {
        // 1、获取代码中所有使用@HfBaseUrl注解修饰的类
        Set<? extends Element> annotatedElements = roundEnvironment.getElementsAnnotatedWith(HfNetWorkInterceptor.class);
        for (Element element : annotatedElements) {
            if(element.getKind() == ElementKind.INTERFACE) {
                //如果是在接口上
                //必须是最外层接口
                if(element.getEnclosingElement().getKind() != ElementKind.PACKAGE){
                    throw new ProcessingException(element,  "%s必须是最外层接口",
                            element.getSimpleName().toString());
                }
                String superName = element.getSimpleName().toString();
                HfApiInterface apiInterface = targetMap.get(superName);
                if(apiInterface!=null){
                    List<TypeMirror> tms= getClassArrayValueFromAnnotation(element,HfNetWorkInterceptor.class,"value");
                    if(tms!=null && tms.size()>0){
                        for (TypeMirror tm : tms){
                            TypeElement typeElement = (TypeElement) typeUtils.asElement(tm);
                            checkOnlyVoidConstruct(typeElement);
                            if(!typeElement.getInterfaces().contains(elementInterceptor)){
                                throw new ProcessingException(element,  "%s必须是实现了okhttp3.Interceptor方法",
                                        typeElement.getSimpleName().toString());
                            }
                            apiInterface.addNetworkInterceptor(typeElement);
                        }
                    }

                }
            }else if(element.getKind() == ElementKind.METHOD) {
                //必须是接口下的方法
                if(element.getEnclosingElement().getKind() != ElementKind.INTERFACE){
                    throw new ProcessingException(element,  "%s必须是接口下的方法",
                            element.getSimpleName().toString());
                }
                //必须是最外层接口下的直接子方法
                if(element.getEnclosingElement().getEnclosingElement().getKind() != ElementKind.PACKAGE){
                    throw new ProcessingException(element,  "%s必须是最外层接口下的直接子方法",
                            element.getSimpleName().toString());
                }

                String superName = element.getEnclosingElement().getSimpleName().toString();
                HfApiInterface apiInterface = targetMap.get(superName);
                if(apiInterface!=null){
                    Map<String, HfApiMethod> map = apiInterface.getMap();
                    HfApiMethod hfApiMethod = map.get(element.getSimpleName().toString());
                    if(hfApiMethod != null){
                        List<TypeMirror> tms= getClassArrayValueFromAnnotation(element,HfNetWorkInterceptor.class,"value");
                        if(tms!=null && tms.size()>0){
                            for (TypeMirror tm : tms) {
                                TypeElement typeElement = (TypeElement) typeUtils.asElement(tm);
                                checkOnlyVoidConstruct(typeElement);
                                if(!typeElement.getInterfaces().contains(elementInterceptor)){
                                    throw new ProcessingException(element,  "%s必须是实现了okhttp3.Interceptor方法",
                                            typeElement.getSimpleName().toString());
                                }
                                hfApiMethod.addNetworkInterceptor(typeElement);
                            }
                        }
                    }
                }
            }
        }
    }

    private void getInterceptor(RoundEnvironment roundEnvironment) throws ProcessingException {
        // 1、获取代码中所有使用@HfBaseUrl注解修饰的类
        Set<? extends Element> annotatedElements = roundEnvironment.getElementsAnnotatedWith(HfInterceptor.class);
        for (Element element : annotatedElements) {
            if(element.getKind() == ElementKind.INTERFACE) {
                //如果是在接口上
                //必须是最外层接口
                if(element.getEnclosingElement().getKind() != ElementKind.PACKAGE){
                    throw new ProcessingException(element,  "%s必须是最外层接口",
                            element.getSimpleName().toString());
                }
                String superName = element.getSimpleName().toString();
                HfApiInterface apiInterface = targetMap.get(superName);
                if(apiInterface!=null){
                    List<TypeMirror> tms= getClassArrayValueFromAnnotation(element,HfInterceptor.class,"value");
                    if(tms!=null && tms.size()>0){
                        for (TypeMirror tm : tms){
                            TypeElement typeElement = (TypeElement) typeUtils.asElement(tm);
                            checkOnlyVoidConstruct(typeElement);
                            if(!typeElement.getInterfaces().contains(elementInterceptor)){
                                throw new ProcessingException(element,  "%s必须是实现了okhttp3.Interceptor方法",
                                        typeElement.getSimpleName().toString());
                            }
                            apiInterface.addInterceptor(typeElement);
                        }
                    }

                }
            }else if(element.getKind() == ElementKind.METHOD) {
                //必须是接口下的方法
                if(element.getEnclosingElement().getKind() != ElementKind.INTERFACE){
                    throw new ProcessingException(element,  "%s必须是接口下的方法",
                            element.getSimpleName().toString());
                }
                //必须是最外层接口下的直接子方法
                if(element.getEnclosingElement().getEnclosingElement().getKind() != ElementKind.PACKAGE){
                    throw new ProcessingException(element,  "%s必须是最外层接口下的直接子方法",
                            element.getSimpleName().toString());
                }

                String superName = element.getEnclosingElement().getSimpleName().toString();
                HfApiInterface apiInterface = targetMap.get(superName);
                if(apiInterface!=null){
                    Map<String, HfApiMethod> map = apiInterface.getMap();
                    HfApiMethod hfApiMethod = map.get(element.getSimpleName().toString());
                    if(hfApiMethod != null){
                        List<TypeMirror> tms= getClassArrayValueFromAnnotation(element,HfInterceptor.class,"value");
                        if(tms!=null && tms.size()>0){
                            for (TypeMirror tm : tms) {
                                TypeElement typeElement = (TypeElement) typeUtils.asElement(tm);
                                checkOnlyVoidConstruct(typeElement);
                                if(!typeElement.getInterfaces().contains(elementInterceptor)){
                                    throw new ProcessingException(element,  "%s必须是实现了okhttp3.Interceptor方法",
                                            typeElement.getSimpleName().toString());
                                }
                                hfApiMethod.addInterceptor(typeElement);
                            }
                        }
                    }
                }
            }
        }
    }

    private void getFlatMap(RoundEnvironment roundEnvironment) throws ProcessingException {
        // 1、获取代码中所有使用@HfBaseUrl注解修饰的类
        Set<? extends Element> annotatedElements = roundEnvironment.getElementsAnnotatedWith(HfFlatMap.class);
        for (Element element : annotatedElements) {
            if(element.getKind() == ElementKind.INTERFACE) {
                //如果是在接口上
                //必须是最外层接口
                if(element.getEnclosingElement().getKind() != ElementKind.PACKAGE){
                    throw new ProcessingException(element,  "%s必须是最外层接口",
                            element.getSimpleName().toString());
                }
                String superName = element.getSimpleName().toString();
                HfApiInterface apiInterface = targetMap.get(superName);
                if(apiInterface!=null){
                    List<TypeMirror> tms= getClassArrayValueFromAnnotation(element,HfFlatMap.class,"value");
                    if(tms!=null && tms.size()>0){
                        for (TypeMirror tm : tms){
                            TypeElement typeElement = (TypeElement) typeUtils.asElement(tm);
                            checkOnlyVoidConstruct(typeElement);
                            boolean isFun = false;
                            for (TypeMirror typeMirror:typeElement.getInterfaces()){
                                TypeElement et = (TypeElement) typeUtils.asElement(typeMirror);
                                if(et.getQualifiedName().toString().equals("io.reactivex.functions.Function")){
                                    isFun = true;
                                }
                            }
                            if(!isFun){
                                throw new ProcessingException(element,  "%s必须是实现了io.reactivex.functions.Function方法",
                                        typeElement.getSimpleName().toString());
                            }
                            apiInterface.addFlatMap(typeElement);
                        }
                    }

                }
            }else if(element.getKind() == ElementKind.METHOD) {
                //必须是接口下的方法
                if(element.getEnclosingElement().getKind() != ElementKind.INTERFACE){
                    throw new ProcessingException(element,  "%s必须是接口下的方法",
                            element.getSimpleName().toString());
                }
                //必须是最外层接口下的直接子方法
                if(element.getEnclosingElement().getEnclosingElement().getKind() != ElementKind.PACKAGE){
                    throw new ProcessingException(element,  "%s必须是最外层接口下的直接子方法",
                            element.getSimpleName().toString());
                }

                String superName = element.getEnclosingElement().getSimpleName().toString();
                HfApiInterface apiInterface = targetMap.get(superName);
                if(apiInterface!=null){
                    Map<String, HfApiMethod> map = apiInterface.getMap();
                    HfApiMethod hfApiMethod = map.get(element.getSimpleName().toString());
                    if(hfApiMethod != null){
                        List<TypeMirror> tms= getClassArrayValueFromAnnotation(element,HfFlatMap.class,"value");
                        if(tms!=null && tms.size()>0){
                            for (TypeMirror tm : tms) {
                                TypeElement typeElement = (TypeElement) typeUtils.asElement(tm);
                                checkOnlyVoidConstruct(typeElement);
                                boolean isFun = false;
                                for (TypeMirror typeMirror:typeElement.getInterfaces()){
                                    TypeElement et = (TypeElement) typeUtils.asElement(typeMirror);
                                    if(et.getQualifiedName().toString().equals("io.reactivex.functions.Function")){
                                        isFun = true;
                                    }
                                }
                                if(!isFun){
                                    throw new ProcessingException(element,  "%s必须是实现了io.reactivex.functions.Function方法",
                                            typeElement.getSimpleName().toString());
                                }
                                hfApiMethod.addFlatMap(typeElement);
                            }
                        }
                    }
                }
            }
        }
    }

    public List<TypeMirror> getClassArrayValueFromAnnotation(Element element, Class<? extends Annotation> annotation, String paramName) {
        List<TypeMirror> values = new ArrayList<>();
        for (AnnotationMirror am : element.getAnnotationMirrors()) {
            if (typeUtils.isSameType(am.getAnnotationType(), elementsUtils.getTypeElement(annotation.getCanonicalName()).asType())) {
                for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry : am.getElementValues().entrySet()) {
                    if (paramName.equals(entry.getKey().getSimpleName().toString())) {
                        List<AnnotationValue> classesTypes = (List<AnnotationValue>) entry.getValue().getValue();
                        Iterator<? extends AnnotationValue> iterator = classesTypes.iterator();
                        while (iterator.hasNext()) {
                            AnnotationValue next = iterator.next();
                            values.add((TypeMirror) next.getValue());
                        }
                    }
                }
            }
        }
        return values;
    }

    private void checkOnlyVoidConstruct(TypeElement typeElement) throws ProcessingException {
        List<? extends Element> l = typeElement.getEnclosedElements();
        int count = 0;
        for (Element e : l){
            ElementKind K = e.getKind();
            if(e.getKind() == ElementKind.CONSTRUCTOR){
                count++;
                if(((ExecutableElement)e).getParameters().size() > 0){
                    throw new ProcessingException(typeElement,  "%s必须是空构造函数",
                            typeElement.getQualifiedName().toString());
                }
            }
        }
        if(count>1){
            throw new ProcessingException(typeElement,  "%s必须只有一个构造方法",
                    typeElement.getQualifiedName().toString());
        }
    }


    /**
     * 创建Java文件
     * @param entries
     */
    private void createJavaFile(Set<Map.Entry<String, HfApiInterface>> entries) {
        for (Map.Entry<String, HfApiInterface> entry : entries) {

            HfApiInterface apiInterface = entry.getValue();
            if(apiInterface.getMap().size() == 0)continue;

            // 获取包名
            String packageName = elementsUtils.getPackageOf(apiInterface.getTypeElement()).getQualifiedName().toString();
            // 根据旧Java类名创建新的Java文件
            String className = apiInterface.getTypeElement().getQualifiedName().toString().substring(packageName.length() + 1);
            String newClassName = className + "_HfClient";

            TypeSpec.Builder typeBuilder = TypeSpec.classBuilder(newClassName)
                    .addModifiers(Modifier.PUBLIC, Modifier.FINAL);

            Set<Map.Entry<String, HfApiMethod>> set = apiInterface.getMap().entrySet();
            for(Map.Entry<String, HfApiMethod> e :set){
                HfApiMethod method = e.getValue();
                MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder(method.getElement().getSimpleName().toString())
                        .addModifiers(Modifier.PUBLIC,Modifier.STATIC);
                StringBuilder parms = new StringBuilder();
                for(VariableElement v : method.getElement().getParameters()){
                    methodBuilder.addParameter(ParameterSpec.get(v));
                    parms.append(v.getSimpleName());
                    parms.append(",");
                }
                if(parms.length()>0){
                    parms.deleteCharAt(parms.length()-1);
                }
                methodBuilder.returns(ClassName.get(method.getElement().getReturnType()));

                methodBuilder.addCode("return $T.Builder.create()",hfHttpClient);
                //增加baseUrl
                if(!apiInterface.getBaseUrl().equals("")){
                    methodBuilder.addCode("\n.setBaseUrl($S)",apiInterface.getBaseUrl());
                }else{
                    methodBuilder.addCode("\n.setBaseUrl(new $T().getUrl())",apiInterface.getBaseUrlElement().asType());
                }

                //增加TimeOut
                methodBuilder.addCode("\n.setTimeOut($L,$L)",
                        method.getConnectTimeOut(),method.getReadTimeOut());
                //增加logInterceptor相关
                if(method.getLogInterceptor()!=null){
                    methodBuilder.addCode("\n.setLogInterceptor(new $T())",method.getLogInterceptor().asType());
                }else if(apiInterface.getLogInterceptor()!=null){
                    methodBuilder.addCode("\n.setLogInterceptor(new $T())",apiInterface.getLogInterceptor().asType());
                }
                //增加cookiejar相关
                if(method.getCookieJar()!=null){
                    methodBuilder.addCode("\n.setCookieJar(new $T())",method.getCookieJar().asType());
                }else if(apiInterface.getCookieJar()!=null){
                    methodBuilder.addCode("\n.setCookieJar(new $T())",apiInterface.getCookieJar().asType());
                }
                //增加dns相关
                if(method.getDns()!=null){
                    methodBuilder.addCode("\n.setDns(new $T())",method.getDns().asType());
                }else if(apiInterface.getDns()!=null){
                    methodBuilder.addCode("\n.setDns(new $T())",apiInterface.getDns().asType());
                }
                //增加networkInterceptor相关
                if(method.getNetworkInterceptor().size() > 0){
                    for (TypeElement nTm : method.getNetworkInterceptor()){
                        methodBuilder.addCode("\n.addNetWorkInterceptor(new $T())",nTm.asType());
                    }
                }else if(apiInterface.getNetworkInterceptor().size() > 0){
                    for (TypeElement nTm : apiInterface.getNetworkInterceptor()){
                        methodBuilder.addCode("\n.addNetWorkInterceptor(new $T())",nTm.asType());
                    }
                }
                //增加networkInterceptor相关
                if(method.getInterceptors().size() > 0){
                    for (TypeElement nTm : method.getInterceptors()){
                        methodBuilder.addCode("\n.addInterceptor(new $T())",nTm.asType());
                    }
                }else if(apiInterface.getInterceptors().size() > 0){
                    for (TypeElement nTm : apiInterface.getInterceptors()){
                        methodBuilder.addCode("\n.addInterceptor(new $T())",nTm.asType());
                    }
                }
                methodBuilder.addCode("\n.build()");
                methodBuilder.addCode("\n.create($L.class)",apiInterface.getTypeElement().getSimpleName());
                methodBuilder.addCode("\n.$L($L)",method.getElement().getSimpleName(),parms.toString());
                //增加flatmap相关
                if(method.getFlatMaps().size() > 0){
                    for (TypeElement nTm : method.getFlatMaps()){
                        methodBuilder.addCode("\n.flatMap(new $T())",ClassName.bestGuess(nTm.getQualifiedName().toString()));
                    }
                }else if(apiInterface.getFlatMaps().size() > 0){
                    for (TypeElement nTm : apiInterface.getFlatMaps()){
                        methodBuilder.addCode("\n.flatMap(new $T())",ClassName.bestGuess(nTm.getQualifiedName().toString()));
                    }
                }
                //增加errorresume相关
                if(method.getErrorResume()!=null){
                    methodBuilder.addCode("\n.onErrorResumeNext(new $T())",ClassName.bestGuess(method.getErrorResume().getQualifiedName().toString()));
                }else if(apiInterface.getErrorResume()!=null){
                    methodBuilder.addCode("\n.onErrorResumeNext(new $T())",ClassName.bestGuess(apiInterface.getErrorResume().getQualifiedName().toString()));
                }
                methodBuilder.addCode("\n.subscribeOn($T.io())",schedulers);
                methodBuilder.addCode("\n.observeOn($T.mainThread());",androidSchedulers);

                typeBuilder.addMethod(methodBuilder.build());
            }

            JavaFile javaFile = JavaFile.builder(packageName, typeBuilder.build())
                    .addFileComment("Generated code from Butter Knife. Do not modify!")
                    .build();
            try {
                javaFile.writeTo(filer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        targetMap.clear();
    }
}