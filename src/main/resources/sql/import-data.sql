delete from word_temp where word_order is null;

update word_temp set position = 'd5p11s9' WHERE position = 'd5011s9';

update word_temp
set div_id = substr(position, strpos(position, 'd') + 1, strpos(position, 'p') - strpos(position, 'd') - 1)::int,
    paragraph_id = substr(position, strpos(position, 'p') + 1, strpos(position, 's') - strpos(position, 'p') - 1)::int,
    sentence_id = substr(position, strpos(position, 's') + 1, length(position) - strpos(position, 's'))::int
where true;

truncate table user_paragraph;
truncate table word;
truncate table amr_word;
truncate table amr_tree;

insert into word(div_id, paragraph_id, sentence_id, word_order, content, pos_label)
select div_id, paragraph_id, sentence_id, word_order, content, pos_label from word_temp;

update word SET pos_label = 'Det' WHERE pos_label = 'DET';
update word SET pos_label = 'Num' WHERE pos_label = 'NUM';
update word SET pos_label = 'Adj' WHERE pos_label = 'ADJ';
update word SET pos_label = 'Nu' WHERE pos_label = 'NU';
update word SET pos_label = 'Punct' WHERE pos_label = 'PUNCT';
update word SET pos_label = 'Pro' WHERE pos_label = 'PRO';
update word SET pos_label = 'Part' WHERE pos_label = 'PART';
update word SET pos_label = 'Pre' WHERE pos_label = 'PRE';
update word SET pos_label = 'Adv' WHERE pos_label = 'ADV';
update word SET pos_label = 'Nc' WHERE pos_label = 'NC';
update word SET pos_label = 'Prt' WHERE pos_label = 'PRT';
update word SET pos_label = 'Aux' WHERE pos_label = 'aux';

------------------------------------------------------------

truncate table amr_word;
truncate table amr_tree;

delete from word_temp where position like 'd1p%';
delete from user_paragraph where div_id = 1;
delete from word where div_id = 1;

insert into amr_tree(sentence_position)
select distinct concat(div_id, '/', paragraph_id, '/', sentence_id) from word_temp;

update word_temp w
set tree_id = t.id
    from amr_tree t
where concat(w.div_id, '/', w.paragraph_id, '/', w.sentence_id) = t.sentence_position;

update word_temp w
set word_id = t.id
    from word t
where t.div_id = w.div_id and t.paragraph_id = w.paragraph_id and t.sentence_id = w.sentence_id and t.word_order = w.word_order;

update word_temp w
set parent_id = t.id
    from word t
where t.div_id = w.div_id and t.paragraph_id = w.paragraph_id and t.sentence_id = w.sentence_id and t.word_order = w.parent_order;

update word_temp w
set amr_label_id = l.id
    from amr_label l
where upper(w.amr_label) = upper(l.name);

insert into amr_word(tree_id, word_id, parent_id, amr_label_id)
select tree_id, word_id, parent_id, amr_label_id
from word_temp where parent_order is not null or amr_label is not null;

insert into user_paragraph(user_id, div_id, paragraph_id)
select distinct 1, div_id, paragraph_id from word;