CREATE TABLE BRANDS
(
    brand_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name     VARCHAR(255) NOT NULL,
    created_at  TIMESTAMP      NOT NULL,
    updated_at TIMESTAMP
);

CREATE TABLE PRODUCTS
(
    product_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name       VARCHAR(255) NOT NULL,
    created_at  TIMESTAMP      NOT NULL,
    updated_at TIMESTAMP
);

CREATE TABLE PRICES
(
    price_list BIGINT AUTO_INCREMENT PRIMARY KEY,
    brand_id   BIGINT         NOT NULL,
    product_id BIGINT         NOT NULL,
    start_date TIMESTAMP      NOT NULL,
    end_date   TIMESTAMP      NOT NULL,
    priority   INT            NOT NULL,
    price      DECIMAL(10, 2) NOT NULL,
    curr       VARCHAR(3)     NOT NULL,
    created_at  TIMESTAMP      NOT NULL,
    updated_at TIMESTAMP,
    CONSTRAINT fk_brand FOREIGN KEY (brand_id) REFERENCES BRANDS (brand_id),
    CONSTRAINT fk_product FOREIGN KEY (product_id) REFERENCES PRODUCTS (product_id)
);
