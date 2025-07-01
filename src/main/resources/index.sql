alter table trade add FULLTEXT(title);

alter table boards add FULLTEXT(title);
alter table boards add FULLTEXT(content);