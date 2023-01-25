package com.tai.game.scripts;

import org.apache.commons.lang3.StringUtils;

public class URIExtensionModifier {
	
	public static final String HTML = ".html";
	public static final String JSON = ".json";
	
	private String URI;
	
	public URIExtensionModifier(String uri) {
		this.URI = uri;
	}
	
	public String getHtml() {
		String beforeQuery = StringUtils.substringBefore(this.URI, "?");
		String afterQuery = this.URI.substring(this.URI.indexOf("?"));
		return beforeQuery + HTML + afterQuery;
	}
	
	public String getJson() {
		String beforeQuery = StringUtils.substringBefore(this.URI, "?");
		String afterQuery = this.URI.substring(this.URI.indexOf("?"));
		return beforeQuery + JSON + afterQuery;
	}
	
}
