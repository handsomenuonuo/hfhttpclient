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
### 1、示例如下：
```java
@HfInterface(baseUrl = "http://43.192.230.161:6666")
public interface TestService {

    @HfApi()
    @GET("/demo/custody/app")
    Observable<BaseResultRoot<OrderBean>> getOrder(@Query("quipmentNo") String quipmentNo);

}
```
### ***注意：***  
* #### *HfInterface* 注解只能用在 *interface接口* 上，且 *不能* 用于内部interface
* #### *HfApi* 注解只能用在 *HfInterface接口* 下的 *直接抽象方法* ，*不能* 用于嵌套的内部interface下的方法  
    
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
