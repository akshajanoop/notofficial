INSERT INTO users (name, email, password_hash, city) VALUES
    ('Alice Smith', 'alice@example.com', 'password123', 'Mumbai'),
    ('Bob Jones',   'bob@example.com',   'password123', 'Delhi'),
    ('Administrator', 'admin@cinereserve.com', 'admin123', 'Mumbai');

INSERT INTO movies (title, genre, description, poster_url, rating, duration_mins, director, cast_members, crew_members) VALUES
    (
        'Practical Magic 2',
        'Fantasy-Romance',
        'The Owens sisters return as ancestral magic and a mysterious curse resurface, forcing a new generation of witches to unite.',
        'https://images.unsplash.com/photo-1518709268805-4e9042af9f23?auto=format&fit=crop&w=600&q=80',
        7.6,
        115,
        'Griffin Dunne',
        'Sandra Bullock, Nicole Kidman, Evan Rachel Wood, Stockard Channing, Dianne Wiest',
        'Alan Silvestri (Music), Andrew Dunn (Cinematography)'
    ),
    (
        'Spider-Man: Brand New Day',
        'Action-Sci-Fi',
        'Peter Parker navigates college life and a new era of street-level vigilante threats across New York City with unexpected allies.',
        'https://images.unsplash.com/photo-1563089145-599997674d42?auto=format&fit=crop&w=600&q=80',
        8.7,
        152,
        'Destin Daniel Cretton',
        'Tom Holland, Zendaya, Mark Ruffalo, Jon Bernthal, Sadie Sink',
        'Michael Giacchino (Music), Brett Pawlak (Cinematography)'
    ),
    (
        'The Odyssey',
        'Epic-Adventure',
        'An epic retelling of Odysseus'' perilous ten-year journey home after the Trojan War, facing mythological sea monsters and gods.',
        'https://images.unsplash.com/photo-1534447677768-be436bb09401?auto=format&fit=crop&w=600&q=80',
        8.9,
        168,
        'Christopher Nolan',
        'Cillian Murphy, Charlize Theron, Christian Bale, Tom Hardy',
        'Ludwig Göransson (Music), Hoyte van Hoytema (Cinematography)'
    ),
    (
        'Mirzapur: The Movie',
        'Crime-Action',
        'The battle for the throne of Purvanchal reaches its cinematic climax in an explosive, high-stakes theatrical war of power and revenge.',
        'https://images.unsplash.com/photo-1440404653325-ab127d49abc1?auto=format&fit=crop&w=600&q=80',
        8.4,
        142,
        'Gurmmeet Singh',
        'Pankaj Tripathi, Ali Fazal, Divyenndu Sharma, Shweta Tripathi',
        'John Stewart Eduri (Music), Sanjay Kapoor (Cinematography)'
    ),
    (
        'The Wild Robot',
        'Animation-Adventure',
        'After a shipwreck, an intelligent robot named Roz is stranded on an uninhabited island and learns to adapt by bonding with local wildlife.',
        'https://images.unsplash.com/photo-1579783902614-a3fb3927b675?auto=format&fit=crop&w=600&q=80',
        8.6,
        102,
        'Chris Sanders',
        'Lupita Nyong''o, Pedro Pascal, Kit Connor, Bill Nighy, Mark Hamill',
        'Kris Bowers (Music), Mary Blee (Editor)'
    ),
    (
        'Resident Evil: Extinction Point',
        'Sci-Fi-Horror',
        'A covert investigation team descends into a submerged Umbrella bio-weapons testing facility beneath the ocean floor.',
        'https://images.unsplash.com/photo-1509198397868-475647b2a1e5?auto=format&fit=crop&w=600&q=80',
        7.5,
        112,
        'Zach Cregger',
        'Kaya Scodelario, Robbie Amell, Hannah John-Kamen',
        'Charlie Clouser (Music), Maxime Alexandre (Cinematography)'
    );

INSERT INTO theatres (name, location, city, latitude, longitude) VALUES
    ('PVR INOX Juhu',       'Juhu Tara Road, Juhu, Mumbai 400049',                    'Mumbai', 19.1027, 72.8270),
    ('Cinepolis IMAX Malad', 'Infiniti Mall, Link Road, Malad West, Mumbai 400064',    'Mumbai', 19.1828, 72.8336),
    ('PVR Director''s Cut',  'Ambience Mall, Vasant Kunj, New Delhi 110070',           'Delhi',  28.5414, 77.1554),
    ('Cinepolis CP IMAX',    'Inner Circle, Connaught Place, New Delhi 110001',        'Delhi',  28.6304, 77.2177);

