package com.easypeasy.wallet.exchange.dao;

import com.easypeasy.wallet.exchange.model.UserProfile;

public interface UserDao {
    UserProfile getUserProfile(String id);
}
