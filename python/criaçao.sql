-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL,ALLOW_INVALID_DATES';

-- -----------------------------------------------------
-- Schema pedidos
-- -----------------------------------------------------

-- -----------------------------------------------------
-- Schema pedidos
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `pedidos` DEFAULT CHARACTER SET utf8 ;
USE `pedidos` ;

-- -----------------------------------------------------
-- Table `pedidos`.`listaenviar`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `pedidos`.`listaenviar` (
  `idListaEnviar` INT(11) NOT NULL AUTO_INCREMENT,
  `numPedido` INT(11) NOT NULL,
  `estado` INT(11) NOT NULL,
  PRIMARY KEY (`idListaEnviar`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `pedidos`.`listareceber`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `pedidos`.`listareceber` (
  `idListaReceber` INT(11) NOT NULL AUTO_INCREMENT,
  `numPedido` INT(11) NOT NULL,
  `estado` INT(11) NOT NULL,
  `relatorio` VARCHAR(1000) NOT NULL,
  PRIMARY KEY (`idListaReceber`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `pedidos`.`pedido`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `pedidos`.`pedido` (
  `numPedido` INT(11) NOT NULL AUTO_INCREMENT,
  `data` VARCHAR(20) NOT NULL,
  `hora` VARCHAR(20) NOT NULL,
  `numDoente` INT(11) NOT NULL,
  `numProcesso` INT(11) NOT NULL,
  `morada` VARCHAR(100) NOT NULL,
  `telefone` INT(11) NOT NULL,
  `numEpisodio` INT(11) NOT NULL,
  `tipoEpisodio` VARCHAR(45) NOT NULL,
  `infoClinica` VARCHAR(200) NULL DEFAULT NULL,
  `relatorio` VARCHAR(1000) NULL DEFAULT NULL,
  `estado` INT(11) NOT NULL,
  PRIMARY KEY (`numPedido`))
ENGINE = InnoDB
AUTO_INCREMENT = 2
DEFAULT CHARACTER SET = utf8;

USE `pedidos`;

DELIMITER $$
USE `pedidos`$$
CREATE
DEFINER=`root`@`localhost`
TRIGGER `pedidos`.`insertReceber`
AFTER INSERT ON `pedidos`.`listareceber`
FOR EACH ROW
begin
	update pedido set relatorio = new.relatorio where numPedido= new.numPedido;
end$$

USE `pedidos`$$
CREATE
DEFINER=`root`@`localhost`
TRIGGER `pedidos`.`insertEnviar`
AFTER INSERT ON `pedidos`.`pedido`
FOR EACH ROW
begin
	insert into listaenviar values(null, new.numPedido, new.estado);
end$$

DELIMITER ;

SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
