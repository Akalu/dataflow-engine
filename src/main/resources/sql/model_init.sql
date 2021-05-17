CREATE SCHEMA auditdb;

CREATE  TABLE auditdb.process_list ( 
	id                   integer  NOT NULL ,
	name                 varchar(100)   ,
	CONSTRAINT pk_process_list_id PRIMARY KEY ( id )
 );

CREATE  TABLE auditdb.process_statuses ( 
	status_id            integer  NOT NULL ,
	status_name          varchar(100)  NOT NULL ,
	CONSTRAINT pk_process_statuses_status_id PRIMARY KEY ( status_id ),
	CONSTRAINT idx_process_statuses UNIQUE ( status_name ) 
 );

CREATE  TABLE auditdb.process_launches ( 
	launch_id            varchar(100)  NOT NULL ,
	process_id           integer  NOT NULL ,
	status_id            integer   ,
	fail_info	     text,
	start_timestamp      timestamp   ,
	lastupdate_timestamp timestamp   ,
	end_timestamp        timestamp   ,
	CONSTRAINT pk_process_launches_launch_id PRIMARY KEY ( launch_id )
 );


INSERT INTO auditdb.process_statuses
(status_id, status_name)
VALUES
(1 , 'In Progress'),
(2 , 'Completed'),
(3 , 'Failed');
