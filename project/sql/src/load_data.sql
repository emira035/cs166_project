
COPY Users
<<<<<<< HEAD
FROM '/extra/emira035/cs166_project/project/data/users.csv'
=======
FROM '/extra/emira035/Phase3_Project/project/data/users.csv' 
>>>>>>> 317b909bce8423cb57574f002cc7e9ded6ef66d9
WITH DELIMITER ',' CSV HEADER;
ALTER SEQUENCE users_userID_seq RESTART 101; 

COPY Hotel
<<<<<<< HEAD
FROM '/extra/emira035/cs166_project/project/data/hotels.csv'
WITH DELIMITER ',' CSV HEADER;   

COPY Rooms
FROM '/extra/emira035/cs166_project/project/data/rooms.csv'
WITH DELIMITER ',' CSV HEADER;

COPY MaintenanceCompany
FROM '/extra/emira035/cs166_project/project/data/company.csv'
WITH DELIMITER ',' CSV HEADER;

COPY RoomBookings
FROM '/extra/emira035/cs166_project/project/data/bookings.csv'
=======
FROM '/extra/emira035/Phase3_Project/project/data/hotels.csv'
WITH DELIMITER ',' CSV HEADER;   

COPY Rooms
FROM '/extra/emira035/Phase3_Project/project/data/rooms.csv'
WITH DELIMITER ',' CSV HEADER;

COPY MaintenanceCompany
FROM '/extra/emira035/Phase3_Project/project/data/company.csv'
WITH DELIMITER ',' CSV HEADER;

COPY RoomBookings
FROM '/extra/emira035/Phase3_Project/project/data/bookings.csv'
>>>>>>> 317b909bce8423cb57574f002cc7e9ded6ef66d9
WITH DELIMITER ',' CSV HEADER;
ALTER SEQUENCE RoomBookings_bookingID_seq RESTART 501; 

COPY RoomRepairs
<<<<<<< HEAD
FROM '/extra/emira035/cs166_project/project/data/roomRepairs.csv'
=======
FROM '/extra/emira035/Phase3_Project/project/data/roomRepairs.csv'
>>>>>>> 317b909bce8423cb57574f002cc7e9ded6ef66d9
WITH DELIMITER ',' CSV HEADER;
ALTER SEQUENCE roomRepairs_repairID_seq RESTART 11;

COPY RoomRepairRequests
<<<<<<< HEAD
FROM '/extra/emira035/cs166_project/project/data/roomRepairRequests.csv'
=======
FROM '/extra/emira035/Phase3_Project/project/data/roomRepairRequests.csv'
>>>>>>> 317b909bce8423cb57574f002cc7e9ded6ef66d9
WITH DELIMITER ',' CSV HEADER;
ALTER SEQUENCE roomRepairRequests_requestNumber_seq RESTART 11;

COPY RoomUpdatesLog
<<<<<<< HEAD
FROM '/extra/emira035/cs166_project/project/data/roomUpdatesLog.csv'
=======
FROM '/extra/emira035/Phase3_Project/project/data/roomUpdatesLog.csv'
>>>>>>> 317b909bce8423cb57574f002cc7e9ded6ef66d9
WITH DELIMITER ',' CSV HEADER;
ALTER SEQUENCE roomUpdatesLog_updateNumber_seq RESTART 51;
