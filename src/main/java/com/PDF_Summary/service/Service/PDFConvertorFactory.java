package com.PDF_Summary.service.Service;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;


public interface PDFConvertorFactory {

	public String PDFConvertToText(MultipartFile File) throws IOException;
}
