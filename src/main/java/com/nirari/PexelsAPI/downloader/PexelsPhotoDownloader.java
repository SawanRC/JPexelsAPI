package com.nirari.PexelsAPI.downloader;

import com.nirari.PexelsAPI.json.PhotosEndpointResponse;

import java.io.IOException;

public class PexelsPhotoDownloader extends PexelsDownloader<PhotosEndpointResponse> {
	
	private static final String SEARCH_ENDPOINT = "search";
	private static final String GET_PHOTO_ENDPOINT = "photos";
	private static final String CURATED_ENDPOINT = "curated";
	
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
		super(authToken, maxPages, maxResultsPerPage, "v1/", PhotosEndpointResponse.class);
	}

	
	/**
	 * Handles API pagination to automatically download up to
	 * {@link PexelsDownloader#maxPages} pages for a given query.
	 *
	 * @param query Search string
	 * @return An ordered {@link PhotosEndpointResponse} array, with each element representing a single page.
	 * @throws IOException Thrown in the event that the endpoint is unreachable.
	 */
	public PhotosEndpointResponse[] downloadPhotos(String query) throws IOException {
		return download(SEARCH_ENDPOINT, query);
	}
	
	/**
	 * Handles API pagination to automatically download up to
	 * {@link PexelsDownloader#maxPages} pages from the curated photos endpoint ({@link PexelsPhotoDownloader#CURATED_ENDPOINT}).
	 *
	 * @return An ordered {@link PhotosEndpointResponse} array, with each element representing a single page.
	 * @throws IOException Thrown in the event that the endpoint is unreachable.
	 */
	public PhotosEndpointResponse[] downloadCuratedPhotos() throws IOException {
		return download(CURATED_ENDPOINT, null);
	}
	
	/**
	 * Retrieves a single photo, based on the given ID.
	 *
	 * @param id Pexels ID of the photo to be queried.
	 * @return {@link PhotosEndpointResponse.Photo} representing the given photo.
	 * @throws IOException Thrown in the event that the endpoint is unreachable.
	 */
	public PhotosEndpointResponse.Photo downloadPhotoFromId(long id) throws IOException {
		return downloadFromId(id, GET_PHOTO_ENDPOINT, PhotosEndpointResponse.Photo.class);
	}
}
