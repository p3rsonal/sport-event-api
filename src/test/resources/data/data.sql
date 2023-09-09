-- Delete all entries from the event table
DELETE FROM events;

-- Football events
INSERT INTO events (name, sport, status, start_time) VALUES ('Premier League Match 1', 'FOOTBALL', 'INACTIVE', '2024-09-07T10:00:00');
INSERT INTO events (name, sport, status, start_time) VALUES ('Premier League Match 2', 'FOOTBALL', 'ACTIVE', '2024-09-08T15:00:00');
INSERT INTO events (name, sport, status, start_time) VALUES ('Premier League Match 3', 'FOOTBALL', 'FINISHED', '2024-09-06T18:00:00');
INSERT INTO events (name, sport, status, start_time) VALUES ('UEFA Champions League Match', 'FOOTBALL', 'INACTIVE', '2024-09-10T20:00:00');

-- Hockey events
INSERT INTO events (name, sport, status, start_time) VALUES ('NHL Match 1', 'HOCKEY', 'ACTIVE', '2024-09-07T17:00:00');
INSERT INTO events (name, sport, status, start_time) VALUES ('NHL Match 2', 'HOCKEY', 'FINISHED', '2024-09-06T19:00:00');
INSERT INTO events (name, sport, status, start_time) VALUES ('NHL Playoff Match', 'HOCKEY', 'INACTIVE', '2024-09-09T21:00:00');

-- Tennis events
INSERT INTO events (name, sport, status, start_time) VALUES ('US Open Match 1', 'TENNIS', 'INACTIVE', '2024-09-07T11:00:00');
INSERT INTO events (name, sport, status, start_time) VALUES ('US Open Match 2', 'TENNIS', 'ACTIVE', '2024-09-08T16:00:00');
INSERT INTO events (name, sport, status, start_time) VALUES ('Wimbledon Final', 'TENNIS', 'FINISHED', '2024-09-06T13:00:00');

-- Basketball events
INSERT INTO events (name, sport, status, start_time) VALUES ('NBA Match 1', 'BASKETBALL', 'ACTIVE', '2024-09-07T14:00:00');
INSERT INTO events (name, sport, status, start_time) VALUES ('NBA Match 2', 'BASKETBALL', 'FINISHED', '2024-09-06T20:00:00');
INSERT INTO events (name, sport, status, start_time) VALUES ('NBA Playoff Match', 'BASKETBALL', 'INACTIVE', '2024-09-09T15:00:00');
