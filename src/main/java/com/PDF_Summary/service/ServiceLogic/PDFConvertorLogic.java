package com.PDF_Summary.service.ServiceLogic;
import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.PDF_Summary.service.Service.PDFConvertorFactory;

@Service
public class PDFConvertorLogic implements PDFConvertorFactory {

	private static final Logger logger = LoggerFactory.getLogger(PDFConvertorLogic.class);
	@Override
	public String PDFConvertToText(MultipartFile File) throws IOException {
		// TODO Auto-generated method stub
		try {
		logger.info("FILE TO TEXT START");
		PDDocument document = PDDocument.load(File.getInputStream());
        PDFTextStripper pdfStripper = new PDFTextStripper();
        String text = pdfStripper.getText(document);
        document.close();
        logger.info(text);
        logger.info("FILE TO TEXT END");
        return text;
		
		}
		catch(IOException ex)
		{
			return "";
		}
		catch(Exception ex)
		{
			return ex.getMessage();
		}
	}

}
