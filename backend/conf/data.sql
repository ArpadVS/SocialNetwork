

# When schema is created, run these commands on DataBase to fill tables
# test1@ pw is 'test'   test2 user1-2 pw is 'user'
insert into socialnetwork.users (id, email, password, first_name, last_name, picture, registration_date) values
(1, 'user1@gmail.com', '$2a$10$2mKCyFbGFRg412ZezMOIlOTF1utPomvc8VCGVKpV07HmzLlvyUZEO', 'Pera', 'Peric', 'user_1.png' ,'2021-10-20'),
(2, 'user2@gmail.com', '$2a$10$4huww6FKWYbkyO.bUxdKx.9f8PMioY5DCkoIT0WKoyXOcmbWCzqOy', 'Milica', 'Varga', 'user_2.png', '2021-10-20'),
(3, 'test1@gmail.com', '$2a$10$ymcNQnpYNH3.r1YVsKD0m.WjxAtfjsjWpb8oHboG6yb18B9I31PF.', 'Mika', 'Mikic', 'default.png', '2021-10-20'),
(4, 'test2@gmail.com', '$2a$10$.YBCxyxc/0/jrStpIusYde7Evef30uGeUQFcqkgNQXNOd3gWOespu', 'Leo', 'Messi', 'user_4.png', '2021-10-24');


insert into socialnetwork.posts ( id, user_id, text, likes, created) values
(1, 1, 'This is a post1', 2, '2021-10-22'),
(2, 1, 'This is a post2', 1, '2021-10-21'),
(3, 1, 'This is a post3', 1, '2021-10-20'),
(4, 2, 'This is a post4', 0, '2021-10-23'),
(5, 3, 'This is a post5', 0, '2021-10-22'),
(6, 3, 'This is a post6', 0, '2021-10-22'),
(7, 3, 'This is a post7', 1, '2021-10-20'),
(8, 3, 'This is a post8', 0, '2021-10-25'),
(9, 3, 'This is a post9', 0, '2021-10-26'),
(10, 4, 'Spamming Spamm', 0, '2021-10-26'),
(11, 4, 'Spamming again', 0, '2021-10-29');

insert into socialnetwork.likes ( id, user_id, post_id) values
(1, 1, 1),
(2, 1, 7),
(3, 2, 1),
(4, 2, 2),
(5, 2, 3);

insert into socialnetwork.friendships ( id, user1_id, user2_id) values
(1, 1, 2),
(2, 1, 3),
(3, 2, 4);

insert into socialnetwork.friend_requests ( id, sender_id, receiver_id) values
(1, 1, 4);
