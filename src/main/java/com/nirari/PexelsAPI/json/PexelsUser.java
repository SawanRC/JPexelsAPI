package com.nirari.PexelsAPI.json;

import com.google.gson.annotations.SerializedName;

public class PexelsUser {
	
	@SerializedName(value="id", alternate="photographer_id")
	public final int id;
	
	@SerializedName(value="name", alternate="photographer")
	public final String name;
	
	@SerializedName(value="url", alternate="photographer_url")
	public final String url;
	
	public PexelsUser(int id, String name, String url) {
		this.id = id;
		this.name = name;
		this.url = url;
	}
}
