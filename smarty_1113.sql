-- Smarty 프로젝트 DB
create database smartydb;
create user`smarty`@`%` identified by '1234'; 
grant all privileges on smarty.* to `smarty`@`%`;
select user, host from mysql.user;

use smartydb;
show tables;

-- 사용자----------------------------------------------------
-- 사용자 테이블
CREATE TABLE user (
    user_id VARCHAR(100) PRIMARY KEY NOT NULL,
    user_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL,
    password VARCHAR(100) NOT NULL,
    phone VARCHAR(100) NOT NULL,
    address VARCHAR(100) NOT NULL,
    birthday DATE NOT NULL,
    join_date datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
    login_date DATE NOT NULL,
    user_status BOOLEAN,
    qr_code BLOB,
    level VARCHAR(100)
);

-- 멤버십 테이블
CREATE TABLE membership (
    membership_id VARCHAR(100) PRIMARY KEY NOT NULL,
    membership_level VARCHAR(100) NOT NULL,
    user_id VARCHAR(100),
    FOREIGN KEY (user_id) REFERENCES user(user_id)
);

-- 로그인 히스토리 테이블
CREATE TABLE login_history (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id VARCHAR(100) NOT NULL,
    login_time DATETIME NOT NULL,
    logout_time DATETIME DEFAULT NULL,    
    login_status ENUM('SUCCESS', 'FAILURE') NOT NULL,
    ip_address VARCHAR(45) NOT NULL,
    user_agent VARCHAR(255),
    FOREIGN KEY (user_id) REFERENCES user(user_id)
);

-- 알림 테이블
CREATE TABLE notification (
    notification_id VARCHAR(100) PRIMARY KEY NOT NULL,
    user_id VARCHAR(100),
    message VARCHAR(300),
    send_date DATE,
    FOREIGN KEY (user_id) REFERENCES user(user_id)
);

-- 챗봇 테이블
CREATE TABLE chatbot (
    chat_room VARCHAR(100) PRIMARY KEY NOT NULL,
    user_id VARCHAR(100),
    question VARCHAR(300),
    answer VARCHAR(300),
    message TEXT,
    chatbot_status BOOLEAN,
    FOREIGN KEY (user_id) REFERENCES user(user_id)
);

-- 시설----------------------------------------------------
-- 시설 테이블
create table facility (
	facility_id varchar(100) primary key not null,
    facility_name varchar(100) not null,
    open_time time,
    close_time time,
    default_time int,
    basic_fee int,
    rate_adjustment float default 0,
    hot_time int default 0,
    contact varchar(100),
    info text,
    caution text,
    court boolean default false,
    product boolean default false,
    facility_status boolean default false,
    facility_images boolean default false
);

-- 시설 첨부파일 테이블
create table facility_attach (
	facility_id varchar(100) not null,
    origin_path varchar(1000) not null,
    thumbnail_path varchar(1000) not null,
	file_name varchar(1000) not null,
    foreign key (facility_id) references facility(facility_id)
);

-- 코트 테이블
create table court (	
	court_id varchar(100) primary key not null,
    facility_id varchar(100) not null,
    court_name varchar(100) not null,
    court_status boolean default false,
    foreign key (facility_id) references facility(facility_id)
);

-- 수업 테이블
CREATE TABLE class (
    class_id VARCHAR(100) PRIMARY KEY NOT NULL,
    facility_id VARCHAR(100),
    class_name VARCHAR(100) NOT NULL,
    start_date DATE,
    end_date DATE,
    day VARCHAR(50),
    start_time TIME,
    end_time TIME,
    price INT,
    FOREIGN KEY (facility_id) REFERENCES facility(facility_id)
);

-- 예약 테이블
CREATE TABLE reservation (
    reservation_id VARCHAR(100) PRIMARY KEY NOT NULL,
    user_id VARCHAR(100),
    court_id varchar(100),
    reservation_start DATETIME NOT NULL,
    reservation_end DATETIME NOT NULL,
    FOREIGN KEY (user_id) REFERENCES user(user_id),
	foreign key (court_id) references court(court_id)
);

-- 수강 신청 테이블
CREATE TABLE enrollment (
    enrollment_id VARCHAR(100) PRIMARY KEY NOT NULL,
    user_id VARCHAR(100),
    class_id VARCHAR(100),
    FOREIGN KEY (user_id) REFERENCES user(user_id),
    FOREIGN KEY (class_id) REFERENCES class(class_id)
);

-- 물품----------------------------------------------------
-- 물품 테이블
CREATE TABLE product (
    product_id varchar(100) primary key not null,
    facility_id VARCHAR(100),
    product_name VARCHAR(100) NOT NULL,
    price INT NOT NULL,  
    size VARCHAR(100),
    stock int,      
    product_images boolean default false,
    FOREIGN KEY (facility_id) REFERENCES facility(facility_id)
);

-- 물품 첨부파일 테이블
create table product_attach (
	product_id varchar(100) not null,
	origin_path varchar(1000) not null,
    thumbnail_path varchar(1000) not null,
	file_name varchar(1000) not null,
    foreign key (product_id) references product(product_id)
);

-- 대여 테이블
CREATE TABLE rental (
    rental_id VARCHAR(100) PRIMARY KEY NOT NULL,
    user_id VARCHAR(100),
    product_id VARCHAR(100),
    rental_date DATETIME NOT NULL,
    return_date DATETIME NOT NULL,
    FOREIGN KEY (user_id) REFERENCES user(user_id),
    FOREIGN KEY (product_id) REFERENCES product(product_id)
);

-- 대여 물품 관리 테이블 (관리자용)
CREATE TABLE product_status (
    status_id VARCHAR(100) PRIMARY KEY NOT NULL,
    product_id VARCHAR(100),        -- 품목 ID
    product_status ENUM('손상', '수리 필요', '재구매 필요', '대여 가능') NOT NULL, -- 상태
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP, -- 업데이트 시각
    FOREIGN KEY (product_id) REFERENCES product(product_id)
);

-- 복합----------------------------------------------------
-- 출결 테이블 (10/14 출석/지각/결석 출결상태 값 추가)
CREATE TABLE attendance (
    attendance_id VARCHAR(100) PRIMARY KEY NOT NULL,
    reservation_id VARCHAR(100),
    enrollment_id VARCHAR(100),
    attendance_status ENUM('Present', 'Late', 'Absent'),  -- 출결 상태
    checkin_date DATETIME NOT NULL,
    checkout_date DATETIME NOT NULL,
    FOREIGN KEY (reservation_id) REFERENCES reservation(reservation_id),
    FOREIGN KEY (enrollment_id) REFERENCES enrollment(enrollment_id)
);

-- 결제 테이블
CREATE TABLE payment (
    payment_id VARCHAR(100) PRIMARY KEY NOT NULL,
    reservation_id VARCHAR(100),
    enrollment_id VARCHAR(100),
    rental_id varchar(100), 
    amount FLOAT NOT NULL,
    payment_date DATE NOT NULL,
    FOREIGN KEY (reservation_id) REFERENCES reservation(reservation_id),
	foreign key(rental_id) references rental(rental_id),
    FOREIGN KEY (enrollment_id) REFERENCES enrollment(enrollment_id)
);

