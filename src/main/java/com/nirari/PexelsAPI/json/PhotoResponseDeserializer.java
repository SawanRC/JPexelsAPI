package com.nirari.PexelsAPI.json;

import com.google.gson.*;

import java.lang.reflect.Type;

public class PhotoResponseDeserializer implements JsonDeserializer<PhotosEndpointResponse.Photo> {
	
	/**
	 * <p>A custom deserializer which mostly relies on the built-in GSON implementation, but with a minor adaptation
	 * to allow it to parse the separate {@code photographer_id}, {@code photographer}, and {@code photographer_url}
	 * fields as a {@link PexelsUser} object.</p>
	 * <br/>
	 * This allows the user field ({@link PhotosEndpointResponse.Photo#pexelsUser})
	 * to be consistent with {@link com.nirari.PexelsAPI.json.VideosEndpointResponse.Video}.
	 * @param jsonElement
	 * @param type
	 * @param jsonDeserializationContext
	 * @return
	 * @throws JsonParseException
	 */
	@Override
	public PhotosEndpointResponse.Photo deserialize(JsonElement jsonElement, Type type,
	                                                JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
		
		JsonObject responseObj = jsonElement.getAsJsonObject(); //Plain JSON object returned from endpoint
		
		
		//Solution to parse the separate fields into a single user object is to manually create a
		//user object with the relevant info and add it to the responseObj. Then GSON will automatically deserialize it.
		JsonObject user = new JsonObject();
		
		user.addProperty("id", responseObj.get("photographer_id").getAsInt());
		user.addProperty("name", responseObj.get("photographer").getAsString());
		user.addProperty("url", responseObj.get("photographer_url").getAsString());
		
		responseObj.add("user", user); //Add the constructed user object to the original response
		
		
		return new Gson().fromJson(responseObj, PhotosEndpointResponse.Photo.class);
	}
}
