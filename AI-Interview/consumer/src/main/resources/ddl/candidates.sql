-- public.candidates definition

-- Drop table

-- DROP TABLE public.candidates;

CREATE TABLE public.candidates (
	id int8 DEFAULT nextval('interviewer_id_seq'::regclass) NOT NULL,
	candidate_name varchar(255) NOT NULL,
	invitation_code varchar(255) NOT NULL,
	cv text NULL,
	email varchar(255) NULL,
	birth date NULL,
	status int4 DEFAULT 1 NOT NULL,
	picture_url varchar(255) NULL,
	CONSTRAINT interviewer_pkey PRIMARY KEY (id)
);