INSERT INTO shows (movie_id, theatre_id, show_time, show_date, total_seats) VALUES
    (1, 1, '18:00:00', DATEADD('DAY', 1, CURRENT_DATE), 30),
    (1, 2, '21:00:00', DATEADD('DAY', 2, CURRENT_DATE), 30),
    (2, 3, '14:00:00', DATEADD('DAY', 2, CURRENT_DATE), 40),
    (3, 4, '21:00:00', DATEADD('DAY', 3, CURRENT_DATE), 25),
    (4, 1, '10:00:00', DATEADD('DAY', 3, CURRENT_DATE), 50),
    (4, 3, '18:00:00', DATEADD('DAY', 4, CURRENT_DATE), 50),
    (5, 2, '14:00:00', DATEADD('DAY', 4, CURRENT_DATE), 35),
    (5, 4, '10:00:00', DATEADD('DAY', 5, CURRENT_DATE), 35),
    (6, 1, '21:00:00', DATEADD('DAY', 6, CURRENT_DATE), 30),
    (6, 2, '18:00:00', DATEADD('DAY', 7, CURRENT_DATE), 30);

INSERT INTO seats (show_id, seat_number, type, status, price, is_auction_eligible) VALUES
    (1, 'A1', 'STANDARD', 'AVAILABLE', 200.00, 0),
    (1, 'A2', 'STANDARD', 'AVAILABLE', 200.00, 0),
    (1, 'A3', 'STANDARD', 'AVAILABLE', 200.00, 0),
    (1, 'A4', 'STANDARD', 'AVAILABLE', 200.00, 0),
    (1, 'A5', 'STANDARD', 'AVAILABLE', 200.00, 0),
    (1, 'B1', 'STANDARD', 'AVAILABLE', 200.00, 0),
    (1, 'B2', 'STANDARD', 'AVAILABLE', 200.00, 0),
    (1, 'B3', 'STANDARD', 'AVAILABLE', 200.00, 0),
    (1, 'B4', 'STANDARD', 'AVAILABLE', 200.00, 0),
    (1, 'B5', 'STANDARD', 'AVAILABLE', 200.00, 0),
    (1, 'C1', 'STANDARD', 'AVAILABLE', 200.00, 0),
    (1, 'C2', 'STANDARD', 'AVAILABLE', 200.00, 0),
    (1, 'C3', 'STANDARD', 'AVAILABLE', 200.00, 0),
    (1, 'C4', 'STANDARD', 'AVAILABLE', 200.00, 0),
    (1, 'C5', 'STANDARD', 'AVAILABLE', 200.00, 0),
    (1, 'D1', 'PREMIUM', 'AVAILABLE', 350.00, 0),
    (1, 'D2', 'PREMIUM', 'AVAILABLE', 350.00, 0),
    (1, 'D3', 'PREMIUM', 'AVAILABLE', 350.00, 0),
    (1, 'D4', 'PREMIUM', 'AVAILABLE', 350.00, 0),
    (1, 'D5', 'PREMIUM', 'AVAILABLE', 350.00, 0),
    (1, 'E1', 'PREMIUM', 'AVAILABLE', 350.00, 0),
    (1, 'E2', 'PREMIUM', 'AVAILABLE', 350.00, 0),
    (1, 'E3', 'PREMIUM', 'AVAILABLE', 350.00, 0),
    (1, 'E4', 'PREMIUM', 'AVAILABLE', 350.00, 0),
    (1, 'E5', 'PREMIUM', 'AVAILABLE', 350.00, 0),
    (1, 'F1', 'RECLINER', 'AVAILABLE', 500.00, 1),
    (1, 'F2', 'RECLINER', 'AVAILABLE', 500.00, 1),
    (1, 'F3', 'RECLINER', 'AVAILABLE', 500.00, 0),
    (1, 'F4', 'RECLINER', 'AVAILABLE', 500.00, 0),
    (1, 'F5', 'RECLINER', 'AVAILABLE', 500.00, 0);

INSERT INTO bookings (user_id, show_id, total_amount, credits_used, status) VALUES
    (1, 1, 400.00, 0, 'CONFIRMED');

INSERT INTO booking_seats (booking_id, seat_id) VALUES
    (1, 1),
    (1, 2);

UPDATE seats SET status = 'RESERVED' WHERE seat_id IN (1, 2);

INSERT INTO auctions (seat_id, current_bid, highest_bidder_id, end_time, status) VALUES
    (26, 600.00, NULL, DATEADD('HOUR', 2, CURRENT_TIMESTAMP), 'ACTIVE');

INSERT INTO credit_transactions (user_id, transaction_type, amount, description, balance_after) VALUES
    (1, 'EARN', 50, 'Credits earned for confirmed booking #1 (Practical Magic 2, Show 1)', 50);

INSERT INTO user_credits (user_id, balance) VALUES
    (1, 50),
    (2,  0);
