package edu.drexel.trainsim.user.commands;

import edu.drexel.trainsim.user.models.User;

@FunctionalInterface
public interface GetOrCreateGoogleUser {
    User call(String email, String type);
}
