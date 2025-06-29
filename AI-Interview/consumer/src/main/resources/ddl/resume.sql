-- public.resumes definition

-- Drop table

-- DROP TABLE public.resumes;

CREATE TABLE public.resumes (
	id bigserial NOT NULL,
	candidate_name varchar(255) NOT NULL,
	raw_text text NULL,
	score int4 DEFAULT 0 NOT NULL,
	email varchar(255) NULL,
	inter_view_status int4 DEFAULT 1 NOT NULL,
	evaluate varchar(255) NULL,
	mp3_path varchar(255) NULL,
	interview_evaluate varchar(255) NULL,
	is_done_status int2 DEFAULT 0 NOT NULL,
	CONSTRAINT resumes_pkey PRIMARY KEY (id)
);