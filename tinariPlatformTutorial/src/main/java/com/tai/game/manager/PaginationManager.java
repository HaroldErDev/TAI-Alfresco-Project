package com.tai.game.manager;

import org.alfresco.service.cmr.search.ResultSet;
import org.apache.commons.lang3.StringUtils;

public class PaginationManager {
	
	public Boolean hasNextPage(ResultSet results) {
		 return results.hasMore();
	}
	
	public Boolean hasPrevPage(int page) {
		return page > 0;
	}
	
	public int getNextPage(int page) {
		return page+1;
	}
	
	public int getPrevPage(int page) {
		return page-1;
	}
	
	public String constructUriNextPage(String toAppend, int currentPage) {
		if (!StringUtils.startsWith(toAppend, "/")) toAppend = "/"+toAppend;
		
		return "/alfresco/s/game" + toAppend + "?page=" + getNextPage(currentPage);
	}
	
	public String constructUriPrevPage(String toAppend, int currentPage) {
		if (!StringUtils.startsWith(toAppend, "/")) toAppend = "/"+toAppend;
		
		return "/alfresco/s/game" + toAppend + "?page=" + getPrevPage(currentPage);
	}
	
}
