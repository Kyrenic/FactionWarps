CREATE TABLE `warps` (
    `id` VARCHAR(36) NOT NULL,
    `faction_id` VARCHAR(36) NOT NULL,
	`name` VARCHAR(1024) NOT NULL,
	`accessible` BOOLEAN NOT NULL DEFAULT FALSE,
	`tax` DOUBLE NOT NULL DEFAULT 0,
    `world` VARCHAR(36) NOT NULL,
    `x` DOUBLE NOT NULL,
    `y` DOUBLE NOT NULL,
    `z` DOUBLE NOT NULL,
    `yaw` REAL NOT NULL,
    `pitch` REAL NOT NULL,
	PRIMARY KEY(`id`)
);

CREATE TABLE `warp_banned_players` (
	`warp_id` VARCHAR(36) NOT NULL,
	`banned_player_id` VARCHAR(36) NOT NULL,
	PRIMARY KEY(`warp_id`, `banned_player_id`),
	FOREIGN KEY(`warp_id`) REFERENCES `warps`(`id`) ON DELETE CASCADE
);

CREATE TABLE `warp_banned_factions` (
	`warp_id` VARCHAR(36) NOT NULL,
	`banned_faction_id` VARCHAR(36) NOT NULL,
	PRIMARY KEY(`warp_id`, `banned_faction_id`),
	FOREIGN KEY(`warp_id`) REFERENCES `warps`(`id`) ON DELETE CASCADE
);