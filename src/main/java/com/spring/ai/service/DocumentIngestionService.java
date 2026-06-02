package com.spring.ai.service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class DocumentIngestionService {
	
	private final Logger logger = LoggerFactory.getLogger(DocumentIngestionService.class);
	
	private final VectorStore vectorStore;
	private final TokenTextSplitter tokenTextSplitter;
	
	public DocumentIngestionService(VectorStore vectorStore, TokenTextSplitter tokenTextSplitter) {
		this.vectorStore = vectorStore;
		this.tokenTextSplitter = tokenTextSplitter;
	}
	
	public void documentIngest(MultipartFile file) throws IOException {
		logger.info("Document ingestion method started");
		String contentOfFile = new String(file.getBytes(), StandardCharsets.UTF_8);
		Document document = new Document(contentOfFile);
		document.getMetadata().put("filename", file.getOriginalFilename());
		List<Document> listOfDocumentChunks = tokenTextSplitter.apply(List.of(document));
		vectorStore.add(listOfDocumentChunks);
		logger.info("Chunk size {} filename {}",listOfDocumentChunks.size(),file.getOriginalFilename());
		logger.info("Document ingestion method Ended");

		
	}

}
