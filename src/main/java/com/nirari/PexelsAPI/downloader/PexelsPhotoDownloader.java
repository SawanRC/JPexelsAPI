package com.nirari.PexelsAPI.downloader;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;

public class PexelsPhotoDownloader {
	
	private static final String ROOT_URL = "https://api.pexels.com/v1/";
	private static final String SEARCH_ENDPOINT = "search";
	private static final String GET_PHOTO_ENDPOINT = "photos";
	private static final String CURATED_ENDPOINT = "curated";
	
	private final HttpClient httpClient = HttpClients.custom()
			                                      .setDefaultRequestConfig(RequestConfig.custom()
					                              .setCookieSpec(CookieSpecs.STANDARD).build())
			                                      .build();
	
	private final String authToken;
	
	private int latestQuota;
	private int latestRemaining;
	private long latestResetTime;
	
	private int maxPages;
	private int maxResultsPerPage;
	
	/**
	 * Creates a {@link PexelsPhotoDownloader} object.
	 *
	 *
	 * <p>
	 *     Note: Both {@code maxPages} and {@code maxResultsPerPage} are subject to API limitations.
	 * </p>
	 * @param authToken Authentication token for the Pexels API.
	 * @param maxPages Maximum number of pages to download for endpoints that return multiple images.
	 * @param maxResultsPerPage Maximum number of images per page.
	 */
	public PexelsPhotoDownloader(String authToken, int maxPages, int maxResultsPerPage) {
		this.maxPages = maxPages;
		this.maxResultsPerPage = maxResultsPerPage;
		this.authToken = authToken;
	}
	
	/**
	 * <p>General method to query a given endpoint, and automatically download up to
	 * {@link PexelsPhotoDownloader#maxPages} pages from the given endpoint.</p>
	 *
	 * <p>
	 *     Internally used by all methods that return multiple pages of images,
	 *     including {@link PexelsPhotoDownloader#downloadPhotos(String, String)}
	 *     and {@link PexelsPhotoDownloader#downloadCuratedPhotos()}.
	 * </p>
	 *
	 * @param endpoint The specific endpoint to query.
	 * @param query Search query (if applicable for the given endpoint, or null otherwise).
	 * @return An ordered {@link PhotosEndpointResponse} array, with each element representing a single page.
	 * @throws IOException Thrown in the event that the endpoint is unreachable.
	 */
	private PhotosEndpointResponse[] downloadPhotos(String endpoint, String query) throws IOException {
		Gson gson = new GsonBuilder().create();
		PhotosEndpointResponse[] responses = new PhotosEndpointResponse[maxPages];
		
		StatefulURIManager uriManager =
				new StatefulURIManager(ROOT_URL + endpoint, query, maxResultsPerPage, maxPages);
		
		while (uriManager.hasNext()) { //Loops up to this.maxPages times
			URI uri = uriManager.next(); //Gets URI for the next page
			
			String jsonResponse = getJsonResponse(uri);
			
			//GSON automatically converts JSON string to a Java object
			PhotosEndpointResponse response = gson.fromJson(jsonResponse, PhotosEndpointResponse.class);
			
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
	
	/**
	 * Handles API pagination to automatically download up to
	 * {@link PexelsPhotoDownloader#maxPages} pages for a given query.
	 *
	 * @param query Search string
	 * @return An ordered {@link PhotosEndpointResponse} array, with each element representing a single page.
	 * @throws IOException Thrown in the event that the endpoint is unreachable.
	 */
	public PhotosEndpointResponse[] downloadPhotos(String query) throws IOException {
		return downloadPhotos(SEARCH_ENDPOINT, query);
	}
	
	/**
	 * Handles API pagination to automatically download up to
	 * {@link PexelsPhotoDownloader#maxPages} pages from the curated photos endpoint ({@link PexelsPhotoDownloader#CURATED_ENDPOINT}).
	 *
	 * @return An ordered {@link PhotosEndpointResponse} array, with each element representing a single page.
	 * @throws IOException Thrown in the event that the endpoint is unreachable.
	 */
	public PhotosEndpointResponse[] downloadCuratedPhotos() throws IOException {
		return downloadPhotos(CURATED_ENDPOINT, null);
	}
	
	
	/**
	 * Queries the given endpoint and returns the JSON response as a string
	 *
	 * @param uri Endpoint to query
	 * @return String containing the JSON response returned by the endpoint
	 * @throws IOException If querying the endpoint fails, or if the endpoint returns any other HTTP code other than
	 * 200 (OK)
	 */
	private String getJsonResponse(URI uri) throws IOException {
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
	
	
	/**
	 * Retrieves a single photo, based on the given ID.
	 *
	 * @param id Pexels ID of the photo to be queried.
	 * @return {@link PhotosEndpointResponse.Photo} representing the given photo.
	 * @throws IOException Thrown in the event that the endpoint is unreachable.
	 */
	public PhotosEndpointResponse.Photo downloadPhotoFromId(long id) throws IOException {
		Gson gson = new GsonBuilder().create();
		
		try {
			URI uri = new URI(ROOT_URL + GET_PHOTO_ENDPOINT + "/" + id);
			
			return gson.fromJson(getJsonResponse(uri), PhotosEndpointResponse.Photo.class);
		}
		catch (URISyntaxException e) {}
		
		return null;
	}
}
