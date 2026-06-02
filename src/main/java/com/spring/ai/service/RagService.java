package com.spring.ai.service;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

@Service
public class RagService {
	
	private final Logger logger = LoggerFactory.getLogger(RagService.class);
	
	private final VectorStore vectorStore;
	private final ChatClient chatClient;
	
//	Dependency Injection( Constructor type)
	public RagService(VectorStore vectorStore,ChatClient.Builder chatClientBuilder) {
		this.vectorStore = vectorStore;
		this.chatClient = chatClientBuilder.build();
	}
	
//	retrieve the store document chunks in vector store
//	build the augmented prompt with chunks
//	then call LLM models with context 
//	then return answer to user
	
	public String query(String userQuestion) {
		
		logger.info("User question: {}", userQuestion);
		
		SearchRequest searchRequest = SearchRequest.builder().
				query(userQuestion).topK(8).similarityThreshold(0.4).build();
		
		logger.debug("Search request: {}", searchRequest.toString());
		
		List<Document> relatedDocuments = vectorStore.similaritySearch(searchRequest);
		String context = relatedDocuments.stream().map(Document::getFormattedContent).
				collect(Collectors.joining("\n\n---\n\n"));
		logger.debug("Context from documents: {}", context);
		
		String llmPrompt = """
				You are a helpful assistant.
				Answer ONLY using the context below.
				If the answer is not in the context, say 'I do not have that information.'
				
				CONTEXT: %s
				QUESTION: %s
				
				ANSWER:
				""".formatted(context, userQuestion);
		logger.debug("Prompt: {}", llmPrompt);
		return chatClient.prompt().user(llmPrompt).call().content();
	}
	
	
}
