--Populate 5 Admin values and 1 non admin client value
--Initializing each client with $10k balance
INSERT INTO client (client_id, email, password, name, date_of_birth, country, is_admin, curr_balance) 
VALUES ('1654658069', 'sowmya@gmail.com', 'Marsh2024', 'Sowmya', to_date('2002-11-12', 'yyyy-mm-dd'), 'India', 'Y', 10000);
INSERT INTO client_identification (client_id, type, value)
VALUES ('1654658069','Aadhar','123456789102');
INSERT INTO client (client_id, email, password, name, date_of_birth, country, is_admin, curr_balance) 
VALUES ('541107416', 'himanshu@gmail.com', 'Marsh2024', 'Himanshu', to_date('2002-12-08', 'yyyy-mm-dd'), 'India', 'Y', 10000);
INSERT INTO client_identification (client_id, type, value)
VALUES ('541107416','Aadhar','154365349453');
INSERT INTO client (client_id, email, password, name, date_of_birth, country, is_admin, curr_balance) 
VALUES ('1425922638', 'rishiyanth@gmail.com', 'Marsh2024', 'Rishiyanth', to_date('2002-11-08', 'yyyy-mm-dd'), 'India', 'Y', 10000);
INSERT INTO client_identification (client_id, type, value)
VALUES ('1425922638','Aadhar','784304248242');
INSERT INTO client (client_id, email, password, name, date_of_birth, country, is_admin, curr_balance) 
VALUES ('1644724236', 'aditi@gmail.com', 'Marsh2024', 'Aditi', to_date('2002-09-28', 'yyyy-mm-dd'), 'India', 'Y', 10000);
INSERT INTO client_identification (client_id, type, value)
VALUES ('1644724236','Aadhar','759304541346');
INSERT INTO client (client_id, email, password, name, date_of_birth, country, is_admin, curr_balance) 
VALUES ('1236679496', 'mohith@gmail.com', 'Marsh2024', 'Mohith', to_date('2002-06-25', 'yyyy-mm-dd'), 'India', 'Y', 10000);
INSERT INTO client_identification (client_id, type, value)
VALUES ('1236679496','Aadhar','674902441257');
INSERT INTO client (client_id, email, password, name, date_of_birth, country, is_admin, curr_balance) 
VALUES ('739982664', 'john.doe@gmail.com', 'Marsh2024', 'John Doe', to_date('1998-03-15', 'yyyy-mm-dd'), 'USA', 'N', 10000);
INSERT INTO client_identification (client_id, type, value)
VALUES ('739982664','SSN','123457353');

--Inserting 6 records into Client Preferences table
INSERT INTO CLIENT_PREFERENCES (client_id ,investment_purpose, income_category, length_of_investment, percentage_of_spend, risk_tolerance, is_advisor_accepted) 
VALUES ( '1654658069', 'Education', 'HIG', 'Short', 'Tier4', 2, 'false');
INSERT INTO CLIENT_PREFERENCES (client_id ,investment_purpose, income_category, length_of_investment, percentage_of_spend, risk_tolerance, is_advisor_accepted) 
VALUES ( '541107416', 'Major Expense', 'LIG', 'Medium', 'Tier2', 1, 'true');
INSERT INTO CLIENT_PREFERENCES (client_id ,investment_purpose, income_category, length_of_investment, percentage_of_spend, risk_tolerance, is_advisor_accepted) 
VALUES ( '1425922638', 'Education', 'MIG', 'Short', 'Tier1', 5, 'true');
INSERT INTO CLIENT_PREFERENCES (client_id ,investment_purpose, income_category, length_of_investment, percentage_of_spend, risk_tolerance, is_advisor_accepted) 
VALUES ( '1644724236', 'Retirement', 'VHIG', 'Medium', 'Tier4', 3, 'false');
INSERT INTO CLIENT_PREFERENCES (client_id ,investment_purpose, income_category, length_of_investment, percentage_of_spend, risk_tolerance, is_advisor_accepted) 
VALUES ( '1236679496', 'Retirement', 'MIG', 'Long', 'Tier3', 3, 'true');
INSERT INTO CLIENT_PREFERENCES (client_id ,investment_purpose, income_category, length_of_investment, percentage_of_spend, risk_tolerance, is_advisor_accepted) 
VALUES ( '739982664', 'Retirement', 'LIG', 'Short', 'Tier4', 1, 'true');

--Insert values into ORDER table for client 541107416
INSERT INTO CLIENT_ORDER (instrument_id, quantity, target_price, direction, client_id, order_id, token) VALUES
('C100', 1000, 95.92, 'B', '541107416', 'ORDER001', 3);
INSERT INTO CLIENT_ORDER (instrument_id, quantity, target_price, direction, client_id, order_id, token) VALUES
('T67890', 10000, 1.03375, 'S', '541107416', 'ORDER002', 4);
INSERT INTO CLIENT_ORDER (instrument_id, quantity, target_price, direction, client_id, order_id, token) VALUES
('T67894', 50, 95.92, 'B', '541107416', 'ORDER003', 3);
INSERT INTO CLIENT_ORDER (instrument_id, quantity, target_price, direction, client_id, order_id, token) VALUES
('N123456', 20, 1.03375, 'B', '541107416', 'ORDER004', 4);

--Insert values into Trade table for client 541107416
INSERT INTO CLIENT_TRADE (trade_id, order_id, execution_price, cash_value, executed_at) VALUES
('TRADE001', 'ORDER001', 95.92, 95920.00, to_date('2024-09-22 21:31:04', 'yyyy-mm-dd hh24-mi-ss'));
INSERT INTO CLIENT_TRADE (trade_id, order_id, execution_price, cash_value, executed_at) VALUES
('TRADE002', 'ORDER002', 1.03375, 10337.50, to_date('2024-09-22 22:01:34', 'yyyy-mm-dd hh24-mi-ss'));
INSERT INTO CLIENT_TRADE (trade_id, order_id, execution_price, cash_value, executed_at) VALUES
('TRADE003', 'ORDER003', 96, 4800.00, to_date('2024-10-15 22:01:34', 'yyyy-mm-dd hh24-mi-ss'));
INSERT INTO CLIENT_TRADE (trade_id, order_id, execution_price, cash_value, executed_at) VALUES
('TRADE004', 'ORDER004', 1.035, 20.70, to_date('2024-10-16 12:12:44', 'yyyy-mm-dd hh24-mi-ss'));

-- Add holdings for client 541107416
INSERT INTO holdings (client_id, instrument_id, quantity, avg_price) VALUES
('541107416', 'C100', 1000, 95.67);  -- JPMorgan Chase Bank
INSERT INTO holdings (client_id, instrument_id, quantity, avg_price) VALUES
('541107416', 'T67890', 10, 1.033828125);  -- USA Note 3.125
INSERT INTO holdings (client_id, instrument_id, quantity, avg_price) VALUES
('541107416', 'T67894', 1000, 95.67);  -- JPMorgan Chase Bank
INSERT INTO holdings (client_id, instrument_id, quantity, avg_price) VALUES
('541107416', 'N123456', 10, 1.033828125);

COMMIT;