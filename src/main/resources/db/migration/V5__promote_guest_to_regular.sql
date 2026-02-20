-- 기존 GUEST 사용자를 REGULAR로 승급
UPDATE member SET role = 'REGULAR' WHERE role = 'GUEST';
