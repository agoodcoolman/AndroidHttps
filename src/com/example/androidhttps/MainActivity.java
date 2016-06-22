package com.example.androidhttps;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.CertificateFactory;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.OkHttpClient.Builder;
import android.support.v7.app.ActionBarActivity;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class MainActivity extends ActionBarActivity {
	private OkHttpClient mHttpclient;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		init();
		Button button = (Button) findViewById(R.id.button);
		

		
		button.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Request request = new Request.Builder().url("https://192.168.1.12:8443/Auth2/index.jsp").build();
				final Call newCall = mHttpclient.newCall(request);
				newCall.enqueue(new Callback() {
					
					@Override
					public void onResponse(Call arg0, Response arg1) throws IOException {
						System.out.println("成功"+arg1.toString());
						
						
					}
					
					@Override
					public void onFailure(Call arg0, IOException arg1) {
						System.out.println("失败"+arg1.toString());
						
					}
				});
				
				
			}
		});
	}

	@SuppressLint("NewApi")
	public void init(){
		// 添加受信任的证书
		
		Builder builder = new OkHttpClient.Builder();
		
		try {
			// 获取到证书,
			InputStream inputStream = getAssets().open("server.cer");
			SSLParameters sslParameters = new SSLParameters();
			
			CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
			KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
			keyStore.load(null);
			
			int index = 0;
			
			keyStore.setCertificateEntry("deao_server", certificateFactory.generateCertificate(inputStream));
			
			TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
			trustManagerFactory.init(keyStore);
			TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();
			SSLContext sslContext = SSLContext.getInstance("TLS");
			
			// 这两行代码随便写的.要在后面双向认证中更改
			KeyStore phoneKeystore = KeyStore.getInstance(KeyStore.getDefaultType());
			phoneKeystore.load(getAssets().open("phone.bks"), "123456".toCharArray());
			KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
			keyManagerFactory.init(phoneKeystore, "123456".toCharArray());
			KeyManager[] keyManagers = keyManagerFactory.getKeyManagers(); 
			
			
			sslContext.init(keyManagers, trustManagers, new SecureRandom());
			NoSSLv3SocketFactory noSSLv3SocketFactory = new NoSSLv3SocketFactory(sslContext.getSocketFactory());
			builder.sslSocketFactory(noSSLv3SocketFactory);
			
			mHttpclient = builder.build();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
				
	}

	
}
