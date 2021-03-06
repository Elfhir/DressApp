package com.dressapp;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Base64;

public class JSONParser {

	public static Cloth parseCloth (JSONObject json) throws JSONException
	{
		if (json == null)
			return null;
		
		
		int id = 0;
		String
			name = "",
			color1 = "",
			color2 = "",
			occasion = "",
			season = "",
			category = "";
		byte[] img = null;
		
		if (json.has("id"))
			id = json.getInt("id");
		
		if (json.has("name"))
			name = json.getString("name");
		
		if (json.has("color1"))
			color1 = json.getString("color1");
		
		if (json.has("color2"))
			color2 = json.getString("color2");
		
		if (json.has("style"))
			occasion = json.getString("style");
		
		if (json.has("season"))
			season = json.getString("season");
		
		if (json.has("category"))
			category = json.getString("category");
		
		if (json.has("image"))
			img = Base64.decode(json.getString("image"), Base64.DEFAULT);
		
		Cloth cloth = new Cloth ();
		
		cloth.setId(id);
		cloth.edit(name, color1, color2, occasion, season, category);
		cloth.setImg(img);
		
		return cloth;
	}
	
	public static ArrayList<Cloth> parseClothes (JSONObject json) throws JSONException
	{
		if (json == null)
			return null;
		
		JSONArray jArray = null;
		ArrayList<Cloth> clothes = new ArrayList<Cloth> ();
		Cloth temp_cloth = null;
		int i = 0,
			jArray_length = 0;
		
		jArray = json.getJSONArray("objects");
		
		jArray_length = jArray.length();
		
		for (i = 0; i < jArray_length; ++i)
		{
			temp_cloth = parseCloth((JSONObject) jArray.get(i));
			clothes.add(temp_cloth);
		}
		
		return clothes;
	}
}
