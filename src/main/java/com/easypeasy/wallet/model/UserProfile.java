package com.easypeasy.wallet.model;

import java.util.Set;

public record UserProfile(String userId, String email, Set<Long> wallets, boolean premiumStatus) { }
