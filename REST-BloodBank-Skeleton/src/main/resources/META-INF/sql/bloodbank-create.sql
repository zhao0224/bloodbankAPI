
-- -----------------------------------------------------
-- Create Schema for BloodBank Application
--
-- In order for the `cst8277`@`localhost` user to be able to Create (or Drop) a Schema,
-- it needs additional privileges. If you are using MySQL Workbench, log-in to it as root,
-- Click on the 'Administration' tab, select 'Users and Privileges' and find the cst8277 user.
-- The 'Administrative Roles' tab has an entry 'DBDesigner' - select that and apply.
--
-- If you wish to use a 'raw' .sql-script instead, you still need to log-in as root,
-- the command to GRANT the appropriate PRIVILEGES is:
--  GRANT ALL PRIVILEGES ON *.* TO `cst8277`@`localhost`;
--
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `bloodbank` DEFAULT CHARACTER SET utf8mb4;
USE `bloodbank`;

-- ------------------------------------------------------------------------
-- Table `person`
-- Note: this is NOT the same Person Entity as Lab1/Assignment1/Assignment2
-- ------------------------------------------------------------------------

CREATE TABLE IF NOT EXISTS `person` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `first_name` VARCHAR(50) NOT NULL,
  `last_name` VARCHAR(50) NOT NULL,
  `created` DATETIME NULL,
  `updated` DATETIME NULL,
  `version` BIGINT NOT NULL DEFAULT 1,
  PRIMARY KEY (`id`)
);

-- -----------------------------------------------------
-- Table `blood_bank`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `blood_bank` (
  `bank_id` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(100) NOT NULL,
  `privately_owned` BIT(1) NOT NULL,
  `created` DATETIME NULL,
  `updated` DATETIME NULL,
  `version` BIGINT NOT NULL DEFAULT 1,
  PRIMARY KEY (`bank_id`),
  UNIQUE INDEX `name_UNIQUE` (`name` ASC) VISIBLE
);

