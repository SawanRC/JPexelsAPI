package com.nirari.PexelsAPI.json;

import com.google.gson.annotations.SerializedName;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

public class PhotosEndpointResponse {
	@SerializedName("total_results")
	public final int totalResults;
	
	@SerializedName("page")
	public final int pageIndex;
	
	@SerializedName("per_page")
	public final int perPage;
	
	@SerializedName("photos")
	public final Photo[] photos;
	
	@SerializedName("next_page")
	public final String nextPage;
	
	public PhotosEndpointResponse(int totalResults, int pageIndex, Photo[] photos, int perPage, String nextPage) {
		this.totalResults = totalResults;
		this.pageIndex = pageIndex;
		this.perPage = perPage;
		this.photos = photos;
		this.nextPage = nextPage;
	}
	
	public class Photo {
		@SerializedName("id")
		public final int id;
		
		@SerializedName("width")
		public final int width;
		
		@SerializedName("height")
		public final int height;
		
		@SerializedName("url")
		public final String url;
		
		@SerializedName("photographer")
		public final String photographer;
		
		@SerializedName("photographer_url")
		public final String photographerUrl;
		
		@SerializedName("photographer_id")
		public final int photographerId;
		
		@SerializedName("avg_color")
		public final String averageColor;
		
		@SerializedName("src")
		public final HashMap<String, String> src;
		
		@SerializedName("liked")
		public final boolean liked;
		
		private Photo(int id, int width, int height,
		             String url, String photographer, String photographerUrl,
		             int photographerId, String averageColor, HashMap<String, String> src, boolean liked) {
			
			this.id = id;
			this.width = width;
			this.height = height;
			this.url = url;
			this.photographer = photographer;
			this.photographerUrl = photographerUrl;
			this.photographerId = photographerId;
			this.averageColor = averageColor;
			this.src = src;
			this.liked = liked;
		}
		
		public BufferedImage downloadImage(String quality) throws IOException {
			URL url = new URL(this.src.get(quality));
			
			
			HttpURLConnection connection = (HttpURLConnection)url.openConnection();
			
			connection.setRequestProperty("User-Agent", "Mozilla/5.0");
			
			InputStream inputStream = connection.getInputStream();
			
			BufferedImage image = ImageIO.read(inputStream);
			
			inputStream.close();
			connection.disconnect();
			
			return image;
		}
	}
}
