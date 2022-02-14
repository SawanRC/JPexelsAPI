package com.nirari.PexelsAPI.downloader;

import com.nirari.PexelsAPI.json.VideosEndpointResponse;

import java.io.IOException;

public class PexelsVideoDownloader extends PexelsDownloader<VideosEndpointResponse> {
	
	private static final String SEARCH_ENDPOINT = "search";
	private static final String GET_VIDEO_ENDPOINT = "videos";
	private static final String POPULAR_ENDPOINT = "popular";
	
	/**
	 * Creates a {@link PexelsVideoDownloader} object.
	 *
	 *
	 * <p>
	 *     Note: Both {@code maxPages} and {@code maxResultsPerPage} are subject to API limitations.
	 * </p>
	 * @param authToken Authentication token for the Pexels API.
	 * @param maxPages Maximum number of pages to download for endpoints that return multiple images.
	 * @param maxResultsPerPage Maximum number of images per page.
	 */
	public PexelsVideoDownloader(String authToken, int maxPages, int maxResultsPerPage) {
		super(authToken, maxPages, maxResultsPerPage, "videos/", VideosEndpointResponse.class);
	}
	
	/**
	 * Handles API pagination to automatically download up to
	 * {@link PexelsDownloader#maxPages} pages for a given query.
	 *
	 * @param query Search string
	 * @return An ordered {@link VideosEndpointResponse} array, with each element representing a single page.
	 * @throws IOException Thrown in the event that the endpoint is unreachable.
	 */
	public VideosEndpointResponse[] downloadVideos(String query) throws IOException {
		return download(SEARCH_ENDPOINT, query);
	}
	
	/**
	 * Handles API pagination to automatically download up to
	 * {@link PexelsDownloader#maxPages} pages from the popular videos endpoint ({@link PexelsVideoDownloader#POPULAR_ENDPOINT}).
	 *
	 * @return An ordered {@link VideosEndpointResponse} array, with each element representing a single page.
	 * @throws IOException Thrown in the event that the endpoint is unreachable.
	 */
	public VideosEndpointResponse[] downloadPopularVideos() throws IOException {
		return download(POPULAR_ENDPOINT, null);
	}
	
	/**
	 * Retrieves a single video, based on the given ID.
	 *
	 * @param id Pexels ID of the video to be queried.
	 * @return {@link VideosEndpointResponse.Video} representing the given video.
	 * @throws IOException Thrown in the event that the endpoint is unreachable.
	 */
	public VideosEndpointResponse.Video downloadVideoFromId(long id) throws IOException {
		return downloadFromId(id, GET_VIDEO_ENDPOINT, VideosEndpointResponse.Video.class);
	}
}
