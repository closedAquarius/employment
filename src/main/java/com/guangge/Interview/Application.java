package com.guangge.Interview;

import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.theme.Theme;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.pgvector.PgVectorStore;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;

@SpringBootApplication
@Theme(value = "customer-support-agent")
public class Application implements AppShellConfigurator {

	private static final Logger logger = LoggerFactory.getLogger(Application.class);

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Bean
	CommandLineRunner ingestTermOfServiceToVectorStore(
					EmbeddingModel embeddingModel, VectorStore vectorStore) {

		return args -> {
			vectorStore.similaritySearch("想找银行项目的人").forEach(doc -> {
				logger.info("Similar Document: {}", doc.getText());
			});
		};
	}

	/*@Bean
	public VectorStore vectorStore(JdbcTemplate jdbcTemplate, EmbeddingModel embeddingModel) {
		return  PgVectorStore.builder(jdbcTemplate, embeddingModel)
				.dimensions(1536)
				.distanceType(PgVectorStore.PgDistanceType.COSINE_DISTANCE)
				.indexType(PgVectorStore.PgIndexType.HNSW)
				.initializeSchema(true)
				.schemaName("public")
				.vectorTableName("resume_vectors")
				.maxDocumentBatchSize(10000)
				.build();
	}*/

	@Bean
	public ChatMemory chatMemory() {
		return new InMemoryChatMemory();
	}
}
