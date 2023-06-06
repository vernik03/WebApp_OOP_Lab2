INSERT INTO users (id, name, is_admin, login, password) VALUES (1, 'Peter Parker', false, 'peter', 'PeTeR');
INSERT INTO users (id, name, is_admin, login, password) VALUES (2, 'Karl Black', false, 'mr_black', '123456789');
INSERT INTO users (id, name, is_admin, login, password) VALUES (3, 'Robert White', true, 'robert', 'admin');

INSERT INTO credit_cards (id, card_number)
VALUES (1, '5748567890123456');
INSERT INTO credit_cards (id, card_number)
VALUES (2, '5563767890167847');
INSERT INTO credit_cards (id, card_number)
VALUES (3, '3420127893456847');
INSERT INTO credit_cards (id, card_number)
VALUES (4, '7983767890167847');
INSERT INTO credit_cards (id, card_number)
VALUES (5, '6463700000167847');
INSERT INTO credit_cards (id, card_number)
VALUES (6, '2463427893467847');
INSERT INTO credit_cards (id, card_number)
VALUES (7, '5563767890167847');

INSERT INTO bank_accounts (fk_user_id, fk_credit_card_id, balance, is_blocked)
VALUES (1, 1, 1000, false);
INSERT INTO bank_accounts (fk_user_id, fk_credit_card_id, balance, is_blocked)
VALUES (2, 2, 2000, false);
INSERT INTO bank_accounts (fk_user_id, fk_credit_card_id, balance, is_blocked)
VALUES (3, 3, 3000, false);
INSERT INTO bank_accounts (fk_user_id, fk_credit_card_id, balance, is_blocked)
VALUES (3, 4, 4000, false);
INSERT INTO bank_accounts (fk_user_id, fk_credit_card_id, balance, is_blocked)
VALUES (2, 5, 1500, false);
INSERT INTO bank_accounts (fk_user_id, fk_credit_card_id, balance, is_blocked)
VALUES (1, 6, 300, false);
INSERT INTO bank_accounts (fk_user_id, fk_credit_card_id, balance, is_blocked)
VALUES (2, 7, 6700, false);

