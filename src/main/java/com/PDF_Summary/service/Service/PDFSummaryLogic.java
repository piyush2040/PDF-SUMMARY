package com.PDF_Summary.service.Service;


import org.springframework.web.multipart.MultipartFile;

public interface PDFSummaryLogic {

	public String processPDF(MultipartFile file, int characterLimit);
}
