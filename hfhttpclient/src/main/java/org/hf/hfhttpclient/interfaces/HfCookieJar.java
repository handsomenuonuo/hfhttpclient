package org.hf.hfhttpclient.interfaces;


import java.util.List;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;

/**********************************
 * @Name: HfCookieJar
 * @Copyright： CreYond
 * @CreateDate： 2021/12/10 9:25
 * @author: HuangFeng
 * @Version： 1.0
 * @Describe:
 *
 **********************************/
public class HfCookieJar implements CookieJar {

    @Override
    public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {

    }

    @Override
    public List<Cookie> loadForRequest(HttpUrl url) {
        return null;
    }
}
