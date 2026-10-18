-- =============================================================================
-- Movie Ticket Booking System — Database Schema
-- =============================================================================

CREATE TABLE IF NOT EXISTS users (
    user_id       INT            NOT NULL AUTO_INCREMENT,
    name          VARCHAR(100)   NOT NULL,
    email         VARCHAR(100)   NOT NULL,
    password_hash VARCHAR(255)   NOT NULL,
    city          VARCHAR(50)    DEFAULT NULL,
    created_at    TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (user_id),
    UNIQUE KEY uq_users_email (email)
);

CREATE TABLE IF NOT EXISTS movies (
    movie_id      INT              NOT NULL AUTO_INCREMENT,
    title         VARCHAR(200)     NOT NULL,
    genre         VARCHAR(50)      DEFAULT NULL,
    description   TEXT             DEFAULT NULL,
    poster_url    VARCHAR(500)     DEFAULT NULL,
    rating        DECIMAL(2,1)     DEFAULT NULL,
    duration_mins INT              DEFAULT NULL,
    director      VARCHAR(100)     DEFAULT NULL,
    cast_members  VARCHAR(300)     DEFAULT NULL,
    crew_members  VARCHAR(300)     DEFAULT NULL,
    PRIMARY KEY (movie_id)
);

CREATE TABLE IF NOT EXISTS theatres (
    theatre_id  INT            NOT NULL AUTO_INCREMENT,
    name        VARCHAR(200)   NOT NULL,
    location    VARCHAR(300)   DEFAULT NULL,
    city        VARCHAR(50)    DEFAULT NULL,
    latitude    DECIMAL(10,6)  DEFAULT 19.0760,
    longitude   DECIMAL(10,6)  DEFAULT 72.8777,
    PRIMARY KEY (theatre_id)
);

CREATE TABLE IF NOT EXISTS shows (
    show_id      INT  NOT NULL AUTO_INCREMENT,
    movie_id     INT  NOT NULL,
    theatre_id   INT  NOT NULL,
    show_time    TIME NOT NULL,
    show_date    DATE NOT NULL,
    total_seats  INT  NOT NULL DEFAULT 0,
    PRIMARY KEY (show_id),
    CONSTRAINT fk_shows_movie   FOREIGN KEY (movie_id)   REFERENCES movies   (movie_id)   ON DELETE CASCADE,
    CONSTRAINT fk_shows_theatre FOREIGN KEY (theatre_id) REFERENCES theatres (theatre_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS seats (
    seat_id            INT                                    NOT NULL AUTO_INCREMENT,
    show_id            INT                                    NOT NULL,
    seat_number        VARCHAR(10)                            NOT NULL,
    type               ENUM('STANDARD','PREMIUM','RECLINER')  NOT NULL DEFAULT 'STANDARD',
    status             ENUM('AVAILABLE','LOCKED','RESERVED')  NOT NULL DEFAULT 'AVAILABLE',
    price              DECIMAL(10,2)                          NOT NULL,
    is_auction_eligible TINYINT(1)                            NOT NULL DEFAULT 0,
    PRIMARY KEY (seat_id),
    CONSTRAINT fk_seats_show FOREIGN KEY (show_id) REFERENCES shows (show_id) ON DELETE CASCADE,
    INDEX idx_seats_show_id (show_id)
);

CREATE TABLE IF NOT EXISTS bookings (
    booking_id   INT                                        NOT NULL AUTO_INCREMENT,
    user_id      INT                                        NOT NULL,
    show_id      INT                                        NOT NULL,
    total_amount DECIMAL(10,2)                              NOT NULL DEFAULT 0.00,
    credits_used INT                                        NOT NULL DEFAULT 0,
    status       ENUM('PENDING','CONFIRMED','CANCELLED')    NOT NULL DEFAULT 'PENDING',
    created_at   TIMESTAMP                                  NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (booking_id),
    CONSTRAINT fk_bookings_user FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE RESTRICT,
    CONSTRAINT fk_bookings_show FOREIGN KEY (show_id) REFERENCES shows (show_id) ON DELETE RESTRICT,
    INDEX idx_bookings_user_id (user_id)
);

CREATE TABLE IF NOT EXISTS booking_seats (
    booking_id INT NOT NULL,
    seat_id    INT NOT NULL,
    PRIMARY KEY (booking_id, seat_id),
    CONSTRAINT fk_bseats_booking FOREIGN KEY (booking_id) REFERENCES bookings (booking_id) ON DELETE CASCADE,
    CONSTRAINT fk_bseats_seat    FOREIGN KEY (seat_id)    REFERENCES seats    (seat_id)    ON DELETE RESTRICT
);

CREATE TABLE IF NOT EXISTS auctions (
    auction_id         INT                                  NOT NULL AUTO_INCREMENT,
    seat_id            INT                                  NOT NULL,
    current_bid        DECIMAL(10,2)                        NOT NULL DEFAULT 0.00,
    highest_bidder_id  INT                                  DEFAULT NULL,
    end_time           TIMESTAMP                            NOT NULL,
    status             ENUM('ACTIVE','RESOLVED','EXPIRED')  NOT NULL DEFAULT 'ACTIVE',
    PRIMARY KEY (auction_id),
    CONSTRAINT fk_auctions_seat    FOREIGN KEY (seat_id)           REFERENCES seats  (seat_id)  ON DELETE CASCADE,
    CONSTRAINT fk_auctions_bidder  FOREIGN KEY (highest_bidder_id) REFERENCES users  (user_id)  ON DELETE SET NULL,
    INDEX idx_auctions_seat_id (seat_id)
);

CREATE TABLE IF NOT EXISTS credit_transactions (
    transaction_id   INT                       NOT NULL AUTO_INCREMENT,
    user_id          INT                       NOT NULL,
    transaction_type ENUM('EARN','REDEEM')     NOT NULL,
    amount           INT                       NOT NULL,
    description      VARCHAR(255)              DEFAULT NULL,
    balance_after    INT                       NOT NULL,
    created_at       TIMESTAMP                 NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (transaction_id),
    CONSTRAINT fk_ctxn_user FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS user_credits (
    user_id INT NOT NULL,
    balance INT NOT NULL DEFAULT 0,
    PRIMARY KEY (user_id),
    CONSTRAINT fk_ucredits_user FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE CASCADE
);
