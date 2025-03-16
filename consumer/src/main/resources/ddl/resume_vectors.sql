-- public.resume_vectors definition

-- Drop table

-- DROP TABLE public.resume_vectors;

CREATE TABLE public.resume_vectors (
	id bigserial NOT NULL,
	embedding public.vector NOT NULL,
	resume_id bigserial NOT NULL,
	"content" text NULL,
	metadata json NULL,
	CONSTRAINT resume_vectors_pkey PRIMARY KEY (id)
);
CREATE INDEX resume_vectors_embedding_idx ON public.resume_vectors USING hnsw (embedding vector_cosine_ops);


-- public.resume_vectors foreign keys

ALTER TABLE public.resume_vectors ADD CONSTRAINT resume_vectors_resume_id_fkey FOREIGN KEY (resume_id) REFERENCES public.resumes(id);