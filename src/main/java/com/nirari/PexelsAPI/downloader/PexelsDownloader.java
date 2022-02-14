package com.nirari.PexelsAPI.downloader;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nirari.PexelsAPI.json.PhotoResponseDeserializer;
import com.nirari.PexelsAPI.json.PhotosEndpointResponse;
import com.nirari.PexelsAPI.util.StatefulURIManager;
import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;
import java.lang.reflect.Array;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;

public abstract class PexelsDownloader<T> {
	protected final String ROOT_URL;
	
	protected final int maxPages;
	protected final int maxResultsPerPage;
	
	private final String authToken;
	
	private int latestQuota;
	private int latestRemaining;
	private long latestResetTime;
	
	private final Class<T> type;
	
	
	private final HttpClient httpClient = HttpClients.custom()
														.setDefaultRequestConfig(RequestConfig.custom()
														.setCookieSpec(CookieSpecs.STANDARD).build())
														.build();
	
	private final Gson gson;
	
	protected PexelsDownloader(String authToken, int maxPages, int maxResultsPerPage, String endpointRoot, Class<T> type) {
		this.maxPages = maxPages;
		this.maxResultsPerPage = maxResultsPerPage;
		this.authToken = authToken;
		this.type = type;
		this.ROOT_URL = "https://api.pexels.com/" + endpointRoot;
		
		this.gson = new GsonBuilder()
							.registerTypeAdapter(PhotosEndpointResponse.Photo.class, new PhotoResponseDeserializer())
							.create();
	}
	
	
	public T[] download(String endpoint, String query) throws IOException {
		
		@SuppressWarnings("unchecked")
		T[] responses = (T[]) Array.newInstance(type, maxPages);
		
		StatefulURIManager uriManager =
				new StatefulURIManager(ROOT_URL + endpoint, query, maxResultsPerPage, maxPages);
		
		while (uriManager.hasNext()) { //Loops up to this.maxPages times
			URI uri = uriManager.next(); //Gets URI for the next page
			
			String jsonResponse = getJsonResponse(uri);
			
			//GSON automatically converts JSON string to a Java object
			T response = gson.fromJson(jsonResponse, type);
			
			//-1 because getCurrentPage() starts from 1, and -1 because the page number is incremented beforehand
			responses[uriManager.getCurrentPage()-2] = response;
			
			try {
				Thread.sleep(1000); //Sleep for 1s between requests to avoid rate limiting.
			}
			catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}
		
		return responses;
	}
	
	protected String getJsonResponse(URI uri) throws IOException {
		CloseableHttpResponse response = null;
		
		try {
			
			HttpGet get = new HttpGet(uri);
			get.setHeader("Authorization", authToken);
			
			response = (CloseableHttpResponse) httpClient.execute(get);
			
			int statusCode = response.getStatusLine().getStatusCode();
			
			if (statusCode == 200) { //200 OK - successful request
				latestRemaining = Integer.parseInt(response.getFirstHeader("X-Ratelimit-Remaining").getValue());
				
				Header reset = response.getFirstHeader("X-Ratelimit-Reset");
				
				if (reset != null) {
					latestResetTime = Long.parseLong(response.getFirstHeader("X-Ratelimit-Reset").getValue());
					latestQuota = Integer.parseInt(response.getFirstHeader("X-Ratelimit-Limit").getValue());
				}
				
				return IOUtils.toString(response.getEntity().getContent(), StandardCharsets.UTF_8);
			}
			
		}
		finally {
			if (response != null) response.close();
		}
		
		throw new IOException("Invalid response: " + response.getStatusLine());
	}
	
	protected <E> E downloadFromId(long id, String endpoint, Class<E> type) throws IOException {
		try {
			URI uri = new URI(ROOT_URL + endpoint + "/" + id);
			
			return gson.fromJson(getJsonResponse(uri), type);
		}
		catch (URISyntaxException e) {}
		
		return null;
	}
}
