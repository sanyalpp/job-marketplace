-- H2 Database Schema
-- Person Table
create table if not exists Person (
   person_id varchar(255) primary key,
   first_name varchar(255) not null,
   last_name varchar(255) not null,
   email varchar(255) not null,
   created_by varchar(255) not null,
   created_date timestamp not null,
   updated_by varchar(255) not null,
   updated_date timestamp not null   
);

ALTER TABLE JOBMARKET.Person ADD CONSTRAINT Person_UNIQ_CONST UNIQUE (email);

-- Project table
create table if not exists Project (
	project_id varchar(255) primary key,
	project_owner_id varchar(255) references Person(person_id) ON DELETE CASCADE,
	project_name varchar(255) not null,
	requirements clob not null,
	max_budget decimal not null,
	last_date_time timestamp not null,
	created_by varchar(255) not null,
  	created_date timestamp not null,
  	updated_by varchar(255) not null,
  	updated_date timestamp not null
);

-- Bid table
create table if not exists Bid (
  bid_id varchar(255) primary key,
  bidder_id varchar(255) references Person(person_id) ON DELETE CASCADE,
  project_id varchar(255) references Project(project_id) ON DELETE CASCADE,
  bid_amount decimal not null,
  created_by varchar(255) not null,
  created_date timestamp not null,
  updated_by varchar(255) not null,
  updated_date timestamp not null
);

-- Winning Bid table
create table if not exists Winning_Bid (
  bid_id varchar(255) primary key,
  bidder_id varchar(255) references Person(person_id) ON DELETE CASCADE,
  project_id varchar(255) references Project(project_id) ON DELETE CASCADE,
  bid_amount decimal not null,
  created_by varchar(255) not null,
  created_date timestamp not null,
  updated_by varchar(255) not null,
  updated_date timestamp not null
);

 -- H2 doesn't support materialized views, in real implementation with say postgres this should be a materialized view.
CREATE VIEW Lowest_Bid_View AS
(
select bid_id, project_id, bid_amount, bidder_id, created_by, created_date from ((select bid_id, project_id, bid_amount, bidder_id, created_by, created_date from jobmarket.bid b1 where bid_amount = 
 		(select min(bid_amount) from jobmarket.bid as b2 where b2 .project_id = b1.project_id) ) ) a1 where created_date = 
 		(select min(created_date) from ((select bid_id, project_id, bid_amount, bidder_id, created_by, created_date from jobmarket.bid b1 where bid_amount = 
 		(select min(bid_amount) from jobmarket.bid as b2 where b2 .project_id = b1.project_id) ) ) a2 where a2.project_id = a1.project_id)
);

SET SCHEMA public;
CREATE ALIAS jobmarket.refresh_materialized_view FOR "com.generic.job.marketplace.datasource.h2.FunctionAliases.refreshMaterializedView";

