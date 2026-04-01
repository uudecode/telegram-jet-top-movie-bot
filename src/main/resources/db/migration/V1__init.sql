create table if not exists movie_messages (
                                              movie_id integer not null,
                                              message_id integer not null,
                                              primary key (movie_id, message_id)
    );

create table if not exists movie_polls (
                                           movie_id integer not null,
                                           user_id bigint not null,
                                           poll_rate integer not null,
                                           primary key (movie_id, user_id)
    );

create table if not exists will_view (
                                         user_id bigint not null,
                                         movie_id integer not null,
                                         primary key (user_id, movie_id)
    );

create index if not exists idx_will_view_movie_id on will_view(movie_id);