package com.application.zimplyshop.managers;

import android.content.Context;

import com.application.zimplyshop.R;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnPerRoute;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;

public class HttpManager {
	private static  DefaultHttpClient sClient;

	private static KeyStore keyStore;


	public static void setParams(Context context,char[] password) throws Exception{
		HttpParams params = new BasicHttpParams();
		HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
		HttpProtocolParams.setContentCharset(params, "UTF-8");
		HttpProtocolParams.setUseExpectContinue(params, true);
		HttpProtocolParams.setUserAgent(params, "androidv4");

		// Make pool
		ConnPerRoute connPerRoute = new ConnPerRouteBean(12);
		ConnManagerParams.setMaxConnectionsPerRoute(params, connPerRoute);
		ConnManagerParams.setMaxTotalConnections(params, 20);

		// Set timeout
		HttpConnectionParams.setStaleCheckingEnabled(params, false);
		HttpConnectionParams.setConnectionTimeout(params, 30 * 1000);
		HttpConnectionParams.setSoTimeout(params, 120 * 1000);
		HttpConnectionParams.setSocketBufferSize(params, 8192);

		// Some client params
		HttpClientParams.setRedirecting(params, true);

		// Register http/s shemas!

		InputStream inputStream = context.getResources().openRawResource(R.raw.keystore);
		KeyStore keyStore = KeyStore.getInstance("BKS");
		keyStore.load(inputStream, password);
		SchemeRegistry schReg = new SchemeRegistry();
		schReg.register(new Scheme("http", PlainSocketFactory
				.getSocketFactory(), 80));

		schReg.register(new Scheme("https", new SSLSocketFactory(keyStore), 443));

		ClientConnectionManager conMgr = new ThreadSafeClientConnManager(
				params, schReg);
		sClient = new DefaultHttpClient(conMgr, params);

	}

	/*static {

		// Set basic data
		HttpParams params = new BasicHttpParams();
		HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
		HttpProtocolParams.setContentCharset(params, "UTF-8");
		HttpProtocolParams.setUseExpectContinue(params, true);
		HttpProtocolParams.setUserAgent(params, "androidv4");

		// Make pool
		ConnPerRoute connPerRoute = new ConnPerRouteBean(12);
		ConnManagerParams.setMaxConnectionsPerRoute(params, connPerRoute);
		ConnManagerParams.setMaxTotalConnections(params, 20);

		// Set timeout
		HttpConnectionParams.setStaleCheckingEnabled(params, false);
		HttpConnectionParams.setConnectionTimeout(params, 30 * 1000);
		HttpConnectionParams.setSoTimeout(params, 120 * 1000);
		HttpConnectionParams.setSocketBufferSize(params, 8192);

		// Some client params
		HttpClientParams.setRedirecting(params, true);

		// Register http/s shemas!


		SchemeRegistry schReg = new SchemeRegistry();
		schReg.register(new Scheme("http", PlainSocketFactory
				.getSocketFactory(), 80));
		schReg.register(new Scheme("https",
				SSLSocketFactory.getSocketFactory(), 443));

		ClientConnectionManager conMgr = new ThreadSafeClientConnManager(
				params, schReg);
		sClient = new DefaultHttpClient(conMgr, params);
	}*/


	public static HttpResponse execute(HttpHead head) throws IOException {
		return sClient.execute(head);
	}

	public static HttpResponse execute(HttpHost host, HttpGet get)
			throws IOException {
		return sClient.execute(host, get);
	}

	public static HttpResponse execute(HttpGet get,Context context) throws IOException {

		// if(CommonLib.isTestBuild)
		// get.addHeader(new BasicHeader("Authorization",
		// "Basic emRldjpvSnU0Rm9oY2hvb20zY2hhPWcmbw==")); //ZDEV new
		// else
		// get.addHeader(new BasicHeader("Accept-Encoding", "gzip"));
		return sClient.execute(get);
	}

	public static HttpResponse execute(HttpPost post) throws IOException {

		// if(CommonLib.isTestBuild)
		// post.addHeader(new BasicHeader("Authorization",
		// "Basic emRldjpvSnU0Rm9oY2hvb20zY2hhPWcmbw==")); //ZDEV new
		// else
		// post.addHeader(new BasicHeader("Accept-Encoding", "gzip"));

		return sClient.execute(post);
	}

	// Dont add Headers Here, Used for third-party calls
	public static HttpResponse simple_execute(HttpPost post) throws IOException {
		return sClient.execute(post);
	}

	public static synchronized CookieStore getCookieStore() {
		return sClient.getCookieStore();
	}

}
