package edu.drexel.trainsim.user.commands;

import com.google.inject.Inject;

import edu.drexel.trainsim.user.models.User;
import org.sql2o.Connection;
import org.sql2o.Sql2o;

import java.util.List;

public class GetOrCreateGoogleUserImpl implements GetOrCreateGoogleUser {
    private final Sql2o db;

    @Inject
    public GetOrCreateGoogleUserImpl(Sql2o db) {
        this.db = db;
    }

    @Override
    public User call(String email, String type) {
        String sql = "SELECT id, email, type FROM users WHERE email = :email";
        String insertSql = "INSERT INTO users(email, type) VALUES(:email, :type)";

        try (Connection con = this.db.open()) {
            List<User> res = con.createQuery(sql).addParameter("email", email).executeAndFetch(User.class);

            // There is a race condition here if we have more than one servers talking to the db.
            if (res.isEmpty()) {
                con.createQuery(insertSql).addParameter("email", email).addParameter("type", type == null ? "Registered" : type).executeUpdate();
                res = con.createQuery(sql).addParameter("email", email).executeAndFetch(User.class);
            }

            return res.get(0);
        }
    }
}
