package com.PDF_Summary.service.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.PDF_Summary.service.Service.PDFSummaryLogic;

@RestController
@RequestMapping("/api/pdf-summary")
public class PDFConvertorController {
	
	@Autowired
	private PDFSummaryLogic pdfSummaryLogic;
	
	@PostMapping(value = "/summarize", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> summarizePdf(
            @RequestParam("file") MultipartFile file,
            @RequestParam("characterLimit") int characterLimit) {

        try {
            String summary = pdfSummaryLogic.processPDF(file, characterLimit);
            return ResponseEntity.ok(summary);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error processing the file: " + e.getMessage());
        }
    }
}
