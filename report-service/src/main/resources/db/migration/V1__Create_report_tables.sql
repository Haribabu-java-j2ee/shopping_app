-- Report Service Database Schema
-- Read-optimized tables for reporting (CQRS pattern)

-- Sales summary table (materialized view concept)
CREATE TABLE sales_summary (
    id BIGSERIAL PRIMARY KEY,
    report_date DATE NOT NULL,
    total_orders INTEGER DEFAULT 0,
    total_revenue DECIMAL(12, 2) DEFAULT 0.00,
    average_order_value DECIMAL(10, 2) DEFAULT 0.00,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Product sales table
CREATE TABLE product_sales (
    id BIGSERIAL PRIMARY KEY,
    product_id VARCHAR(50) NOT NULL,
    product_name VARCHAR(200) NOT NULL,
    units_sold INTEGER DEFAULT 0,
    total_revenue DECIMAL(12, 2) DEFAULT 0.00,
    report_date DATE NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Indexes
CREATE INDEX idx_report_date ON sales_summary(report_date);
CREATE INDEX idx_product_report_date ON product_sales(product_id, report_date);

COMMENT ON TABLE sales_summary IS 'Aggregated sales data for reporting (CQRS read model)';
COMMENT ON TABLE product_sales IS 'Product-level sales analytics';