-- -----------------------------------------------------
-- Table `blood_donation`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `blood_donation` (
  `donation_id` INT NOT NULL AUTO_INCREMENT,
  `bank_id` INT NOT NULL,
  `milliliters` INT NOT NULL,
  `blood_group` CHAR(2) NOT NULL,
  `rhd` BIT(1) NOT NULL,
  `created` DATETIME NULL,
  `updated` DATETIME NULL,
  `version` BIGINT NOT NULL DEFAULT 1,
  PRIMARY KEY (`donation_id`),
  INDEX `fk_blood_donation_blood_bank1_idx` (`bank_id` ASC) VISIBLE,
  CONSTRAINT `fk_blood_donation_blood_bank1`
    FOREIGN KEY (`bank_id`) REFERENCES `blood_bank` (`bank_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
);

-- -----------------------------------------------------
-- Table `donation_record`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `donation_record` (
  `record_id` INT NOT NULL AUTO_INCREMENT,
  `person_id` INT NOT NULL,
  `donation_id` INT NULL,
  `tested` BIT(1) NOT NULL,
  `created` DATETIME NULL,
  `updated` DATETIME NULL,
  `version` BIGINT NOT NULL DEFAULT 1,
  INDEX `fk_donation_record_person1_idx` (`person_id` ASC) VISIBLE,
  INDEX `fk_donation_record_blood_donation1_idx` (`donation_id` ASC) VISIBLE,
  UNIQUE INDEX `record_id_UNIQUE` (`record_id` ASC) VISIBLE,
  PRIMARY KEY (`record_id`),
  CONSTRAINT `fk_donation_record_person1`
    FOREIGN KEY (`person_id`)
    REFERENCES `person` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_donation_record_blood_donation1`
    FOREIGN KEY (`donation_id`)
    REFERENCES `blood_donation` (`donation_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION
);

-- -----------------------------------------------------
-- Table `address`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `address` (
  `address_id` INT NOT NULL AUTO_INCREMENT,
  `street_number` VARCHAR(10) NOT NULL,
  `street` VARCHAR(100) NOT NULL,
  `city` VARCHAR(100) NOT NULL,
  `province` VARCHAR(100) NOT NULL,
  `country` VARCHAR(100) NOT NULL,
  `zipcode` VARCHAR(100) NOT NULL,
  `created` DATETIME NULL,
  `updated` DATETIME NULL,
  `version` BIGINT NOT NULL DEFAULT 1,
  PRIMARY KEY (`address_id`)
);


-- -----------------------------------------------------
-- Table `phone`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `phone` (
  `phone_id` INT NOT NULL AUTO_INCREMENT,
  `country_code` VARCHAR(10) NOT NULL DEFAULT '1',
  `area_code` VARCHAR(10) NOT NULL,
  `number` VARCHAR(10) NOT NULL,
  `created` DATETIME NULL,
  `updated` DATETIME NULL,
  `version` BIGINT NOT NULL DEFAULT 1,
  PRIMARY KEY (`phone_id`)
);


-- -----------------------------------------------------
-- Table `contact`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `contact` (
  `person_id` INT NOT NULL,
  `phone_id` INT NOT NULL,
  `contact_type` VARCHAR(10) NOT NULL,
  `email` VARCHAR(100) NULL,
  `address_id` INT NULL,
  `created` DATETIME NULL,
  `updated` DATETIME NULL,
  `version` BIGINT NOT NULL DEFAULT 1,
  PRIMARY KEY (`person_id`, `phone_id`),
  INDEX `fk_contact_phone1_idx` (`phone_id` ASC) VISIBLE,
  INDEX `fk_contact_person1_idx` (`person_id` ASC) VISIBLE,
  CONSTRAINT `fk_contact_address1`
    FOREIGN KEY (`address_id`)
    REFERENCES `address` (`address_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_contact_phone1`
    FOREIGN KEY (`phone_id`)
    REFERENCES `phone` (`phone_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_contact_person1`
    FOREIGN KEY (`person_id`)
    REFERENCES `person` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION
);

-- Table for SecurityUser

CREATE TABLE IF NOT EXISTS `security_user` (
  `user_id` INT NOT NULL AUTO_INCREMENT,
  `password_hash` VARCHAR(256) NOT NULL,
  `username` VARCHAR(100) NOT NULL,
  `person_id` INT NULL DEFAULT NULL,
  PRIMARY KEY (`user_id`),
  UNIQUE INDEX `user_id_UNIQUE` (`user_id` ASC) VISIBLE,
  UNIQUE INDEX `username_UNIQUE` (`username` ASC) VISIBLE,
  INDEX `fk_security_user_person1_idx` (`person_id` ASC) VISIBLE,
  CONSTRAINT `fk_security_user_person1`
    FOREIGN KEY (`person_id`)
    REFERENCES `person` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION
);


-- Table for Security Role

CREATE TABLE IF NOT EXISTS `security_role` (
  `role_id` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`role_id`),
  UNIQUE INDEX `role_id_UNIQUE` (`role_id` ASC) VISIBLE,
  UNIQUE INDEX `name_UNIQUE` (`name` ASC) VISIBLE
);


-- Table for the Many-to-Many relationship between SecurityUser and SecurityRole
CREATE TABLE IF NOT EXISTS `user_has_role` (
  `user_id` INT NOT NULL,
  `role_id` INT NOT NULL,
  PRIMARY KEY (`user_id`, `role_id`),
  INDEX `fk_security_user_has_security_role_security_role1_idx` (`role_id` ASC) VISIBLE,
  INDEX `fk_security_user_has_security_role_security_user1_idx` (`user_id` ASC) VISIBLE,
  CONSTRAINT `fk_security_user_has_security_role_security_user1`
    FOREIGN KEY (`user_id`)
    REFERENCES `security_user` (`user_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_security_user_has_security_role_security_role1`
    FOREIGN KEY (`role_id`)
    REFERENCES `security_role` (`role_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION
);