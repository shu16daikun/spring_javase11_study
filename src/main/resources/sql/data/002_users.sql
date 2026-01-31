INSERT INTO users (id, username, password_encode, authority_id, is_first_login, reset_password)
VALUES
  ('US0001', 'JavaStudyAdmin', '$2a$08$hlbqvXkXId77o29MEnk9peZvB.JENII1N6I79Ly0K9h0Z8pvYN9Ny', 'AU0001', true, false),
  ('US0002', 'JavaStudyUser',  '$2a$08$hlbqvXkXId77o29MEnk9peZvB.JENII1N6I79Ly0K9h0Z8pvYN9Ny', 'AU0002', true, false)
ON CONFLICT (id) DO NOTHING;
@@
