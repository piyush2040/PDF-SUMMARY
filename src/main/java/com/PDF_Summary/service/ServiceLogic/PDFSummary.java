package com.PDF_Summary.service.ServiceLogic;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.PDF_Summary.service.Service.OpenAIFactory;
import com.PDF_Summary.service.Service.PDFConvertorFactory;
import com.PDF_Summary.service.Service.PDFSummaryLogic;

@Service
public class PDFSummary implements PDFSummaryLogic {
	
	@Autowired
	private PDFConvertorFactory pdfConvertorFactory;
	@Autowired
	private VectorEmbedderLogic vectorEmbedderLogic;
	@Autowired
	private OpenAIFactory openAIFactory;

	@Override
	public String processPDF(MultipartFile file, int characterLimit) {
		// TODO Auto-generated method stub
		try {
			if(file == null || file.isEmpty())
			{
				return "ERROR|File is Empty.";
			}
			String pdftext = pdfConvertorFactory.PDFConvertToText(file);
			if(pdftext.startsWith("ERROR"))
			{
				return pdftext;
			}
			List<String> chunks = splitIntoChunks(pdftext, 1000);
			List<String> summaries = new ArrayList<>();
			// Step 2: Generate vector embeddings and summarize each chunk
	        for (String chunk : chunks) {
	            //List<Double> embedding = vectorEmbedderLogic.GenerateVector(chunk); // Generate vector embedding
	            String summary = openAIFactory.summarizeWithGPT(chunk, null, 100); // Summarize with vector embedding
	            summaries.add(summary);
	        }
	        String combinedSummary = String.join(" ", summaries);
	        //need to optimize using recursive way or new algorithm
	        String finalSummary = openAIFactory.summarizeWithGPT(combinedSummary,null, characterLimit);
			//List<Double> vectorDoubles = vectorEmbedderLogic.GenerateVector(pdftext);
//			if(vectorDoubles.size() == 0)
//			{
//				return "ERROR|Got Error in vector Embedding";
//			}
			//String summaryString = openAIFactory.summarizeWithGPT(vectorDoubles, characterLimit);
			if(finalSummary.startsWith("ERROR"))
			{
				return finalSummary;
			}
			return finalSummary;
			
		}
		catch(Exception ex)
		{
			return "ERROR|" + ex.getMessage();
		}
	}
	// Helper method to split text into chunks
    private List<String> splitIntoChunks(String text, int chunkSize) {
        List<String> chunks = new ArrayList<>();
        int start = 0;
        while (start < text.length()) {
            int end = Math.min(start + chunkSize, text.length());
            chunks.add(text.substring(start, end));
            start = end;
        }
        
        return chunks;
    }
	
}
