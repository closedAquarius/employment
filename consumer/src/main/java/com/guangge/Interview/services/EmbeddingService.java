package com.guangge.Interview.services;

import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmbeddingService {
    private final VectorStore vectorStore; // Spring AI
    private final EmbeddingModel embeddingModel;

    public EmbeddingService(VectorStore vectorStore, EmbeddingModel embeddingModel) {
        this.vectorStore = vectorStore;
        this.embeddingModel = embeddingModel;
    }

    public float[] generateEmbedding(String text) {
        return embeddingModel.embed(text);
    }

    public float[] generateEmbedding(Document document) {
        return embeddingModel.embed(document.getText());
    }

    public void saveEmbedding(String text) {
        List<Document> documents = List.of(new Document(text));
        vectorStore.add(documents);
    }
}
