# --- Post, User schema

# --- !Ups
CREATE TABLE IF NOT EXISTS `socialnetwork`.`users` (
    `id` int (11) NOT NULL AUTO_INCREMENT,
    `email` varchar(35) NOT NULL,
    `password` varchar(255) NOT NULL,
    `first_name` varchar(255) NOT NULL,
    `last_name` varchar(255) NOT NULL,
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

# --- !Downs
drop table 'post'
drop table 'user'