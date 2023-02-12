CREATE TABLE `command_log` (
	`id` INT NOT NULL AUTO_INCREMENT,
	`sender_id` VARCHAR(36),
	`command` VARCHAR(1024) NOT NULL,
	`time` DATETIME NOT NULL DEFAULT NOW(),
	PRIMARY KEY(`id`)
);