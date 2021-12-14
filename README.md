# hfhttpclient
----
[![](https://jitpack.io/v/handsomenuonuo/hfhttpclient.svg)](https://jitpack.io/#handsomenuonuo/hfhttpclient)

一个方便开发者的网络请求库，自动生成网络请求代码，使用了[Rxjava]() + [Retrofit]() + [OkHttp]()
----

## 导入方法
### 1、将以下代码加入根目录的build.gradle:  
```gradle
allprojects {
    repositories {
        maven { url "https://jitpack.io" }
    }
}
```
### 2、然后在需要用到的moudle下的build.gradle下加入以下代码：  

```gradle
dependencies {
    annotationProcessor 'com.github.handsomenuonuo.hfhttpclient:compiler:1.0.1'
    implementation 'com.github.handsomenuonuo.hfhttpclient:hfhttpclient:1.0.1'
}

```
### 3、最后不能忘了导入 [Rxjava]()、[Retrofit]()、[OkHttp]() 的library

```gradle
dependencies {
    /**************************rxjava2***********************************/
    implementation 'io.reactivex.rxjava2:rxjava:2.2.14'
    implementation 'io.reactivex.rxjava2:rxandroid:2.1.1'
    /**************************retrofit2***********************************/
    implementation 'com.squareup.retrofit2:retrofit:2.6.0'
    implementation 'com.squareup.retrofit2:converter-gson:2.6.0'
    implementation 'com.squareup.retrofit2:adapter-rxjava2:2.6.0'
    /**************************okhttp3***********************************/
    implementation 'com.squareup.okhttp3:okhttp:3.12.1'
    implementation 'com.squareup.okhttp3:logging-interceptor:3.12.1'
}

```
## 简单使用方法
### HfInterface用于配置请求的baseUrl，最简单的配置为直接设置url的string，其他用法见下面的 [HfInterface的另一种用法](#HfInterface的另一种用法)。
### HfApi用于配置请求的connectTimeout和readTimeout，单位为秒，可以省略，默认为30秒。
### [HfInterface和HfApi是必须的配置]()
### 1、示例如下：
```java
@HfInterface(baseUrl = "http://43.192.230.161:6666")
public interface TestService {

    @HfApi()
    //或@HfApi(connectTimeSec = 40,readTimeSec = 40)
    @GET("/demo/custody/app")
    Observable<BaseResultRoot<OrderBean>> getOrder(@Query("quipmentNo") String quipmentNo);

}
```
### ***注意：***  
* #### [*HfInterface* 注解只能用在 *interface接口* 上，且 *不能* 用于内部interface]()
* #### [*HfApi* 注解只能用在 *HfInterface接口* 下的 *直接抽象方法* ，*不能* 用于嵌套的内部interface下的方法]()  
    
### 2、点击Build -> Rebuild,就会生成网络请求的代码，生成的位置和代码如下：
![image](https://github.com/handsomenuonuo/hfhttpclient/blob/main/1.png)  
![image](https://github.com/handsomenuonuo/hfhttpclient/blob/main/2.png)  

### 3、使用
```java
   TestService_HfClient.getOrder("111").compose(mView.bindUntilEvent(ActivityEvent.DESTROY)).subscribe(new Observer<BaseResultRoot<OrderBean>>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
            }

            @Override
            public void onNext(@NonNull BaseResultRoot<OrderBean> s) {
            }

            @Override
            public void onError(@NonNull Throwable e) {
            }

            @Override
            public void onComplete() {
            }
        });
```
---
## HfInterface的另一种用法，适用于动态改变的baseUrl。
### 注意：
#### baseUrl 和 getUrl 必须配置一个，如果两个都配置，则会按baseUrl来进行配置。
#### getUrl的class必须实现[org.hf.hfhttpclient.interfaces.HfUrl](https://github.com/handsomenuonuo/hfhttpclient/blob/main/hfhttpclient/src/main/java/org/hf/hfhttpclient/interfaces/HfUrl.java)接口，并且 有且只能有一个空构造函数。
示例如下：
```java
@HfInterface(getUrl = MyGetUrl.class)
public interface TestService {

    @HfApi()
    @GET("/demo/custody/app")
    Observable<BaseResultRoot<OrderBean>> getOrder(@Query("quipmentNo") String quipmentNo);

}
```
```java
public class MyGetUrl implements HfUrl {
    @Override
    public String getUrl() {
        return SPUtils.getInstance().getString("BASE_URL");
    }
}
```
---
## 其他配置使用方法
---
### 注意：
#### 1、以下配置都可以用在interface上和interface下的直接抽象方法上，优先级为 [*方法>interface*]() 。即  
##### *如果设置在方法上，则会调用方法上的配置生成代码。*  
##### *如果设置在interface上，则该接口下的未设置该配置的方法，都会调用interface上的配置生成代码。*  
#### 2、所有class的配置，其构造函数，有且只能有一个空构造函数。
---
### @HfLogInterceptor
#### 此配置内部实现跟HfInterceptor一样，因为有内置的log拦截器[HfLoggerInterceptor](https://github.com/handsomenuonuo/hfhttpclient/blob/main/hfhttpclient/src/main/java/org/hf/hfhttpclient/HfLoggerInterceptor.java)，如果需要实现自己的log拦截器来置换内部拦截器，就需要配置此项。必须实现 [okhttp3.Interceptor]() 接口，示例如下：
```java
@HfLogInterceptor(LoggerInterceptor.class)
@HfInterface(baseUrl = "http://43.192.230.161:6666")
public interface TestService {

    @HfLogInterceptor(LoggerInterceptor.class)
    @HfApi()
    @GET("/demo/custody/app")
    Observable<BaseResultRoot<OrderBean>> getOrder(@Query("quipmentNo") String quipmentNo);

}
```
```java
public class LoggerInterceptor implements Interceptor {

    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        return null;
    }
}
```
---
### @HfCookieJar
#### 可以实现请求缓存和添加cookie,必须实现 [okhttp3.CookieJar]() 接口，示例如下：
```java
@HfCookieJar(MyCookieJar.class)
@HfInterface(baseUrl = "http://43.192.230.161:6666")
public interface TestService {

    @HfCookieJar(MyCookieJar.class)
    @HfApi()
    @GET("/demo/custody/app")
    Observable<BaseResultRoot<OrderBean>> getOrder(@Query("quipmentNo") String quipmentNo);

}
```
```java
public class MyCookieJar implements CookieJar {
    @Override
    public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
    }

    @Override
    public List<Cookie> loadForRequest(HttpUrl url) {
        return null;
    }
}
```
---
### @HfDns
#### 可以自行配置DNS，必须实现 [okhttp3.Dns]() 接口，示例如下：
```java
@HfDns(MyDns.class)
@HfInterface(baseUrl = "http://43.192.230.161:6666")
public interface TestService {

    @HfDns(MyDns.class)
    @HfApi()
    @GET("/demo/custody/app")
    Observable<BaseResultRoot<OrderBean>> getOrder(@Query("quipmentNo") String quipmentNo);

}
```
```java
public class MyDns implements Dns {

    @Override
    public List<InetAddress> lookup(String hostname) throws UnknownHostException {
        return null;
    }
}
```
---
### @HfNetWorkInterceptor
#### 可以用来配置Token等NetWorkInterceptor，必须实现 [okhttp3.Interceptor]() 接口，示例如下：
```java
@HfNetWorkInterceptor({TokenInterceptor.class,TokenInterceptor1.class})
@HfInterface(baseUrl = "http://43.192.230.161:6666")
public interface TestService {

    @HfNetWorkInterceptor({TokenInterceptor.class,TokenInterceptor1.class})
    @HfApi()
    @GET("/demo/custody/app")
    Observable<BaseResultRoot<OrderBean>> getOrder(@Query("quipmentNo") String quipmentNo);

}
```
```java
public class TokenInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        return null;
    }
}
```
---
### @HfInterceptor
#### 可以添加拦截器，比如log的拦截器，必须实现 [okhttp3.Interceptor]() 接口，示例如下：
```java
@HfInterceptor({MyInterceptor.class，MyInterceptor1.class})
@HfInterface(baseUrl = "http://43.192.230.161:6666")
public interface TestService {

    @HfInterceptor({MyInterceptor.class，MyInterceptor1.class})
    @HfApi()
    @GET("/demo/custody/app")
    Observable<BaseResultRoot<OrderBean>> getOrder(@Query("quipmentNo") String quipmentNo);

}
```
```java
public class MyInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        return null;
    }
}
```
---
### @HfFlatMap
#### 该配置小心使用，可以添加rxjava2的flapMap，必须实现 [io.reactivex.functions.Function]() 接口，示例如下：
```java
@HfFlatMap({MyFlatMap.class，MyFlatMap1.class})
@HfInterface(baseUrl = "http://43.192.230.161:6666")
public interface TestService {

    @HfFlatMap({MyFlatMap.class，MyFlatMap1.class})
    @HfApi()
    @GET("/demo/custody/app")
    Observable<BaseResultRoot<OrderBean>> getOrder(@Query("quipmentNo") String quipmentNo);

}
```
```java
public class MyFlatMap<T extends BaseMsgRoot> implements Function<T, ObservableSource<T>> {

    @Override
    public ObservableSource<T> apply(@NonNull T t) throws Exception {
        return null;
    }
}
```
#### 也可以这样用：
```java
 TestService_HfClient.getOrder("111")
                .compose(mView.bindUntilEvent(ActivityEvent.DESTROY))
                .flatMap(new MyFlatMap())
                .flatMap(new MyFlatMap1())
                .flatMap(new Function<BaseResultRoot<OrderBean>, ObservableSource<?>>() {
                    @Override
                    public ObservableSource<?> apply(@NonNull BaseResultRoot<OrderBean> orderBeanBaseResultRoot) throws Exception {
                        return 自行实现;
                    }
                })
                .subscribe(new Observer<BaseResultRoot<OrderBean>>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
            }

            @Override
            public void onNext(@NonNull BaseResultRoot<OrderBean> s) {
            }

            @Override
            public void onError(@NonNull Throwable e) {
            }

            @Override
            public void onComplete() {
            }
        });
```
---
### @HfErrorResume
#### 该配置小心使用，可以添加rxjava2的onErrorResumeNext，必须实现 [io.reactivex.functions.Function]() 接口，示例如下：
```java
@HfErrorResume(MyErrorResume.class)
@HfInterface(baseUrl = "http://43.192.230.161:6666")
public interface TestService {

    @HfErrorResume(MyErrorResume.class)
    @HfApi()
    @GET("/demo/custody/app")
    Observable<BaseResultRoot<OrderBean>> getOrder(@Query("quipmentNo") String quipmentNo);

}
```
```java
public class MyErrorResume<T> implements Function<Throwable, Observable<T>> {

    @Override
    public Observable<T> apply(Throwable throwable) {
        return null;
    }
}
```
#### 也可以这样用：
```java
 TestService_HfClient.getOrder("111")
                .compose(mView.bindUntilEvent(ActivityEvent.DESTROY))
                //.onErrorResumeNext(new MyErrorResume())
                .onErrorResumeNext(new Function<Throwable, ObservableSource>() {
                    @Override
                    public ObservableSource apply(@NonNull Throwable throwable) throws Exception {
                        return null;
                    }
                })
                .subscribe(new Observer<BaseResultRoot<OrderBean>>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
            }

            @Override
            public void onNext(@NonNull BaseResultRoot<OrderBean> s) {
            }

            @Override
            public void onError(@NonNull Throwable e) {
            }

            @Override
            public void onComplete() {
            }
        });
```
