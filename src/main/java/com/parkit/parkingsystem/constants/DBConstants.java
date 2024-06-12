package com.parkit.parkingsystem.constants;

public class DBConstants {

    /* Parking */

    public static final String GET_NEXT_PARKING_SPOT = """
            SELECT MIN(parking_number) FROM parking
            WHERE available = TRUE AND type = ?""";

    public static final String UPDATE_PARKING_SPOT = """
            UPDATE parking SET available = ?
            WHERE parking_number = ?""";

    /* Ticket */

    public static final String SAVE_TICKET = """
            INSERT INTO ticket(parking_number, vehicle_reg_number, price, in_time, out_time) VALUES(?, ?, ?, ?, ?)""";

    public static final String UPDATE_TICKET = """
            UPDATE ticket SET price = ?, out_time = ? WHERE id = ?""";

    public static final String GET_TICKET = """
            SELECT t.parking_number, t.id, t.price, t.in_time, t.out_time, p.type FROM ticket t, parking p
            WHERE p.parking_number = t.parking_number AND t.vehicle_reg_number = ?
            ORDER BY t.in_time DESC LIMIT 1""";

    public static final String GET_NB_TICKET = """
            SELECT COUNT(out_time) FROM ticket
            WHERE vehicle_reg_number = ?""";

}
