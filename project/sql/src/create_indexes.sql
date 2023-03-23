DROP INDEX IF EXISTS user_userid_index;
DROP INDEX IF EXISTS user_name_index;
DROP INDEX IF EXISTS user_password_index;
DROP INDEX IF EXISTS user_usertype_index;

DROP INDEX IF EXISTS hotel_hotelid_index;
DROP INDEX IF EXISTS hotel_hotelname_index;
DROP INDEX IF EXISTS hotel_hotellatitude_index;
DROP INDEX IF EXISTS hotel_hotellongitude_index;
DROP INDEX IF EXISTS hotel_dateestablished_index;
DROP INDEX IF EXISTS hotel_manageruserid_index;

DROP INDEX IF EXISTS room_hotelid_index;
DROP INDEX IF EXISTS room_roomnumber_index;
DROP INDEX IF EXISTS room_price_index;
DROP INDEX IF EXISTS room_imageurl_index;

DROP INDEX IF EXISTS maintenancecompany_companyid_index;
DROP INDEX IF EXISTS maintenancecompany_name_index;
DROP INDEX IF EXISTS maintenancecompany_address_index;

DROP INDEX IF EXISTS roombooking_bookingid_index;
DROP INDEX IF EXISTS roombooking_customerid_index;
DROP INDEX IF EXISTS roombooking_hotelid_index;
DROP INDEX IF EXISTS roombooking_roomnumber_index;
DROP INDEX IF EXISTS roombooking_bookingdate_index;

DROP INDEX IF EXISTS roomrepair_repairid_index;
DROP INDEX IF EXISTS roomrepair_companyid_index;
DROP INDEX IF EXISTS roomrepair_hotelid_index;
DROP INDEX IF EXISTS roomrepair_roomnumber_index;
DROP INDEX IF EXISTS roomrepair_repairdate_index;

DROP INDEX IF EXISTS roomrepairrequest_requestnumber_index;
DROP INDEX IF EXISTS roomrepairrequest_managerid_index;
DROP INDEX IF EXISTS roomrepairrequest_repairid_index;

DROP INDEX IF EXISTS roomupdatelog_updatenumber_index;
DROP INDEX IF EXISTS roomupdatelog_managerid_index;
DROP INDEX IF EXISTS roomupdatelog_hotelid_index;
DROP INDEX IF EXISTS roomupdatelog_roomnumber_index;
DROP INDEX IF EXISTS roomupdatelog_updateon_index;

/* Users 
Index is needed here due to the amount of potential user we can have
Since there is no limit to how much we can have, index would be faster
then query engine
*/
CREATE INDEX user_userid_index ON Users USING btree(userID);
CREATE INDEX user_name_index ON Users USING btree(name);
CREATE INDEX user_password_index ON Users USING btree(password);
CREATE INDEX user_usertype_index ON Users USING btree(userType);

/* Hotel 
Since this is being read a lot of times, using index will speed up
any read queries. 
*/
CREATE INDEX hotel_hotelid_index ON Hotel USING btree(hotelID);
CREATE INDEX hotel_hotelname_index ON Hotel USING btree(hotelName);
CREATE INDEX hotel_hotellatitude_index ON Hotel USING btree(latitude);
CREATE INDEX hotel_hotellongitude_index ON Hotel USING btree(longitude);
CREATE INDEX hotel_dateestablished_index ON Hotel USING btree(dateEstablished);
CREATE INDEX hotel_manageruserid_index ON Hotel USING btree(managerUserID);

/* Rooms 
Index would not require here since the room for each hotel is finite.
The size of hotels will not increase and room as well.
The query engine will be faster if it scans the table rather than
loading up the index, reading, processing, etc
CREATE INDEX room_hotelid_index ON Rooms USING btree(hotelID);
CREATE INDEX room_roomnumber_index ON Rooms USING btree(roomNumber);
CREATE INDEX room_price_index ON Rooms USING btree(price);
CREATE INDEX room_imageurl_index ON Rooms USING btree(imageURL);
*/
/* Maintenance Company
There is no reason to index here. Faster to query than index. 
CREATE INDEX maintenancecompany_companyid_index ON MaintenanceCompany USING btree(companyID);
CREATE INDEX maintenancecompany_name_index ON MaintenanceCompany USING btree(name);
CREATE INDEX maintenancecompany_address_index ON MaintenanceCompany USING btree(addrress);
*/

/* Room Bookings 
Index is needed here due to the amount of potential rooms that can be booked
Since there is no limit to how much we can have, index would be faster
then query engine. It useful when looking for dates, index will be much faster
*/
CREATE INDEX roombooking_bookingid_index ON RoomBookings USING btree(bookingID);
CREATE INDEX roombooking_customerid_index ON RoomBookings USING btree(customerID);
CREATE INDEX roombooking_hotelid_index ON RoomBookings USING btree(hotelID);
CREATE INDEX roombooking_roomnumber_index ON RoomBookings USING btree(roomNumber);
CREATE INDEX roombooking_bookingdate_index ON RoomBookings USING btree(bookingDate);

/* Room Repair 
Index will not be used
CREATE INDEX roomrepair_repairid_index ON RoomRepairs USING btree(repairID);
CREATE INDEX roomrepair_companyid_index ON RoomRepairs USING btree(companyID);
CREATE INDEX roomrepair_hotelid_index ON RoomRepairs USING btree(hotelID);
CREATE INDEX roomrepair_roomnumber_index ON RoomRepairs USING btree(roomNumber);
CREATE INDEX roomrepair_repairdate_index ON RoomRepairs USING btree(repairDate);
*/

/* Room Repair Request 
Index will not be used
CREATE INDEX roomrepairrequest_requestnumber_index ON RoomRepairRequests USING btree(requestNumber);
CREATE INDEX roomrepairrequest_managerid_index ON RoomRepairRequests USING btree(managerID);
CREATE INDEX roomrepairrequest_repairid_index ON RoomRepairRequests USING btree(repairID);
*/

/* Room Update Log */
CREATE INDEX roomupdatelog_updatenumber_index ON RoomUpdatesLog USING btree(updateNumber);
CREATE INDEX roomupdatelog_managerid_index ON RoomUpdatesLog USING btree(managerID);
CREATE INDEX roomupdatelog_hotelid_index ON RoomUpdatesLog USING btree(hotelID);
CREATE INDEX roomupdatelog_roomnumber_index ON RoomUpdatesLog USING btree(roomNumber);
CREATE INDEX roomupdatelog_updateon_index ON RoomUpdatesLog USING btree(updatedOn);

