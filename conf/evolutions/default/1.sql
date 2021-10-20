# --- Post, User schema

# --- !Ups
CREATE TABLE IF NOT EXISTS `socialnetwork`.`post` (
    `id` INT(11) NOT NULL AUTO_INCREMENT,
    `userId` INT (11) NOT NULL,
    `text` VARCHAR(255) NOT NULL ,
    `likes` INT NOT NULL,
    `created` DATE NOT NULL,
    PRIMARY KEY (`id`));


CREATE TABLE IF NOT EXISTS `socialnetwork`.`user` (
       `id` bigint(20) NOT NULL AUTO_INCREMENT,
       `email` varchar(35) NOT NULL,
       `password` varchar(255) NOT NULL,
       `first_name` varchar(255) NOT NULL,
       `last_name` varchar(255) NOT NULL,
       `registration_date` date DEFAULT NULL,
       PRIMARY KEY (`id`),
       UNIQUE KEY (`email`)
)
AUTO_INCREMENT = 1
DEFAULT CHARACTER SET = utf8mb4

# --- !Downs
drop table 'post'
drop table 'user'