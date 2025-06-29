CREATE TABLE public.knowledge (
	id bigserial NOT NULL,
	language varchar(50) NOT NULL,--1:java 2:python 3:c++ 4:rust 5:go
	type varchar(2) NOT NULL,--1:算法 2:设计模式 3:笔试
	question 	varchar(255) NOT null,--问题
	CONSTRAINT knowledge_pkey PRIMARY KEY (id)
);