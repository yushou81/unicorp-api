-- 用户测试数据
INSERT INTO user (id, username, password, nickname, role, status, is_deleted, create_time, update_time) 
VALUES 
(1, 'testuser1', '$2a$10$1sDFTz6rpeF3z4RdYFJZ0.xVMiY0U2PJNCRsHMG.Jeh0MQ1t.jdKe', '测试用户1', 'USER', 0, 0, NOW(), NOW()),
(2, 'testmerchant1', '$2a$10$1sDFTz6rpeF3z4RdYFJZ0.xVMiY0U2PJNCRsHMG.Jeh0MQ1t.jdKe', '测试商户1', 'MERCHANT', 0, 0, NOW(), NOW()),
(3, 'admin', '$2a$10$1sDFTz6rpeF3z4RdYFJZ0.xVMiY0U2PJNCRsHMG.Jeh0MQ1t.jdKe', '管理员', 'ADMIN', 0, 0, NOW(), NOW()),
(4, 'deleted_user', '$2a$10$1sDFTz6rpeF3z4RdYFJZ0.xVMiY0U2PJNCRsHMG.Jeh0MQ1t.jdKe', '已删除用户', 'USER', 0, 1, NOW(), NOW());

-- 商户测试数据
INSERT INTO merchant (id, user_id, name, address, phone, description, logo_url, business_hours, average_rating, status, is_deleted, create_time, update_time)
VALUES
(1, 2, '测试商户1', '内江市东兴区测试地址1', '13800138001', '这是一个测试商户1', 'https://example.com/logo1.jpg', '09:00-22:00', 4.5, 'OPEN', 0, NOW(), NOW()),
(2, 3, '测试商户2', '内江市市中区测试地址2', '13800138002', '这是一个测试商户2', 'https://example.com/logo2.jpg', '10:00-21:00', 3.8, 'OPEN', 0, NOW(), NOW()),
(3, 2, '已关闭商户', '内江市东兴区测试地址3', '13800138003', '这是一个已关闭的商户', 'https://example.com/logo3.jpg', '09:00-20:00', 4.0, 'CLOSED', 0, NOW(), NOW()),
(4, 2, '已删除商户', '内江市东兴区测试地址4', '13800138004', '这是一个已删除的商户', 'https://example.com/logo4.jpg', '08:00-20:00', 4.2, 'OPEN', 1, NOW(), NOW());

-- 产品测试数据
INSERT INTO product (id, merchant_id, name, description, price, image_url, stock, sales_count, status, is_deleted, create_time, update_time)
VALUES
(1, 1, '测试产品1', '这是商户1的测试产品1', 29.90, 'https://example.com/product1.jpg', 100, 50, 'ON_SALE', 0, NOW(), NOW()),
(2, 1, '测试产品2', '这是商户1的测试产品2', 19.90, 'https://example.com/product2.jpg', 200, 30, 'ON_SALE', 0, NOW(), NOW()),
(3, 1, '售罄产品', '这是商户1的已售罄产品', 39.90, 'https://example.com/product3.jpg', 0, 150, 'SOLD_OUT', 0, NOW(), NOW()),
(4, 1, '已下架产品', '这是商户1的已下架产品', 49.90, 'https://example.com/product4.jpg', 50, 10, 'OFF_SHELF', 0, NOW(), NOW()),
(5, 1, '已删除产品', '这是商户1的已删除产品', 59.90, 'https://example.com/product5.jpg', 80, 20, 'ON_SALE', 1, NOW(), NOW()),
(6, 2, '商户2产品1', '这是商户2的测试产品1', 69.90, 'https://example.com/product6.jpg', 150, 60, 'ON_SALE', 0, NOW(), NOW()),
(7, 2, '商户2产品2', '这是商户2的测试产品2', 79.90, 'https://example.com/product7.jpg', 120, 40, 'ON_SALE', 0, NOW(), NOW()); 