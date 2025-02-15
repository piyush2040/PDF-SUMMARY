package com.PDF_Summary.service.Service;

import java.io.IOException;
import java.util.List;


public interface VectorEmbedderFactory {
	public List<Double> GenerateVector(String text) throws IOException, Exception;
}
