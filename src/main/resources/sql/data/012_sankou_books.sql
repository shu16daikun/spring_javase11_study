INSERT INTO sankou_books (id, name, color_id)
VALUES
  ('SA00001', '徹底攻略Java SE Bronze問題集[1Z0-818]対応', 'SC001'),
  ('SA00002', '徹底攻略Java SE 11 Silver問題集[1Z0-815]対応', 'SC002'),
  ('SA00003', '徹底攻略Java SE 11 Gold問題集[1Z0-816]対応',  'SC003')
ON CONFLICT (id) DO NOTHING;
@@
