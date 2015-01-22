/*
 * This file is part of ForsakenSuite.
 *
 * Copyright © 2012-2014,
 * 									ForsakenNetwork LLC
 * 									<http://www.forsaken.co/>
 * ForsakenSuite is licensed under the Forsaken Network License Version 1
 *
 * ForsakenSuite is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the Forsaken Network License Version 1.
 *
 * ForsakenSuite is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License,
 * the MIT license and the Forsaken Network License Version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License.
 */
package co.forsaken.api.json;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.message.BasicHeader;

import co.forsaken.projectindigo.utils.Callback;

import com.google.gson.Gson;

public class JsonWebCall {

  private String                         _url;
  private PoolingClientConnectionManager _connectionManager;
  private boolean                        _log = false;

  public JsonWebCall(String url) {
    _url = url;
    SchemeRegistry schemeRegistry = new SchemeRegistry();
    schemeRegistry.register(new Scheme("http", 80, PlainSocketFactory.getSocketFactory()));

    _connectionManager = new PoolingClientConnectionManager(schemeRegistry);
    _connectionManager.setMaxTotal(200);
    _connectionManager.setDefaultMaxPerRoute(20);
  }

  public String executeRet(Object arg) {
    if (_log) System.out.println("Requested: [" + _url + "]");
    HttpClient httpClient = new DefaultHttpClient(_connectionManager);
    InputStream in = null;
    String res = null;
    try {
      Gson gson = new Gson();
      HttpPost request = new HttpPost(_url);
      if (arg != null) {
        StringEntity params = new StringEntity(gson.toJson(arg));
        params.setContentType(new BasicHeader("Content-Type", "application/json"));
        request.setEntity(params);
      }
      HttpResponse response = httpClient.execute(request);
      if (response != null) {
        in = response.getEntity().getContent();
        res = convertStreamToString(in);
      }
    } catch (Exception ex) {
      System.out.println("JSONWebCall.execute() Error: \n" + ex.getMessage());
      System.out.println("Result: \n" + res);
      StackTraceElement[] arrOfSTE;
      int max = (arrOfSTE = ex.getStackTrace()).length;
      for (int i = 0; i < max; i++) {
        StackTraceElement trace = arrOfSTE[i];
        System.out.println(trace);
      }
    } finally {
      httpClient.getConnectionManager().shutdown();
      if (in != null) {
        try {
          in.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
    if (_log) System.out.println("Returned: [" + _url + "] [" + res + "]");
    return res;
  }

  public String executeGet() {
    if (_log) System.out.println("Requested: [" + _url + "]");
    HttpClient httpClient = new DefaultHttpClient(_connectionManager);
    InputStream in = null;
    String res = null;
    try {
      HttpGet request = new HttpGet(_url);
      request.setHeader("Content-Type", "application/json");
      HttpResponse response = httpClient.execute(request);
      if (response != null) {
        in = response.getEntity().getContent();
        res = convertStreamToString(in);
      }
    } catch (Exception ex) {
      System.out.println("JSONWebCall.execute() Error: \n" + ex.getMessage());
      System.out.println("Result: \n" + res);
      StackTraceElement[] arrOfSTE;
      int max = (arrOfSTE = ex.getStackTrace()).length;
      for (int i = 0; i < max; i++) {
        StackTraceElement trace = arrOfSTE[i];
        System.out.println(trace);
      }
    } finally {
      httpClient.getConnectionManager().shutdown();
      if (in != null) {
        try {
          in.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
    if (_log) System.out.println("Returned: [" + _url + "] [" + res + "]");
    return res;
  }

  public void execute(Object arg) {
    if (_log) System.out.println("Requested: [" + _url + "]");
    HttpClient httpClient = new DefaultHttpClient(_connectionManager);
    try {
      Gson gson = new Gson();
      HttpPost request = new HttpPost(_url);
      if (arg != null) {
        StringEntity params = new StringEntity(gson.toJson(arg));
        params.setContentType(new BasicHeader("Content-Type", "application/json"));
        request.setEntity(params);
      }
      httpClient.execute(request);
    } catch (Exception ex) {
      System.out.println("JSONWebCall.executeNoRet() Error: \n" + ex.getMessage());
      StackTraceElement[] arrOfSTE;
      int max = (arrOfSTE = ex.getStackTrace()).length;
      for (int i = 0; i < max; i++) {
        StackTraceElement trace = arrOfSTE[i];
        System.out.println(trace);
      }
    } finally {
      httpClient.getConnectionManager().shutdown();
    }
    if (_log) System.out.println("Returned: [" + _url + "]");
  }

  public <T> T execute(Class<T> retType, Object arg) {
    if (_log) System.out.println("Requested: [" + _url + "]");
    HttpClient httpClient = new DefaultHttpClient(_connectionManager);
    InputStream in = null;
    T returnData = null;
    String res = null;

    try {
      Gson gson = new Gson();
      HttpPost request = new HttpPost(_url);
      if (arg != null) {
        StringEntity params = new StringEntity(gson.toJson(arg));
        params.setContentType(new BasicHeader("Content-Type", "application/json"));
        request.setEntity(params);
      }
      HttpResponse response = httpClient.execute(request);
      if (response != null) {
        in = response.getEntity().getContent();
        res = convertStreamToString(in);
        returnData = new Gson().fromJson(res, retType);
      }
    } catch (Exception ex) {
      System.out.println("JSONWebCall.execute() Error: \n" + ex.getMessage());
      System.out.println("Result: \n" + res);
      StackTraceElement[] arrOfSTE;
      int max = (arrOfSTE = ex.getStackTrace()).length;
      for (int i = 0; i < max; i++) {
        StackTraceElement trace = arrOfSTE[i];
        System.out.println(trace);
      }
    } finally {
      httpClient.getConnectionManager().shutdown();
      if (in != null) {
        try {
          in.close();
        } catch (IOException ex) {
          ex.printStackTrace();
        }
      }
    }
    if (_log) System.out.println("Returned: [" + _url + "] [" + res + "]");
    return returnData;
  }

  public <T> T executeGet(Class<T> retType, boolean encapsulate) throws Exception {
    if (_log) System.out.println("Requested: [" + _url + "]");
    try {
      HttpURLConnection.setFollowRedirects(false);
      HttpURLConnection con = (HttpURLConnection) new URL(_url).openConnection();
      con.setRequestMethod("HEAD");

      con.setConnectTimeout(2000);

      if (con.getResponseCode() != HttpURLConnection.HTTP_OK) { throw new Exception("Service " + _url + " unavailable, oh no!"); }
    } catch (java.net.SocketTimeoutException e) {
      throw new Exception("Service " + _url + " unavailable, oh no!", e);
    } catch (java.io.IOException e) {
      throw new Exception("Service " + _url + " unavailable, oh no!", e);
    }

    HttpClient httpClient = new DefaultHttpClient(_connectionManager);
    InputStream in = null;
    T returnData = null;
    String res = null;

    try {
      HttpGet request = new HttpGet(_url);
      request.setHeader("Content-Type", "application/json");
      HttpResponse response = httpClient.execute(request);
      if (response != null) {
        in = response.getEntity().getContent();
        res = convertStreamToString(in);
        if (encapsulate) {
          res = "{\"data\":" + res + "}";
        }
        returnData = new Gson().fromJson(res, retType);
      }
    } catch (Exception ex) {
      throw ex;
    } finally {
      httpClient.getConnectionManager().shutdown();
      if (in != null) {
        try {
          in.close();
        } catch (IOException ex) {
          ex.printStackTrace();
        }
      }
    }
    if (_log) System.out.println("Returned: [" + _url + "] [" + res + "]");
    return returnData;
  }

  public <T> void execute(Class<T> callbackClass, Callback<T> callback) {
    execute(callbackClass, callback, null);
  }

  public <T> void execute(Class<T> callbackClass, Callback<T> callback, Object arg) {
    if (_log) System.out.println("Requested: [" + _url + "]");
    HttpClient httpClient = new DefaultHttpClient(_connectionManager);
    InputStream in = null;
    String res = null;

    try {
      Gson gson = new Gson();
      HttpPost request = new HttpPost(_url);
      if (arg != null) {
        StringEntity params = new StringEntity(gson.toJson(arg));
        params.setContentType(new BasicHeader("Content-Type", "application/json"));
        request.setEntity(params);
      }
      HttpResponse response = httpClient.execute(request);
      if (response != null) {
        in = response.getEntity().getContent();

        res = convertStreamToString(in);
        if (_log) System.out.println("Returned: [" + _url + "] [" + res + "]");
        callback.run(new Gson().fromJson(res, callbackClass));
      }
    } catch (Exception ex) {
      ex.printStackTrace();
      System.out.println("JSONWebCall.execute() Error: \n" + ex.getMessage());
      System.out.println("Result: \n" + res);
      StackTraceElement[] arrOfSTE;
      int max = (arrOfSTE = ex.getStackTrace()).length;
      for (int i = 0; i < max; i++) {
        StackTraceElement trace = arrOfSTE[i];
        System.out.println(trace);
      }
    } finally {
      httpClient.getConnectionManager().shutdown();
      if (in != null) {
        try {
          in.close();
        } catch (IOException ex) {
          ex.printStackTrace();
        }
      }
    }
  }

  protected String convertStreamToString(InputStream is) {
    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
    StringBuilder sb = new StringBuilder();

    String line = null;
    try {
      while ((line = reader.readLine()) != null) {
        sb.append(line + "\n");
      }
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      try {
        is.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    return sb.toString();
  }
}
