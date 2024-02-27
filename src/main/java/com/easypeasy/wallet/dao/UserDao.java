package com.easypeasy.wallet.dao;

import com.easypeasy.wallet.model.UserProfile;

public interface UserDao {
    UserProfile getUserProfile(String id);
}
