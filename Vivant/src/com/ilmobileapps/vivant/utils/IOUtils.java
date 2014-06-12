package com.ilmobileapps.vivant.utils;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpProtocolParams;

import android.util.Log;

public class IOUtils
{
	private static HttpClient getHttpClient()
	{
		SchemeRegistry aSchemeRegistry = new SchemeRegistry();
		aSchemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
		aSchemeRegistry.register(new Scheme("https", org.apache.http.conn.ssl.SSLSocketFactory.getSocketFactory(), 443));

		BasicHttpParams aBasicHttpParams = new BasicHttpParams();
		aBasicHttpParams.setIntParameter("http.socket.timeout", 120000);
		aBasicHttpParams.setIntParameter("http.connection.timeout", 120000);
		HttpProtocolParams.setVersion(aBasicHttpParams, HttpVersion.HTTP_1_1);
		return new DefaultHttpClient(new ThreadSafeClientConnManager(aBasicHttpParams, aSchemeRegistry), aBasicHttpParams);
	}

	public static String getContentFromURL(String paramString) throws IOException
	{
		ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
		HttpClient aHttpClient = getHttpClient();

		HttpGet aHttpGet = new HttpGet(paramString);
		aHttpGet.addHeader("Accept-Encoding", "gzip");
		HttpResponse aHttpResponse = aHttpClient.execute(aHttpGet);
		InputStream anInputStream = aHttpResponse.getEntity().getContent();
		Header aHeader = aHttpResponse.getFirstHeader("Content-Encoding");
		if ((aHeader != null) && (aHeader.getValue().equalsIgnoreCase("gzip"))) anInputStream = new GZIPInputStream(anInputStream);
		byte[] arrayOfByte = new byte[8192];
		while (true)
		{
			int i = ((InputStream) anInputStream).read(arrayOfByte);
			if (i == -1) break;
			localByteArrayOutputStream.write(arrayOfByte, 0, i);
		}
		return localByteArrayOutputStream.toString();
	}

	public static String readTextFile(File file) throws IOException
	{
		// This will reference one line at a time
		StringBuffer sb = new StringBuffer();
		try
		{
			// FileReader reads text files in the default encoding.
			FileReader fileReader = new FileReader(file);
			// Always wrap FileReader in BufferedReader.
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			String line = null;
			while ((line = bufferedReader.readLine()) != null)
			{
				sb.append(line);
			}
			// Always close files.
			bufferedReader.close();
		}
		catch (FileNotFoundException e)
		{
			Log.e(IOUtils.class.getSimpleName(), "readTextFile: Unable to open file '" + file.getName() + "'", e);
		}
		catch (IOException e)
		{
			Log.e(IOUtils.class.getSimpleName(), "readTextFile: Error reading file '" + file.getName() + "'", e);
			throw e;
		}
		return sb.toString();
	}

	public static void writeTextFile(File file, float value) throws IOException
	{

		FileOutputStream fos = null;
		try
		{
			fos = new FileOutputStream(file);
			fos.write(Float.toString(value).getBytes());
			fos.flush();
		}
		catch (FileNotFoundException e)
		{
			Log.e(IOUtils.class.getSimpleName(), "writeTextFile: Unable to open file '" + file.getName() + "'", e);
		}
		catch (IOException e)
		{
			Log.e(IOUtils.class.getSimpleName(), "writeTextFile: Error writing file '" + file.getName() + "'", e);
		}
		finally
		{
			if (fos != null) try
			{
				fos.close();
			}
			catch (IOException e)
			{
			}
		}
	}
}
