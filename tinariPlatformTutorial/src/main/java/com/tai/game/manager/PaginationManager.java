package com.tai.game.manager;

import org.alfresco.service.cmr.search.ResultSet;

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
	
}
