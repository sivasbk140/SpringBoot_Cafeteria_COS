-- Drop the old status check constraint
ALTER TABLE order_table DROP CONSTRAINT IF EXISTS order_table_status_check;

-- Add new constraint with updated status values
ALTER TABLE order_table ADD CONSTRAINT order_table_status_check
    CHECK (status IN (
        'PLACED_ORDER',
        'ORDER_CONFIRMED',
        'ORDER_PREPARING',
        'ASSIGNED_DELIVERY_STAFF',
        'ORDER_DELIVERED',
        'ORDER_CANCELLED'
    ));
