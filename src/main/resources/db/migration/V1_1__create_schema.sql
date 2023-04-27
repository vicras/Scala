CREATE TABLE person
(
    id            VARCHAR(100) NOT NULL,
    created_at    TIMESTAMP WITHOUT TIME ZONE,
    updated_at    TIMESTAMP WITHOUT TIME ZONE,
    entity_status VARCHAR(255),
    first_name    VARCHAR(255),
    last_name     VARCHAR(255),
    patronymic    VARCHAR(255),
    telegram_id   BIGINT       NOT NULL,
    birth_date    date,
    telephone     BIGINT,
    mail          VARCHAR(255),
    home_address  VARCHAR(255),
    password      VARCHAR(255),
    language      VARCHAR(255) NOT NULL,
    state         VARCHAR(255),
    CONSTRAINT pk_person PRIMARY KEY (id)
);

INSERT INTO public.person (id, created_at, updated_at, entity_status, first_name, last_name, patronymic, telegram_id,
                           birth_date, telephone, password, mail, home_address, language, state)
VALUES ('37c98fa4-2fa1-4669-8e60-a68e9a2b69cc', '2023-04-01 01:07:27.000000', '2023-04-01 01:07:27.000000', null, 'Viktar', 'Graskov', 'Ivanovich', 1234567, '2000-11-30', 375297473331,
        '$2y$10$S8ObXp.D9LDbtIjfzj2Lwee7UO0BrOSbw9ZEBQvIwLQOXkR9npYRe','viktar.graskov@gmail.com', '-', 'RU', null);

