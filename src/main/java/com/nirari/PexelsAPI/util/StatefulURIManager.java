package com.nirari.PexelsAPI.util;

import org.apache.http.client.utils.URIBuilder;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Iterator;

public class StatefulURIManager implements Iterator<URI> {
	
	private final URIBuilder uriBuilder;
	private final String searchQuery;
	private final int maxResultsPerPage;
	private final int maxPages;
	
	private int currentPage = 1;
	
	public StatefulURIManager(String url, String query, int maxResultsPerPage, int maxPages) {
		try {
			this.uriBuilder = new URIBuilder(url);
		}
		catch (URISyntaxException e) {
			throw new RuntimeException("Invalid URI");
		}
		
		this.searchQuery = query;
		this.maxResultsPerPage = maxResultsPerPage;
		this.maxPages = maxPages;
	}
	
	public StatefulURIManager(String url, int maxResultsPerPage, int maxPages) {
		this(url, null, maxResultsPerPage, maxPages);
	}
	
	
	@Override
	public boolean hasNext() {
		return currentPage <= maxPages;
	}
	
	@Override
	public URI next() {
		try {
			uriBuilder.setParameter("per_page", String.valueOf(maxResultsPerPage));
			uriBuilder.setParameter("page", String.valueOf(currentPage));
			
			if (searchQuery != null) {
				uriBuilder.setParameter("query", this.searchQuery);
			}
			
			currentPage++;
			
			return uriBuilder.build();
		}
		catch (URISyntaxException e) {
			throw new RuntimeException("Invalid URI"); //Not expecting to reach here
		}
	}
	
	public int getCurrentPage() {
		return currentPage;
	}
}
