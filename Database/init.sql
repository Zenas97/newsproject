CREATE TABLE `articoli` (
	`id` BIGINT NOT NULL AUTO_INCREMENT,
	`autore` VARCHAR(255),
	`titolo` TEXT,
	`testo` TEXT,
	PRIMARY KEY (`id`)
) ENGINE=InnoDB;