INSERT INTO users (id, name, is_admin, login, password) VALUES (1, 'Peter Parker', false, 'peter', 'PeTeR');
INSERT INTO users (id, name, is_admin, login, password) VALUES (2, 'Karl Black', false, 'mr_black', '123456789');
INSERT INTO users (id, name, is_admin, login, password) VALUES (3, 'Robert White', true, 'robert', 'admin');

INSERT INTO bank_accounts (id, card_number, balance, is_blocked, fk_user_id)
VALUES (1, '5748567890123456', 1000, false, 1);
INSERT INTO bank_accounts (id, card_number, balance, is_blocked, fk_user_id)
VALUES (2, '5563767890167847', 2000, false, 3);
INSERT INTO bank_accounts (id, card_number, balance, is_blocked, fk_user_id)
VALUES (3, '3420127893456847', 3000, false, 2);
INSERT INTO bank_accounts (id, card_number, balance, is_blocked, fk_user_id)
VALUES (4, '7983767890167847', 4000, false, 2);
INSERT INTO bank_accounts (id, card_number, balance, is_blocked, fk_user_id)
VALUES (5, '6463700000167847', 1500, false, 1);
INSERT INTO bank_accounts (id, card_number,  balance, is_blocked, fk_user_id)
VALUES (6, '2463427893467847', 300, false, 3);
INSERT INTO bank_accounts (id, card_number, balance, is_blocked, fk_user_id)
VALUES (7, '5563767890167847', 6700, false, 1);


