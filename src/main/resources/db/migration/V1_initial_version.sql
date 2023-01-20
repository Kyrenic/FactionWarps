CREATE TABLE `warps`(
    id VARCHAR(36) NOT NULL,
    faction_id VARCHAR(36) NOT NULL,
	name VARCHAR(1024) NOT NULL,
	open BOOLEAN NOT NULL DEFAULT FALSE,
	tax DOUBLE NOT NULL DEFAULT 0,
	PRIMARY KEY(`id`),
)

CREATE TABLE `warp_locations`(
	warp_id VARCHAR(36) NOT NULL,
	world VARCHAR(36) NOT NULL,
	x DOUBLE NOT NULL,
	y DOUBLE NOT NULL,
	z DOUBLE NOT NULL,
	yaw DOUBLE NOT NULL,
	pitch DOUBLE NOT NULL,
	PRIMARY KEY(`warp_id`),
	FOREIGN KEY(`warp_id`) REFERENCES `warps`(`id`) ON DELETE CASCADE
)

CREATE TABLE `warp_banned_players`(
	warp_id VARCHAR(36) NOT NULL,
	banned_player_id VARCHAR(36) NOT NULL,
	PRIMARY KEY(`warp_id`, `banned_player_id`),
	FOREIGN KEY(`warp_id`) REFERENCES `warps`(`id`) ON DELETE CASCADE
)

CREATE TABLE `warp_banned_factions`(
	warp_id VARCHAR(36) NOT NULL,
	banned_faction_id VARCHAR(36) NOT NULL,
	PRIMARY KEY(`warp_id`, `banned_faction_id`),
	FOREIGN KEY(`warp_id`) REFERENCES `warps`(`id`) ON DELETE CASCADE
)