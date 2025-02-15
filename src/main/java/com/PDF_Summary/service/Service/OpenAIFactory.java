package com.PDF_Summary.service.Service;

import java.io.IOException;
import java.util.List;

public interface OpenAIFactory {
	public String summarizeWithGPT(String text,List<Double> embedding, int characterLimit) throws IOException;
}
