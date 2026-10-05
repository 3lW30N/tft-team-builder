create table item (
   id varchar(255) primary key,
   name varchar(255) not null unique,
   image_link varchar(255),
   component1_id varchar(255),
   component2_id varchar(255),
   constraint fk_item_component1
       foreign key (component1_id) references item(id),
   constraint fk_item_component2
       foreign key (component2_id) references item(id),
   constraint chk_item_component_order check (
       component1_id is null or component2_id is null or component1_id <= component2_id
   )
-- alphabetical order to avoid double items
);

create table trait (
   id varchar(255) primary key,
   name varchar(255) not null unique,
   image_link varchar(255)
);

create table unit (
   id serial primary key,
   name varchar(255) not null,
   image_link varchar(255)
);

create table unit_trait (
     unit_id integer not null,
     trait_id varchar(255) not null,
     constraint pk_unit_trait primary key (unit_id, trait_id),
     constraint fk_unit_trait_unit
         foreign key (unit_id) references unit(id),
     constraint fk_unit_trait_trait
         foreign key (trait_id) references trait(id)
);

create table build (
   id serial primary key,
    avg_placement float,
   unit_id integer not null,
    item1_id varchar,
    item2_id varchar,
    item3_id varchar,
    stars integer,
   constraint fk_build_unit
       foreign key (unit_id) references unit(id),
   constraint fk_item1_build
       foreign key (item1_id) references item(id),
    constraint fk_item2_build
       foreign key (item2_id) references item(id),
   constraint fk_item3_build
       foreign key (item3_id) references item(id),
   constraint chk_item_item_order check (
       (item1_id is null or item2_id is null or item1_id <= item2_id)
           and (item2_id is null or item3_id is null or item2_id <= item3_id)
           and (item1_id is null or item3_id is null or item1_id <= item3_id)
       )
);

create table comp (
   id serial primary key,
   name varchar(255),
   avg_placement float,
   win_rate float,
   difficulty float,
   levelling varchar(255),
   build_id integer,
   constraint fk_comp_build
       foreign key (build_id) references build(id)
);



