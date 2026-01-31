INSERT INTO sankou_book_color (id, name)
VALUES
  ('SC001', 'BRONZE'),
  ('SC002', 'SILVER'),
  ('SC003', 'GOLD')
ON CONFLICT (id) DO NOTHING;
@@
