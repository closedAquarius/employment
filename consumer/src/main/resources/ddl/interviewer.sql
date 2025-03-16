-- public.interviewer definition

-- Drop table

-- DROP TABLE public.interviewer;

CREATE TABLE public.interviewer (
	id bigserial NOT NULL,
	candidate_name varchar(255) NOT NULL,
	invitation_code varchar(255) NOT NULL,
	cv text NULL,
	email varchar(255) NULL,
	birth date NULL,
	status int4 DEFAULT 1 NOT NULL,
	CONSTRAINT interviewer_pkey PRIMARY KEY (id)
);