
-- -----------------------------------------------------
-- Someone has to create the SKRS tables first time
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `SKRSViewMapping` (
  `idSKRSViewMapping` BIGINT(15) NOT NULL AUTO_INCREMENT ,
  `register` VARCHAR(255) NOT NULL ,
  `datatype` VARCHAR(255) NOT NULL ,
  `version` INT NOT NULL ,
  `tableName` VARCHAR(255) NOT NULL ,
  `createdDate` TIMESTAMP NOT NULL ,
  PRIMARY KEY (`idSKRSViewMapping`) ,
  INDEX `idx` (`register` ASC, `datatype` ASC, `version` ASC) ,
  UNIQUE INDEX `unique` (`register` ASC, `datatype` ASC, `version` ASC) )
  ENGINE = InnoDB;
CREATE  TABLE IF NOT EXISTS `SKRSColumns` (
  `idSKRSColumns` BIGINT(15) NOT NULL AUTO_INCREMENT ,
  `viewMap` BIGINT(15) NOT NULL ,
  `isPID` TINYINT NOT NULL ,
  `tableColumnName` VARCHAR(255) NOT NULL ,
  `feedColumnName` VARCHAR(255) NULL ,
  `feedPosition` INT NOT NULL ,
  `dataType` INT NOT NULL ,
  `maxLength` INT NULL ,
  PRIMARY KEY (`idSKRSColumns`) ,
  INDEX `viewMap_idx` (`viewMap` ASC) ,
  UNIQUE INDEX `viewColumn` (`tableColumnName` ASC, `viewMap` ASC) ,
  CONSTRAINT `viewMap`
  FOREIGN KEY (`viewMap` )
  REFERENCES `SKRSViewMapping` (`idSKRSViewMapping` )
    ON DELETE CASCADE
    ON UPDATE NO ACTION)
  ENGINE = InnoDB;

-- ---------------------------------------------------------------------------------------------------------------------
-- SKS
-- ---------------------------------------------------------------------------------------------------------------------

INSERT IGNORE INTO SKRSViewMapping (register, datatype, version, tableName, createdDate)
  VALUES ('sks', 'institution', 1, 'Organisation', NOW());
INSERT IGNORE INTO SKRSColumns (viewMap, isPID, tableColumnName, feedColumnName, feedPosition, dataType, maxLength) VALUES
((SELECT idSKRSViewMapping FROM SKRSViewMapping WHERE register='sks' AND datatype='institution' AND version=1), 1, 'OrganisationPID',  NULL,                  0,  -5, NULL),
((SELECT idSKRSViewMapping FROM SKRSViewMapping WHERE register='sks' AND datatype='institution' AND version=1), 0, 'Navn',             'navn',                1,  12, NULL),
((SELECT idSKRSViewMapping FROM SKRSViewMapping WHERE register='sks' AND datatype='institution' AND version=1), 0, 'Nummer',           'nummer',              2,  12, NULL),
((SELECT idSKRSViewMapping FROM SKRSViewMapping WHERE register='sks' AND datatype='institution' AND version=1), 0, 'Organisationstype','organisationstype',   3,  12, NULL),
((SELECT idSKRSViewMapping FROM SKRSViewMapping WHERE register='sks' AND datatype='institution' AND version=1), 0, 'ModifiedDate',      NULL,                 0,  93, NULL),
((SELECT idSKRSViewMapping FROM SKRSViewMapping WHERE register='sks' AND datatype='institution' AND version=1), 0, 'ValidFrom',        'validFrom',           4,  93, NULL),
((SELECT idSKRSViewMapping FROM SKRSViewMapping WHERE register='sks' AND datatype='institution' AND version=1), 0, 'ValidTo',          'validTo',             5,  93, NULL);
