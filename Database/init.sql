CREATE TABLE `articoli` (
	`id` BIGINT NOT NULL AUTO_INCREMENT,
	`autore` VARCHAR(255),
	`titolo` TEXT,
	`testo` TEXT,
	`data` DATE,
	`stato` BOOLEAN,
	`predizione` DOUBLE,
	PRIMARY KEY (`id`)
) ENGINE=InnoDB;