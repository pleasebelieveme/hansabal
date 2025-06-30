alter table trade add FULLTEXT(title);

alter table board add FULLTEXT(title);
alter table board add FULLTEXT(content);