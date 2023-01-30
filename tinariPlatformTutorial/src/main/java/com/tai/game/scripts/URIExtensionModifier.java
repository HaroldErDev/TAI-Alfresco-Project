package com.tai.game.scripts;

import org.apache.commons.lang3.StringUtils;

public class URIExtensionModifier {
	
	public static final String HTML = ".html";
	public static final String JSON = ".json";
	
	private String URI;
	
	public URIExtensionModifier(String URI) {
		this.URI = URI;
	}
	
	/**
     * Gets the URI with {@link com.tai.game.scripts.URIExtensionModifier#HTML html} extension. Examples:
     * <p>
     * {@code "/alfresco/s/game/test?..."} will be:
     * <br>{@code "/alfresco/s/game/test.html?..."}
     * <p>
     * {@code "/alfresco/s/game/test"} will be:
     * <br>{@code "/alfresco/s/game/test.html"}
     * 
     * @return  the URI string with html extension
     */
	public String getHtml() {
		if (!this.URI.contains("?")) return this.URI + HTML;
		
		String beforeQuery = StringUtils.substringBefore(this.URI, "?");
		String afterQuery = this.URI.substring(this.URI.indexOf("?"));
		return beforeQuery + HTML + afterQuery;
	}
	
	/**
     * Gets the URI with {@link com.tai.game.scripts.URIExtensionModifier#JSON json} extension. Examples:
     * <p>
     * {@code "/alfresco/s/game/test?..."} will be:
     * <br>{@code "/alfresco/s/game/test.json?..."}
     * <p>
     * {@code "/alfresco/s/game/test"} will be:
     * <br>{@code "/alfresco/s/game/test.json"}
     * 
     * @return  the URI string with json extension
     */
	public String getJson() {
		if (!this.URI.contains("?")) return this.URI + JSON;
		
		String beforeQuery = StringUtils.substringBefore(this.URI, "?");
		String afterQuery = this.URI.substring(this.URI.indexOf("?"));
		return beforeQuery + JSON + afterQuery;
	}
	
	public void setUri(String URI) {
		this.URI = URI;
	}
	
	public String getUri() {
		return this.URI;
	}
	
}
