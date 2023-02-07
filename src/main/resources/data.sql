-- phpMyAdmin SQL Dump
-- version 5.1.0
-- https://www.phpmyadmin.net/
--
-- Hôte : localhost:8889
-- Généré le : mar. 07 fév. 2023 à 11:19
-- Version du serveur :  5.7.32
-- Version de PHP : 7.4.16

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de données : `gdc`
--

-- --------------------------------------------------------

--
-- Structure de la table `user`
--

CREATE TABLE `user` (
  `dtype` varchar(31) NOT NULL,
  `id` bigint(20) NOT NULL,
  `email` varchar(50) NOT NULL,
  `first_name` varchar(50) NOT NULL,
  `is_admin` bit(1) NOT NULL,
  `last_name` varchar(50) NOT NULL,
  `password` varchar(50) NOT NULL,
  `rtt` int(11) NOT NULL,
  `vacations_avalaible` int(11) NOT NULL,
  `department` varchar(50) NOT NULL,
  `id_manager` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Déchargement des données de la table `user`
--

INSERT INTO `user` (`dtype`, `id`, `email`, `first_name`, `is_admin`, `last_name`, `password`, `rtt`, `vacations_avalaible`, `department`, `id_manager`) VALUES
('', 1, 'karon_miner44@outlook.com', 'Karon', b'1', 'Miner', 'DFsCRHaA3u', 5, 12, '', NULL),
('', 2, 'bennie_pratt13@aol.com', 'Bennie', b'0', 'Pratt', '7SwMxLApGp', 4, 2, 'Informatique', NULL),
('', 3, 'sam-woods93@gmail.com', 'Sam', b'0', 'Woods', '5zpH3ya8eq', 3, 0, 'Managment', NULL),
('', 4, 'bennie_pratt13@aol.com', 'Bennie', b'0', 'Pratt', '7SwMxLApGp', 4, 2, '', 2),
('', 5, 'marcie_rouse40@gmail.com', 'Marcie', b'0', 'Rouse', 'adqDtc9Mdz', 5, 5, '', 2),
('', 6, 'blanco_daniel80@yahoo.com', 'Daniel', b'0', 'Blanco', 'adqDtc9Mdz', 2, 1, '', 3);

--
-- Index pour les tables déchargées
--

--
-- Index pour la table `user`
--
ALTER TABLE `user`
  ADD PRIMARY KEY (`id`),
  ADD KEY `FKdmyn33mfb0rxhxy6uhni98cb` (`id_manager`);

--
-- AUTO_INCREMENT pour les tables déchargées
--

--
-- AUTO_INCREMENT pour la table `user`
--
ALTER TABLE `user`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- Contraintes pour les tables déchargées
--

--
-- Contraintes pour la table `user`
--
ALTER TABLE `user`
  ADD CONSTRAINT `FKdmyn33mfb0rxhxy6uhni98cb` FOREIGN KEY (`id_manager`) REFERENCES `user` (`id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;

INSERT INTO `employer_rtt` (`id`, `rtt_available`) VALUES
(1, '7');