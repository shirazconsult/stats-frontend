CREATE TABLE `users` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user` varchar(32) COLLATE utf8_danish_ci NOT NULL,
  `pwd` varchar(32) COLLATE utf8_danish_ci NOT NULL,
  `app` varchar(32) COLLATE utf8_danish_ci NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE key `user` (`user`, `app`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

insert into users(user, pwd, app) values('admin', 'admin', 'mnm-amq');