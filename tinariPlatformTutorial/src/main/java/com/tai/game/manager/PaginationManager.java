package com.tai.game.manager;

import org.alfresco.service.cmr.search.ResultSet;
import org.apache.commons.lang3.StringUtils;

public class PaginationManager {
	
	public static final String BASE_URI = "/alfresco/s/game";
	
	/**
     * Checks if there can be a next page
     * 
     * @param results  the result set of the search query
     * @return  true if "results" has more pages
     */
	public Boolean hasNextPage(ResultSet results) {
		 return results.hasMore();
	}
	
	/**
     * Checks if there can be a previous page
     * 
     * @param currentPage  the current page
     * @return  true if there are previous pages
     */
	public Boolean hasPrevPage(int currentPage) {
		return currentPage > 0;
	}
	
	/**
     * Gets the next page number
     * 
     * @param currentPage  the current page number
     * @return  the next page number
     */
	public int getNextPage(int currentPage) {
		return currentPage+1;
	}
	
	/**
     * Gets the previous page number
     * 
     * @param currentPage  the current page number
     * @return  the previous page number
     */
	public int getPrevPage(int currentPage) {
		return currentPage-1;
	}
	
	/**
     * Builds the URI for the next page
     * 
     * @param toAppend  body of the URI to append at the {@link com.tai.game.manager.PaginationManager#BASE_URI Base URI}
     * @param currentPage  the current page number
     * @return  the URI string for the next page
     */
	public String constructUriNextPage(String toAppend, int currentPage) {
		if (!StringUtils.startsWith(toAppend, "/")) toAppend = "/"+toAppend;
		
		return BASE_URI + toAppend + "?page=" + getNextPage(currentPage);
	}
	
	/**
     * Builds the URI for the previous page
     * 
     * @param toAppend  body of the URI to append at the {@link com.tai.game.manager.PaginationManager#BASE_URI Base URI}
     * @param currentPage  the current page number
     * @return  the URI string for the previous page
     */
	public String constructUriPrevPage(String toAppend, int currentPage) {
		if (!StringUtils.startsWith(toAppend, "/")) toAppend = "/"+toAppend;
		
		return BASE_URI + toAppend + "?page=" + getPrevPage(currentPage);
	}
	
}
