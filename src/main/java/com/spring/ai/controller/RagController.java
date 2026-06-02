package com.spring.ai.controller;

import java.io.IOException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.spring.ai.dto.QueryRequestDTO;
import com.spring.ai.dto.QueryResponseDTO;
import com.spring.ai.service.DocumentIngestionService;
import com.spring.ai.service.RagService;

@RestController
@RequestMapping("/api/rag")
public class RagController {
	
	private final RagService ragService;
	private final DocumentIngestionService documentIngestionService;
	
	public RagController(RagService ragService,DocumentIngestionService documentIngestionService) {
		this.ragService = ragService;
		this.documentIngestionService = documentIngestionService;
	}
	
	@PostMapping("/add/document")
	public ResponseEntity<String> documentIngest(@RequestParam("file") MultipartFile file) throws IOException{
		documentIngestionService.documentIngest(file);
		return ResponseEntity.ok("File uploaded as reference documnet: "+file.getOriginalFilename());
	}
	
	@PostMapping("/query")
	public ResponseEntity<QueryResponseDTO> query(@RequestBody QueryRequestDTO request) {
		String answer = ragService.query(request.getQuestion());
		QueryResponseDTO response = new QueryResponseDTO();
		response.setAnswer(answer);
		return ResponseEntity.ok(response);
	}
}
