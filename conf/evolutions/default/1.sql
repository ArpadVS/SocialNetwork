# Post schema

# --- !Ups
CREATE TABLE IF NOT EXISTS `socialnetwork`.`post` (
    `id` INT(11) NOT NULL AUTO_INCREMENT,
    `userId` INT (11) NOT NULL,
    `text` VARCHAR(255) NOT NULL ,
    `likes` INT NOT NULL,
    `created` DATE NOT NULL,
    PRIMARY KEY (`id`))
AUTO_INCREMENT = 2
DEFAULT CHARACTER SET = utf8

# --- !Downs
drop table 'post'