DROP TABLE holdings;
DROP TABLE CLIENT_TRADE;
DROP TABLE CLIENT_ORDER;
DROP TABLE CLIENT_PREFERENCES;
DROP TABLE CLIENT_IDENTIFICATION;
DROP TABLE CLIENT;

--Create Table Client
CREATE TABLE CLIENT (	
    client_id VARCHAR2(50),
    email VARCHAR2(50) UNIQUE NOT NULL,
    password VARCHAR2(255) NOT NULL,
    name VARCHAR2(255) NOT NULL,
    date_of_birth DATE NOT NULL,
    country VARCHAR2(50) NOT NULL,
    is_admin CHAR(1) NOT NULL,
    curr_balance NUMBER NOT NULL,
    CONSTRAINT pk_client_id PRIMARY KEY(client_id),
    CONSTRAINT chk_email_format CHECK (REGEXP_LIKE(email, '^[A-Za-z0-9\._%+-]+@[A-Za-z0-9\.-]+\.[A-Za-z]{2,}$')),
    CONSTRAINT chk_is_admin CHECK (is_admin IN ('Y', 'N'))
);
--Create Table Client Identification
CREATE TABLE CLIENT_IDENTIFICATION (
    client_id VARCHAR2(50),
    type VARCHAR2(255),
    value VARCHAR2(255),
    CONSTRAINT pk_client_id_type PRIMARY KEY(client_id,type),
    CONSTRAINT fk_client_id_identification FOREIGN KEY(client_id) REFERENCES CLIENT(client_id)
);

--Create table for Client Preferences
CREATE TABLE CLIENT_PREFERENCES (
    client_id VARCHAR2(50),
    investment_purpose VARCHAR2(30),
    income_category VARCHAR2(6),
    length_of_investment VARCHAR2(15),
    percentage_of_spend VARCHAR2(6),
    risk_tolerance NUMBER(2),
    is_advisor_accepted VARCHAR2(5),
    CONSTRAINT pk_client_id_type_preferences PRIMARY KEY(client_id),
    CONSTRAINT fk_client_id_preferences FOREIGN KEY(client_id) REFERENCES CLIENT(client_id),
    CONSTRAINT chk_is_advisor_accepted CHECK (is_advisor_accepted IN ('true', 'false'))
);

--Create holdings table
CREATE TABLE holdings (
    client_id VARCHAR(255) NOT NULL,
    instrument_id VARCHAR(255) NOT NULL,
    quantity INT NOT NULL,
    avg_price DECIMAL(19, 4) NOT NULL,
    PRIMARY KEY (client_id, instrument_id),
    FOREIGN KEY (client_id) REFERENCES client(client_id) ON DELETE CASCADE
);

--Create table ORDER
CREATE TABLE CLIENT_ORDER(
    instrument_id VARCHAR2(50) NOT NULL,
    quantity NUMBER NOT NULL,
    target_price DECIMAL(25,10) NOT NULL,
    direction CHAR(1) NOT NULL,
    client_id VARCHAR2(50),
    order_id VARCHAR2(50),
    token NUMBER NOT NULL,
    CONSTRAINT pk_order_id PRIMARY KEY(order_id),
    CONSTRAINT chk_direction CHECK (direction IN('S', 'B')),
    CONSTRAINT fk_order FOREIGN KEY(client_id) REFERENCES CLIENT(client_id) ON DELETE CASCADE
);
--Create table TRADE
CREATE TABLE CLIENT_TRADE (
    trade_id VARCHAR2(50),
    order_id VARCHAR2(50),
    execution_price DECIMAL(25,10) NOT NULL,
    cash_value DECIMAL(25,10) NOT NULL,
    executed_at TIMESTAMP NOT NULL,
    CONSTRAINT pk_trade_id PRIMARY KEY(trade_id),
--    FOREIGN KEY(order_id) REFERENCES CLIENT_ORDER(order_id) ON DELETE CASCADE
    CONSTRAINT fk_trade FOREIGN KEY(order_id) REFERENCES CLIENT_ORDER(order_id) ON DELETE CASCADE
);

COMMIT;