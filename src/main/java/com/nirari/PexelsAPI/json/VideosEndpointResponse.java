package com.nirari.PexelsAPI.json;

import com.google.gson.annotations.SerializedName;

public class VideosEndpointResponse {
	
	@SerializedName("page")
	public final int pageIndex;
	
	@SerializedName("per_page")
	public final int perPage;
	
	@SerializedName("next_page")
	public final String nextPage;
	
	@SerializedName("total_results")
	public final String totalResults;
	
	@SerializedName("videos")
	public final Video[] videos;
	
	public VideosEndpointResponse(int pageIndex, int perPage, String nextPage, String totalResults, Video[] videos) {
		this.pageIndex = pageIndex;
		this.perPage = perPage;
		this.nextPage = nextPage;
		this.totalResults = totalResults;
		this.videos = videos;
	}
	
	public class Video {
		@SerializedName("id")
		public final int id;
		
		@SerializedName("width")
		public final int width;
		
		@SerializedName("height")
		public final int height;
		
		@SerializedName("url")
		public final String url;
		
		@SerializedName("image")
		public final String thumbnailUrl;
		
		@SerializedName("duration")
		public final int duration;
		
		@SerializedName("user")
		public final PexelsUser pexelsUser;
		
		@SerializedName("video_files")
		public final VideoFile[] files;
		
		@SerializedName("video_pictures")
		public final PreviewImage[] previewImages;
		
		
		public Video(int id, int width, int height, String url, String thumbnailUrl, int duration,
		             PexelsUser pexelsUser, VideoFile[] files, PreviewImage[] previewImages) {
			
			this.id = id;
			this.width = width;
			this.height = height;
			this.url = url;
			this.thumbnailUrl = thumbnailUrl;
			this.duration = duration;
			this.pexelsUser = pexelsUser;
			this.files = files;
			this.previewImages = previewImages;
		}
		
		
		private class VideoFile {
			@SerializedName("id")
			public final int id;
			
			@SerializedName("quality")
			public final String quality;
			
			@SerializedName("file_type")
			public final String fileType;
			
			@SerializedName("width")
			public final int width;
			
			@SerializedName("height")
			public final int height;
			
			@SerializedName("link")
			public final String url;
			
			private VideoFile(int id, String quality, String fileType, int width, int height, String url) {
				this.id = id;
				this.quality = quality;
				this.fileType = fileType;
				this.width = width;
				this.height = height;
				this.url = url;
			}
		}
		
		private class PreviewImage {
			@SerializedName("id")
			public final int id;
			
			@SerializedName("picture")
			public final String url;
			
			@SerializedName("nr")
			public final int index;
			
			private PreviewImage(int id, String url, int index) {
				this.id = id;
				this.url = url;
				this.index = index;
			}
		}
	}
}
