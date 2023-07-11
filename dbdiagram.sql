CREATE TABLE `friends` (
  `user_id` integer PRIMARY KEY,
  `friend_id` integer,
  `confirm` boolean
);

CREATE TABLE `users` (
  `id` integer PRIMARY KEY,
  `name` varchar(150),
  `email` varchar(150),
  `login` varchar(150),
  `birthday` localdate
);

CREATE TABLE `films` (
  `id` integer PRIMARY KEY,
  `name` varchar(300),
  `description` varchar(1000),
  `duration` long,
  `raiting` integer,
  `likes` integer,
  `release_date` localdate
);

CREATE TABLE `likes` (
  `film_id` integer PRIMARY KEY,
  `user_id` integer
);

CREATE TABLE `genres` (
  `id` integer PRIMARY KEY,
  `name` varchar(100)
);

CREATE TABLE `mpa` (
  `id` integer PRIMARY KEY,
  `name` varchar(7)
);

CREATE TABLE `films_genres` (
  `film_id` integer,
  `genre_id` integer,
  PRIMARY KEY (`film_id`, `genre_id`)
);

ALTER TABLE `friends` ADD FOREIGN KEY (`friend_id`) REFERENCES `users` (`id`);

ALTER TABLE `friends` ADD FOREIGN KEY (`user_id`) REFERENCES `users` (`id`);

ALTER TABLE `likes` ADD FOREIGN KEY (`user_id`) REFERENCES `users` (`id`);

ALTER TABLE `likes` ADD FOREIGN KEY (`film_id`) REFERENCES `films` (`id`);

ALTER TABLE `films_genres` ADD FOREIGN KEY (`genre_id`) REFERENCES `genres` (`id`);

ALTER TABLE `films_genres` ADD FOREIGN KEY (`film_id`) REFERENCES `films` (`id`);

ALTER TABLE `mpa` ADD FOREIGN KEY (`id`) REFERENCES `films` (`raiting`);
