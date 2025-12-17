CREATE UNIQUE INDEX IF NOT EXISTS uk_hold_active_one_per_auction
    ON p_wallet_transaction (auction_id)
    WHERE transaction_type = 'HOLD'
    AND hold_status = 'HOLD_ACTIVE';