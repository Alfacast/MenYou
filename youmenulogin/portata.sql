-- phpMyAdmin SQL Dump
-- version 3.4.7.1
-- http://www.phpmyadmin.net
--
-- Host: 62.149.150.222
-- Generato il: Giu 02, 2016 alle 19:18
-- Versione del server: 5.5.47
-- Versione PHP: 5.3.8

SET SQL_MODE="NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- Database: `Sql787643_2`
--

-- --------------------------------------------------------

--
-- Struttura della tabella `portata`
--

CREATE TABLE IF NOT EXISTS `portata` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `unique_id` varchar(23) NOT NULL,
  `nome` varchar(50) NOT NULL,
  `categoria` varchar(50) NOT NULL,
  `descrizione` varchar(100) NOT NULL,
  `prezzo` varchar(100) NOT NULL,
  `opzioni` varchar(100) NOT NULL,
  `disponibile` varchar(10) NOT NULL,
  `foto` varchar(10) NOT NULL,
  `created_at` datetime NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=8 ;

--
-- Dump dei dati per la tabella `portata`
--

INSERT INTO `portata` (`id`, `unique_id`, `nome`, `categoria`, `descrizione`, `prezzo`, `opzioni`, `disponibile`, `foto`, `created_at`) VALUES
(6, '575004ecb6e953.64390579', 'Primi', 'Cannelloni', 'Desc1', '10', 'Nessuna', 'Si', 'No', '2016-06-02 12:05:32'),
(7, '575026c5859201.97328570', 'Gelato', 'Dolci', 'Gelatoso', '2', 'Con panna', 'Si', 'No', '2016-06-02 14:29:56');

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
