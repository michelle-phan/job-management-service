CREATE DATABASE IF NOT EXISTS `search-jms`;

GRANT ALL PRIVILEGES ON *.* TO 'root' IDENTIFIED BY 'password';

USE search-jms;

DROP TABLE IF EXISTS `scheduler`;

DROP TABLE IF EXISTS `spark_job`;

CREATE TABLE spark_job (
	id BIGINT NOT NULL auto_increment,
	uuid BINARY(16) NOT NULL,
	created_by BINARY(16) NOT NULL,
	created_date datetime NOT NULL,
	modified_by binary(255),
	modified_date datetime,
	name VARCHAR(255) NOT NULL,
	description VARCHAR(255),
	path VARCHAR(255) NOT NULL,
	file VARCHAR(255) NOT NULL,
	main_class VARCHAR(255) NOT NULL,
	number_of_args TINYINT(1) NOT NULL,
	PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE scheduler (
	id BIGINT NOT NULL auto_increment,
	uuid BINARY(16) NOT NULL,
	created_by BINARY(16) NOT NULL,
	created_date datetime NOT NULL,
	modified_by binary(255),
	modified_date datetime,
	name VARCHAR(255) NOT NULL,
	dag VARCHAR(255) NOT NULL,
	description VARCHAR(255),
	args VARCHAR(255) NOT NULL,
	schedule_interval VARCHAR(255) NOT NULL,
	driver_memory VARCHAR(255) NOT NULL,
	executor_cores tinyint(1) NOT NULL,
	retries tinyint(1) NOT NULL,
	email VARCHAR(255) NOT NULL,
	start_date datetime NOT NULL,
	spark_job_id BIGINT NOT NULL,
	active TINYINT(1) DEFAULT 0,
	PRIMARY KEY (id),
    CONSTRAINT `scheduler_ibfk_1` FOREIGN KEY (`spark_job_id`) REFERENCES `spark_job` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
