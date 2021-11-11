# --- Post, User schema

# --- !Ups
CREATE TABLE IF NOT EXISTS `socialnetwork`.`users` (
    `id` int (11) NOT NULL AUTO_INCREMENT,
    `email` varchar(35) NOT NULL,
    `password` varchar(255) NOT NULL,
    `first_name` varchar(255) NOT NULL,
    `last_name` varchar(255) NOT NULL,
    `picture` varchar(255) NOT NULL,
    `registration_date` date DEFAULT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY (`email`)
);

CREATE TABLE IF NOT EXISTS `socialnetwork`.`posts` (
    `id` int(11) NOT NULL AUTO_INCREMENT,
    `user_id` int (11) NOT NULL,
    `text` VARCHAR(255) NOT NULL ,
    `likes` INT NOT NULL,
    `created` DATE NOT NULL,
    PRIMARY KEY (`id`),
    FOREIGN KEY (user_id) REFERENCES users(id)
);


CREATE TABLE IF NOT EXISTS `socialnetwork`.`likes` (
`id` int(11) NOT NULL AUTO_INCREMENT,
`user_id` int (11) NOT NULL,
`post_id` int (11) NOT NULL,
PRIMARY KEY (`id`),
FOREIGN KEY (user_id) REFERENCES users(id),
FOREIGN KEY (post_id) REFERENCES posts(id),
UNIQUE KEY `userpostuk` (`user_id`,`post_id`)
);

CREATE TABLE IF NOT EXISTS `socialnetwork`.`friend_requests` (
`id` int(11) NOT NULL AUTO_INCREMENT,
`sender_id` int (11) NOT NULL,
`receiver_id` int (11) NOT NULL,
PRIMARY KEY (`id`),
FOREIGN KEY (sender_id) REFERENCES users(id),
FOREIGN KEY (receiver_id) REFERENCES users(id),
UNIQUE KEY `uk1` (`sender_id`,`receiver_id`)
);

CREATE TABLE IF NOT EXISTS `socialnetwork`.`friendships` (
`id` int(11) NOT NULL AUTO_INCREMENT,
`user1_id` int (11) NOT NULL,
`user2_id` int (11) NOT NULL,
PRIMARY KEY (`id`),
FOREIGN KEY (user1_id) REFERENCES users(id),
FOREIGN KEY (user2_id) REFERENCES users(id),
UNIQUE KEY `uk1` (`user1_id`,`user2_id`),
UNIQUE KEY `uk2` (`user2_id`,`user1_id`)
);

# --- !Downs
drop table 'users';
drop table 'posts';
drop table 'likes';
drop table 'friend_requests';
drop table 'friendships';
