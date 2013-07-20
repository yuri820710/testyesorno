package com.greenrobot.testyesorno;


import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.List;
import java.util.zip.GZIPInputStream;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;

import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;


import android.util.Log;
public class HTTPHelper {


	public static final int GET = 0;

	public static final int POST = 1;

	public static final String RESPONSE_KEY = "response";

	public static final String STATUS_CODE_KEY = "statusCode";


	public static final boolean USE_STAGING_SERVER = false;

	public static final String TAG = "YesOrNo.HTTPHelper";
	public static final boolean DEBUG = false;


	public static String doRequest(final String url, final int requestType,
			final List<NameValuePair> nameValuePairs, final Header[] headers, final String body) {
		//Log.d("HTTPHelper", "requesting URL: " + url);
		HttpRequestBase httpRequest = null;
		StringBuffer buffer = new StringBuffer();
		if (POST == requestType) {
			httpRequest = new HttpPost(url + "?device=android");
			try {
				if (nameValuePairs != null) {
					((HttpPost) httpRequest).setEntity(new UrlEncodedFormEntity(nameValuePairs));
				}
				if (!isNullOrEmpty(body)) {

					((HttpPost) httpRequest).setEntity(new ByteArrayEntity(  
							body.getBytes("UTF8")));  
					
				}
			} catch (UnsupportedEncodingException usee) {
				Log.e("HttpService", "Could not encode the Entities.", usee);
			}
		} else {
			httpRequest = new HttpGet(url);
		}
		try {
			if (headers != null) {
				httpRequest.setHeaders(headers);
			}

			HttpClient client = getHttpClient();
			HttpResponse httpResponse = client.execute(httpRequest);
			InputStream inputStream = null;
			Header contentEncoding = httpResponse.getFirstHeader("Content-Encoding");

			//Log.v("httpResponse status line", httpResponse.getStatusLine().toString());

			inputStream = httpResponse.getEntity().getContent();

			if (contentEncoding != null && contentEncoding.getValue().equalsIgnoreCase("gzip")) {
				inputStream = new GZIPInputStream(inputStream);
			}

			int contentLength = (int) httpResponse.getEntity().getContentLength();
			if (contentLength < 0) {
				contentLength = 256;
			}
			byte[] data = new byte[contentLength];
			int len = 0;
			while (-1 != (len = inputStream.read(data))) {
				buffer.append(new String(data, 0, len));
			}
			inputStream.close();
		} catch (ClientProtocolException cpe) {
			Log.e("HttpService", "Http protocol error occured.", cpe);
		} catch (IllegalStateException ise) {
			Log.e("HttpService", "Could not get a HTTP response from the server.", ise);
		} catch (UnknownHostException e) {
			Log.e("HttpService", e.getMessage());
		} catch (IOException ioe) {
			Log.e("HttpService",
					"Could not establish a HTTP connection to the server or could not get a response properly from the server.",
					ioe);
		}
		if (DEBUG) Log.v("response buffer", buffer.toString());
		return buffer.toString();
	}




	public static boolean isNullOrEmpty(final String string) {
		if (string == null) {
			return true;
		} else if (string.length() == 0) {
			return true;
		} else if (string.compareTo("") == 0) {
			return true;
		} else {
			return false;
		}
	}


	public static int doRequest(final String url, final int requestType, final Header[] headers,
			final String body) {
		HttpClient client = getHttpClient();
		HttpPost request = new HttpPost(url);
		try {
			if (headers != null) {
				request.setHeaders(headers);
			}
			try {
				if (!isNullOrEmpty(body)) {
					request.setEntity(new StringEntity(body));
				}
			} catch (UnsupportedEncodingException usee) {
				Log.e("HttpService", "Could not encode the Entities.", usee);
			}

			HttpResponse response = client.execute(request);
			StringBuffer buffer = new StringBuffer();
			InputStream inputStream = response.getEntity().getContent();
			int contentLength = (int) response.getEntity().getContentLength();
			if (contentLength < 0) {
				contentLength = 256;
			}
			byte[] data = new byte[contentLength];
			int len = 0;
			while (-1 != (len = inputStream.read(data))) {
				buffer.append(new String(data, 0, len));
			}
			HashMap<String, String> hash = new HashMap<String, String>();
			hash.put(RESPONSE_KEY, buffer.toString());
			hash.put(STATUS_CODE_KEY, response.getStatusLine().getStatusCode() + "");
			return response.getStatusLine().getStatusCode();
		} catch (ClientProtocolException e) {
			Log.e(Server.TAG, "doRequest() - ClientProtocolException: " + e.getMessage());
		} catch (IOException e) {
			Log.e(Server.TAG, "doRequest() - IOException: " + e.getMessage());
		} catch (Exception e) {
			Log.e(Server.TAG, "doRequest() - Exception: " + e.getMessage());
		}
		return HttpStatus.SC_NOT_FOUND;
	}

	public static HttpClient getHttpClient() {

        return new DefaultHttpClient();
	}
}
