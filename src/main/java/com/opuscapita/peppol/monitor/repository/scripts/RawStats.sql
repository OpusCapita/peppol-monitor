CREATE TABLE `raw_stats` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `ap` varchar(35) NOT NULL,
  `tstamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `direction` enum('IN','OUT') DEFAULT NULL,
  `sender` varchar(35) NOT NULL,
  `receiver` varchar(35) NOT NULL,
  `doc_type` varchar(255) NOT NULL,
  `profile` varchar(255) DEFAULT NULL,
  `channel` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=latin1;