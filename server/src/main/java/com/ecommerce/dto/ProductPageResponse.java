package com.ecommerce.dto;

import java.util.List;

public class ProductPageResponse {
    private List<ProductDTO> data;
    private int currentPage;
    private int pageSize;
    private long totalElements;
    private int totalPages;
    
    // Getters and setters
    public List<ProductDTO> getData() { return data; }
    public void setData(List<ProductDTO> data) { this.data = data; }
    
    public int getCurrentPage() { return currentPage; }
    public void setCurrentPage(int currentPage) { this.currentPage = currentPage; }
    
    public int getPageSize() { return pageSize; }
    public void setPageSize(int pageSize) { this.pageSize = pageSize; }
    
    public long getTotalElements() { return totalElements; }
    public void setTotalElements(long totalElements) { this.totalElements = totalElements; }
    
    public int getTotalPages() { return totalPages; }
    public void setTotalPages(int totalPages) { this.totalPages = totalPages; }
}