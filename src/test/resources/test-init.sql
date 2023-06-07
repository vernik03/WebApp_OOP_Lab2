INSERT INTO users (id, name, is_admin, login, password) VALUES (1, 'Peter Parker', false, 'peter', 'PeTeR');
INSERT INTO users (id, name, is_admin, login, password) VALUES (2, 'Karl Black', false, 'mr_black', '123456789');
INSERT INTO users (id, name, is_admin, login, password) VALUES (3, 'Robert White', true, 'robert', 'admin');
ALTER TABLE users ALTER COLUMN id RESTART WITH 4;

INSERT INTO bank_accounts (id, card_number, balance, is_blocked)
VALUES (1, '5748567890123456', 1000, false);
INSERT INTO bank_accounts (id, card_number, balance, is_blocked)
VALUES (2, '5563767890167847', 2000, false);
INSERT INTO bank_accounts (id, card_number, balance, is_blocked)
VALUES (3, '3420127893456847', 3000, false);
INSERT INTO bank_accounts (id, card_number, balance, is_blocked)
VALUES (4, '7983767890167847', 4000, false);
INSERT INTO bank_accounts (id, card_number, balance, is_blocked)
VALUES (5, '6463700000167847', 1500, false);
INSERT INTO bank_accounts (id, card_number,  balance, is_blocked)
VALUES (6, '2463427893467847', 300, false);
INSERT INTO bank_accounts (id, card_number, balance, is_blocked)
VALUES (7, '5563767890167847', 6700, false);

ALTER TABLE credit_cards ALTER COLUMN id RESTART WITH 8;

INSERT INTO credit_cards (fk_user_id, fk_bank_account_id)
VALUES (1, 1);
INSERT INTO credit_cards (fk_user_id, fk_bank_account_id)
VALUES (2, 2);
INSERT INTO credit_cards (fk_user_id, fk_bank_account_id)
VALUES (3, 3);
INSERT INTO credit_cards (fk_user_id, fk_bank_account_id)
VALUES (3, 4);
INSERT INTO credit_cards (fk_user_id, fk_bank_account_id)
VALUES (2, 5);
INSERT INTO credit_cards (fk_user_id, fk_bank_account_id)
VALUES (1, 6);
INSERT INTO credit_cards (fk_user_id, fk_bank_account_id)
VALUES (2, 7);

